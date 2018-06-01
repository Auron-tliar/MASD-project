package Informers;

import jadex.bdiv3.annotation.Goal;

@Goal(recur=true)
public class MaintainGoldGoal {
	
	protected int goldAmount;
	
	public MaintainGoldGoal() {
		
	}
	
	public int getGoldAmount() {
		return goldAmount;
	}
	
	public void setGoldAmount(int goldAmount) {
		this.goldAmount = goldAmount;
	}
}
