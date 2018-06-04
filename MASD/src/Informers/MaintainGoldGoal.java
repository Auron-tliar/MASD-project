package Informers;

import jadex.bdiv3.annotation.Goal;

@Goal(recur=true)
public class MaintainGoldGoal {
	
	protected int goldAmount;
	protected float decreaseRate;
	
	public MaintainGoldGoal(float decreaseRate) {
		this.decreaseRate = decreaseRate;
		this.goldAmount = 0;
	}
	
	public int getGoldAmount() {
		return goldAmount;
	}
	
	public void addGold(int goldAmount) {
		this.goldAmount += goldAmount;
	}
}
