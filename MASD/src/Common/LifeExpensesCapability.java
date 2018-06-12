package Common;

import Adventurers.AdventurerBDI.AcquireQuestGoal;
import Adventurers.AdventurerBDI.DoQuestGoal;
import Adventurers.AdventurerBDI.FormGroupGoal;
import Adventurers.AdventurerBDI.ImproveGoal;
import Utilities.IMessageService;
import Utilities.Message;
import jadex.bdiv3.annotation.Belief;
import jadex.bdiv3.annotation.Body;
import jadex.bdiv3.annotation.Capability;
import jadex.bdiv3.annotation.Deliberation;
import jadex.bdiv3.annotation.Goal;
import jadex.bdiv3.annotation.GoalMaintainCondition;
import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.annotation.PlanBody;
import jadex.bdiv3.annotation.PlanCapability;
import jadex.bdiv3.annotation.Plans;
import jadex.bdiv3.annotation.Trigger;
import jadex.bdiv3.features.IBDIAgentFeature;
import jadex.bdiv3.model.MProcessableElement.ExcludeMode;
import jadex.commons.future.IFuture;

@Capability
public class LifeExpensesCapability 
{
	@Belief(updaterate=100)
	protected long currentTime = System.currentTimeMillis();
	
	@Belief
	protected long nextPayment;

	protected Integer currentGold;
	
	@Belief
	public Integer getCurrentGold()
	{
		return currentGold;
	}
	
	@Belief
	public void setCurrentGold(Integer currentGold)
	{
		this.currentGold = currentGold;
	}
	
	protected PayExpensesGoal payExpensesGoal;
	
	
	protected String name;
	protected String id;
	
	protected Boolean dying = false;
	
	@Belief
	public Boolean getDying()
	{
		return dying;
	}
	
	@Belief
	public void setDying(Boolean dying)
	{
		this.dying = dying; 
	}
	
	protected long paymentInterval = 3000;
	protected Integer paymentAmount = 3;
	
	public Integer getPaymentAmount()
	{
		return paymentAmount;
	}
	
	public IFuture<Void> setNextPayment()
	{
		nextPayment += paymentInterval;
		
		return IFuture.DONE;
	}
	
	/*public void dispatchPayLifeExpensesGoal()
	{
		
	}*/
	
	public LifeExpensesCapability() {}
	
	public IFuture<Void> initialize(String name, String id,	Integer startGold)
	{
		this.name = name;
		this.id = id;
		this.currentGold = startGold;
		
		nextPayment = System.currentTimeMillis() + paymentInterval;
		currentTime = System.currentTimeMillis();
		
		return IFuture.DONE;
	}
	
	/*public LifeExpensesCapability(String name, String id, IMessageService messageServer, Integer startGold)
	{
		this.name = name;
		this.id = id;
		this.messageServer = messageServer;
		this.currentGold = startGold;
		
		nextPayment = System.currentTimeMillis() + paymentInterval;
		currentTime = System.currentTimeMillis();
	}*/
	
	/*public void Die()
	{
		bdiFeature.dropGoal(payExpensesGoal);
		messageServer.send(new Message(id, "Overseer", Message.Performatives.inform, "", "Death", false));
	}*/

	
	public Integer payGold(Integer gold)
	{
		//System.out.println("Adventurer " + name +  ": Paying " + gold + " gold.");
		if (currentGold < gold)
		{
			return -1;
		}
		else
		{
			currentGold -= gold;
			setCurrentGold(currentGold - gold);
			return gold;
		}
	}
	
	public void receiveGold(Integer gold)
	{
		//System.out.println("Adventurer " + name +  ": Receiving " + gold + " gold.");
		//currentGold += gold;
		setCurrentGold(currentGold + gold);
	}
	
	@Goal(deliberation=@Deliberation(inhibits={AcquireQuestGoal.class, DoQuestGoal.class, FormGroupGoal.class,
			ImproveGoal.class}), excludemode=ExcludeMode.Never, unique=true)
	public class PayExpensesGoal
	{
		@GoalMaintainCondition(beliefs= {"currentTime","nextPayment"})
		public Boolean checkMaintain()
		{
			return currentTime < nextPayment;
		}
		
		/*@GoalTargetCondition()
		public Boolean targetMaintain()
		{
			return currentTime < nextPayment;
		}*/
	}
	
	@Plan(trigger=@Trigger(goals=PayExpensesGoal.class))
	public class PayExpensesPlan 
	{
		
		//@PlanAPI
		//protected IPlan rplan;
		
		//@PlanReason
		//protected Common.PayExpensesGoal goal;
		
		@PlanBody
		public IFuture<Void> body()
		{
			if (payGold(paymentAmount) == -1)
			{
				System.out.println("<" +name + "> Not enough gold to pay life expenses, dying...");
				setDying(true);
				return IFuture.DONE;
			}
			
			System.out.println("<" + name + "> Life expenses: " + paymentAmount + 
					", " + currentGold + " gold left.");
			
			setNextPayment().get();
			
			return IFuture.DONE;
		}
	}
}
