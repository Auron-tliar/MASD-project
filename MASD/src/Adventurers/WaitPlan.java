package Adventurers;

import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.annotation.PlanBody;
import jadex.bdiv3.annotation.PlanCapability;
import jadex.bdiv3.annotation.PlanContextCondition;
import jadex.bdiv3.runtime.IPlan;
import jadex.bdiv3.runtime.impl.PlanFailureException;
import jadex.commons.future.IFuture;

@Plan
public class WaitPlan 
{
	@PlanCapability
	protected AdventurerBDI capa;
	
	@PlanContextCondition(beliefs="announcedAuction")
	public Boolean checkContext()
	{
		return (capa.announcedAuction != null);
	}
	
	@PlanBody
	public IFuture<Void> body(IPlan plan)
	{
		System.out.println("111");
		plan.waitForFactChanged("announcedAuction").get();

		System.out.println("222");
		
		//throw new PlanFailureException();
		
		return IFuture.DONE;
	}
}
