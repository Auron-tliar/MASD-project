package Adventurers;

import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.annotation.PlanBody;
import jadex.bdiv3.annotation.PlanCapability;
import jadex.commons.future.IFuture;

@Plan
public class TryToRetirePlan
{
	@PlanCapability
	protected AdventurerBDI capa;
	
	//@PlanAPI
	//protected IPlan rplan;
	
	//@PlanReason
	//protected PayExpences goal;
	
	@PlanBody
	public IFuture<Void> body()
	{
		
		
		return IFuture.DONE;
	}
}
