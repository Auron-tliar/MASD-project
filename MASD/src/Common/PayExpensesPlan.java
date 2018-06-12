package Common;

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
	protected IExpensesAgent capa;
	
	//@PlanAPI
	//protected IPlan rplan;
	
	//@PlanReason
	//protected PayExpences goal;
	
	@PlanBody
	public IFuture<Void> body()
	{
		Integer paymentAmount = capa.getPaymentAmount();
		String name = capa.getName();
		if (capa.payGold(paymentAmount) == -1)
		{
			System.out.println("<" + name + "> Not enough gold to pay life expenses, dying...");
			capa.Die();
			return IFuture.DONE;
		}
		
		System.out.println("<" + name + "> Life expenses: " + paymentAmount + 
				", " + capa.getCurrentGold() + " gold left.");
		
		capa.setNextPayment().get();
		
		return IFuture.DONE;
	}
}
