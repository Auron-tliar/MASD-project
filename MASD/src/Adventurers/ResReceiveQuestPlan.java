package Adventurers;

import Utilities.Message;
import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.annotation.PlanBody;
import jadex.bdiv3.annotation.PlanPrecondition;
import jadex.bdiv3.runtime.IPlan;
import jadex.commons.future.IFuture;

@Plan
public class ResReceiveQuestPlan
{
	@PlanPrecondition
	public Boolean checkPreconditions(Message msg)
	{
		if (msg == null)
		{
			return false;
		}


		return false;
	}
	
	@PlanBody
	public IFuture<Void> body(IPlan plan)
	{

		System.out.println("No correct message handler found!");
		
		return IFuture.DONE;
	}
}
