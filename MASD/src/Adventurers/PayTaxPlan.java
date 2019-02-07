package Adventurers;

import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.annotation.PlanBody;
import jadex.bdiv3.annotation.PlanCapability;
import jadex.commons.future.IFuture;

@Plan
public class PayTaxPlan
{
	@PlanCapability
	protected AdventurerBDI capa;
	
	@PlanBody
	public IFuture<Void> body(Double taxAmount)
	{
		if (taxAmount == 0)
		{
			return IFuture.DONE;
		}
		
		capa.payTax(taxAmount);
		
		return IFuture.DONE;
	}
}
