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
public class ResAdventurerDescriptionPlan 
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
		
		if (capa.collecting && msg.getProtocol() == "FormGroup" && msg.getPerformative() == Message.Performatives.inform)
		{
			return true;
		}

		return false;
	}
	
	@PlanBody
	public IFuture<Void> body(IPlan plan, Message msg)
	{
		capa.lfgAdventurers.put(msg.getSender(), ((Tuple2<List<String>, Attributes>)msg.getContent()).getSecondEntity());
		
		return IFuture.DONE;
	}
}
