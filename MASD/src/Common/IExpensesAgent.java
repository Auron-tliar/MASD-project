package Common;

import jadex.commons.future.IFuture;

public interface IExpensesAgent {

	public void Die();
	
	public Integer payGold(Integer gold);

	public IFuture<Void> setNextPayment();
	
	public Integer getPaymentAmount();
	
	public String getName();
	
	public Integer getCurrentGold();
}
