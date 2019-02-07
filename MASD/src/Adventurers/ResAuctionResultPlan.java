package Adventurers;

import Common.Quest;
import Utilities.Message;
import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.annotation.PlanBody;
import jadex.bdiv3.annotation.PlanCapability;
import jadex.bdiv3.annotation.PlanPrecondition;
import jadex.bdiv3.runtime.IPlan;
import jadex.commons.future.IFuture;

@Plan
public class ResAuctionResultPlan
{
	@PlanCapability
	protected AdventurerBDI capa;
	
	@PlanPrecondition
	public Boolean checkPreconditions(Message msg)
	{
		if (msg == null)
		{
			return false;
		}
		if (msg.getProtocol() == "Auction" && (msg.getPerformative() == Message.Performatives.accept ||
				msg.getPerformative() == Message.Performatives.reject || msg.getPerformative() == Message.Performatives.parcel) &&
				capa.auctioneerId == msg.getSender())
		{
			return true;
		}

		return false;
	}
	
	@PlanBody
	public IFuture<Void> body(IPlan plan, Message msg)
	{

		switch (msg.getPerformative())
		{
			case accept:
				if (capa.id == capa.groupLeader)
				{
					for (String id : capa.group)
					{
						if (id != capa.id)
						{
							capa.messageServer.send(new Message(capa.auctioneerId, id, Message.Performatives.accept, "",
									"Auction", false));
						}
					}
					
					while (capa.leCapability.getCurrentGold() < (Double)msg.getContent())
					{
						plan.waitForFactChanged("leCapability.currentGold").get();
					}
					
					capa.messageServer.send(new Message(capa.id, capa.auctioneerId, Message.Performatives.payment, 
							capa.leCapability.payGold((Double)msg.getContent()), "Auction", false));
				}
				else
				{
					capa.messageServer.send(new Message(capa.id, capa.groupLeader, Message.Performatives.payment,
							capa.leCapability.sendGold((Double)msg.getContent() / capa.group.size()), "Auction", false));
				}
				break;
			case reject:
				capa.announcedAuction = null;
				capa.doneFormation = true;
				capa.forming = false;
				capa.stopFormation = false;
				break;
				
			case parcel:
				for (String id : capa.group)
				{
					if (id != capa.id)
					{
						capa.messageServer.send(new Message(capa.auctioneerId, id, Message.Performatives.parcel, "", 
								"Auction", false));
					}
				}
				capa.currentQuest = (Quest)msg.getContent();
				plan.dispatchSubgoal(capa.new DoQuestGoal());
			default:
				break;
		}
		
		return IFuture.DONE;
	}
}

