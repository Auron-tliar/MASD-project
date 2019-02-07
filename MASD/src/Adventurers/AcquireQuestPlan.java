package Adventurers;

import Utilities.Message;
import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.annotation.PlanBody;
import jadex.bdiv3.annotation.PlanCapability;
import jadex.bdiv3.runtime.IPlan;
import jadex.commons.future.IFuture;
import jadex.rules.eca.ICondition;

@Plan
public class AcquireQuestPlan 
{
	@PlanCapability
	protected AdventurerBDI capa;
	
	@PlanBody
	public IFuture<Void> body(IPlan plan)
	{
		//if (System.currentTimeMillis() < capa.announcedAuction.getStartTime())
		//{
			capa.collectedResponces = 0;
			
			Double min =((capa.announcedAuction.getStartTime()-capa.lastQuestTime + capa.announcedAuction.getRequirements().total()*1000) * 
					(1.4 - 0.1*capa.riskLevel)) / capa.leCapability.getPaymentInterval() * capa.leCapability.getPaymentAmount();
			Double max = (capa.leCapability.getCurrentGold() - (5-capa.riskLevel + capa.announcedAuction.getRequirements().total()*1000 * 
					(1.4 - 0.1*capa.riskLevel) / capa.leCapability.getPaymentInterval())*capa.leCapability.getPaymentAmount() );
			
			capa.minProfit = min;
			capa.maxPrice = max;
			
			for (String id : capa.group)
			{
				if (id != capa.id)
				{
					capa.messageServer.send(new Message(capa.id, id, Message.Performatives.request, "", "FormGroup", false));
				}
			}
			
			System.out.println("Adventurer " + capa.name + " has determined his/her min reward (" + min + ") and max price " + 
					+ max + ") for the auction. Waiting for the auction to start...");
			
			/*System.out.println("Adv: Auction start time: " + capa.announcedAuction.getStartTime());
			
			System.out.println("Adv: Current time: " + System.currentTimeMillis());
			System.out.println("Adv: Wait time: " + (capa.announcedAuction.getStartTime() - System.currentTimeMillis()));*/
			
			plan.waitFor(capa.announcedAuction.getStartTime() - System.currentTimeMillis() + 1).get();
			
			//System.out.println(System.currentTimeMillis());
			
			//System.out.println("The wait is over!");
			
			while (capa.collectedResponces < capa.group.size() - 1)
			{
				plan.waitForFactChanged("collectedResponces").get();
			}
			Double nextBid = capa.announcedAuction.getStartCost();// + (capa.group.size() - capa.announcedAuction.getStartCost() % 
					//capa.group.size());
			if ((capa.announcedAuction.getReward() - nextBid) / capa.group.size() >= capa.minProfit &&
					nextBid / capa.group.size() < capa.maxPrice)
			{
				capa.messageServer.send(new Message(capa.id, capa.auctioneerId, Message.Performatives.propose, 
						nextBid, "Auction", false));

				capa.auctioning = true;
			}
			else
			{
				capa.readyToAuction = false;
			}
			
		//}
		
		return IFuture.DONE;
	}
}
