package Adventurers;

import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.annotation.PlanBody;
import jadex.bdiv3.annotation.PlanCapability;
import jadex.bdiv3.runtime.IPlan;
import jadex.commons.future.IFuture;

@Plan
public class DiePlan 
{
	@PlanCapability
	protected AdventurerBDI capa;
	
	@PlanBody
	public IFuture<Void> body(IPlan plan)
	{
		capa.Die();
		
		plan.waitFor(100000).get();
		
		return IFuture.DONE;
	}
}
