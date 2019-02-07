package Adventurers;

import Utilities.Message;
import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.annotation.PlanBody;
import jadex.bdiv3.annotation.PlanCapability;
import jadex.bdiv3.annotation.PlanPrecondition;
import jadex.bdiv3.runtime.IPlan;
import jadex.commons.future.IFuture;

@Plan
public class ResAuctionCollectPlan
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
		if (msg.getProtocol() == "Auction" && msg.getPerformative() == Message.Performatives.payment)
		{
			return true;
		}

		return false;
	}
	
	@PlanBody
	public IFuture<Void> body(IPlan plan, Message msg)
	{
		capa.leCapability.receiveGold((Double)msg.getContent());
		
		return IFuture.DONE;
	}
}
