package Adventurers;

import Utilities.Message;
import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.annotation.PlanBody;
import jadex.bdiv3.annotation.PlanCapability;
import jadex.bdiv3.annotation.PlanPrecondition;
import jadex.bdiv3.runtime.IPlan;
import jadex.commons.future.IFuture;
import jadex.commons.Tuple2;

@Plan
public class ResMinProfitRequestPlan
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

		if (msg.getProtocol() == "FormGroup" && msg.getPerformative() == Message.Performatives.request)
		{
			return true;
		}

		return false;
	}
	
	@PlanBody
	public IFuture<Void> body(IPlan plan, Message msg)
	{
		Double min =((capa.announcedAuction.getStartTime()-capa.lastQuestTime + capa.announcedAuction.getRequirements().total()*1000) * 
				(1.4 - 0.1*capa.riskLevel)) / capa.leCapability.getPaymentInterval() * capa.leCapability.getPaymentAmount();
		Double max = (capa.leCapability.getCurrentGold() - (5-capa.riskLevel + capa.announcedAuction.getRequirements().total()*1000 * 
				(1.4 - 0.1*capa.riskLevel) / capa.leCapability.getPaymentInterval())*capa.leCapability.getPaymentAmount() );
		
		capa.messageServer.send(new Message(capa.id, capa.groupLeader, Message.Performatives.inform, 
				new Tuple2<Integer, Integer>(min.intValue(), max.intValue()), "FormGroup", false));
		
		return IFuture.DONE;
	}
}
