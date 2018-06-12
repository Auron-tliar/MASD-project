package Adventurers;

import Common.Attributes;
import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.annotation.PlanAborted;
import jadex.bdiv3.annotation.PlanBody;
import jadex.bdiv3.annotation.PlanCapability;
import jadex.bdiv3.annotation.PlanFailed;
import jadex.bdiv3.annotation.PlanPassed;
import jadex.bdiv3.runtime.IPlan;
import jadex.commons.future.IFuture;

@Plan
public class TrainPlan
{
	@PlanCapability
	protected AdventurerBDI capa;
	
	@PlanBody
	public IFuture<Void> body(IPlan plan)
	{
		Integer attr = capa.attributes.lowest();
		System.out.println("Adventurer " + capa.name + " is training " + Attributes.getAttributeNames().get(attr) + "(" + 
				capa.attributes.Values.get(attr) + ")");
		
		for (int i = 0; i < 10; i++)
		{
			plan.waitFor(2000).get();
			capa.attributes.Values.set(attr, capa.attributes.Values.get(attr) + 0.1);
			capa.receiveGold(1);
			System.out.println("Round " + i);
		}
		
		return IFuture.DONE;
	}
	
	@PlanPassed
	public void passed()
	{
	  System.out.println("Plan finished successfully.");
	}

	@PlanAborted
	public void aborted()
	{
	  System.out.println("Plan aborted.");
	}

	@PlanFailed
	public void failed(Exception e)
	{
	  System.out.println("Plan failed: "+e);
	}
}
