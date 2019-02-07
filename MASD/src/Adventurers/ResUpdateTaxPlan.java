package Adventurers;

import Informers.Auction;
import Utilities.Message;
import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.annotation.PlanBody;
import jadex.bdiv3.annotation.PlanCapability;
import jadex.bdiv3.annotation.PlanPrecondition;
import jadex.bdiv3.runtime.IPlan;
import jadex.commons.future.IFuture;

@Plan
public class ResUpdateTaxPlan
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
		
		if (msg.getSender() == "Overseer" && msg.getPerformative() == Message.Performatives.inform && msg.getProtocol() == "Tax")
		{
			return true;
		}
		return false;
	}
	
	@PlanBody
	public IFuture<Void> body(IPlan plan, Message msg)
	{
		capa.taxPercentage = (Double)msg.getContent();
		
		return IFuture.DONE;
	}
}
