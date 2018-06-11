package Adventurers;

import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.annotation.PlanAPI;
import jadex.bdiv3.annotation.PlanBody;
import jadex.bdiv3.annotation.PlanCapability;
import jadex.bdiv3.annotation.PlanReason;
import jadex.bdiv3.runtime.IPlan;
import jadex.commons.future.IFuture;

@Plan
public class PayExpensesPlan 
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
		if (!capa.payGold(capa.getPaymentAmount()))
		{
			System.out.println("<" + capa.getName() + "> Not enough gold to pay life expenses, dying...");
			capa.Die();
			return IFuture.DONE;
		}
		
		System.out.println("<" + capa.getName() + "> Life expenses: " + capa.getPaymentAmount() + 
				", " + capa.getCurrentGold() + " gold left.");
		
		capa.setNextPayment().get();
		
		return IFuture.DONE;
	}
}
