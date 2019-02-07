package Adventurers;

import java.util.List;

import Common.Attributes;
import Utilities.Message;
import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.annotation.PlanBody;
import jadex.bdiv3.annotation.PlanCapability;
import jadex.bdiv3.annotation.PlanPrecondition;
import jadex.bdiv3.runtime.IPlan;
import jadex.commons.Tuple2;
import jadex.commons.future.IFuture;

@Plan
public class ResAdventurerProposalPlan
{
	@PlanCapability
	AdventurerBDI capa;
	
	@PlanPrecondition
	public Boolean checkPreconditions(Message msg)
	{
		if (msg == null)
		{
			return false;
		}
		
		if (!capa.doneFormation && msg.getProtocol() == "FormGroup" && msg.getPerformative() == Message.Performatives.propose)
		{
			return true;
		}


		return false;
	}
	
	@PlanBody
	public IFuture<Void> body(IPlan plan, Message msg)
	{
		if (capa.id != capa.groupLeader)
		{
			capa.messageServer.send(new Message(msg.getSender(), capa.groupLeader, msg.getPerformative(), 
					msg.getContent(), msg.getProtocol(), msg.getBroadcast()));
		}
		else if (capa.lfgAdventurers.containsKey(msg.getSender()))
		{
			for (Tuple2<String, Double> t : capa.advValuations)
			{
				if (t.getFirstEntity() == msg.getSender() && t.getSecondEntity() >= (0.1 * (1.0 + capa.riskLevel)))
				{
					capa.stopFormation = true;
					capa.messageServer.send(new Message(capa.id, msg.getSender(), Message.Performatives.agree,
							new Tuple2<List<String>, Attributes>(capa.group, capa.groupAtributes), "FormGroup", false));
				}
			}
		}
		
		return IFuture.DONE;
	}
}
