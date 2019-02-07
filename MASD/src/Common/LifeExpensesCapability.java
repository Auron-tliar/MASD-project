package Common;

import Adventurers.AdventurerBDI;
import Adventurers.AdventurerBDI.AcquireQuestGoal;
import Adventurers.AdventurerBDI.DoQuestGoal;
//import Adventurers.AdventurerBDI.FormGroupGoal;
import Adventurers.AdventurerBDI.ImproveGoal;
import Utilities.IMessageService;
import Utilities.Message;
import jadex.bdiv3.annotation.Belief;
import jadex.bdiv3.annotation.Body;
import jadex.bdiv3.annotation.Capability;
import jadex.bdiv3.annotation.Deliberation;
import jadex.bdiv3.annotation.Goal;
import jadex.bdiv3.annotation.GoalMaintainCondition;
import jadex.bdiv3.annotation.GoalParameter;
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

	@Belief
	protected Double currentGold;

	@Belief(dynamic=true)
	public void setCurrentGold(Double currentGold)
	{
		this.currentGold = currentGold;
	}
	
	@Belief(dynamic=true)
	public Double getCurrentGold()
	{
		return currentGold;
	}
	
	protected Double goldDelta = 0.0;
	
	public Double getGoldDelta()
	{
		return goldDelta;
	}

	public void setGoldDelta(Double goldDelta)
	{
		this.goldDelta = goldDelta;
	}

	protected PayExpensesGoal payExpensesGoal;
	
	
	protected String name;
	protected String id;
	
	@Belief
	protected Boolean dying = false;
	
	@Belief(dynamic=true)
	public Boolean getDying()
	{
		return dying;
	}
	
	@Belief(dynamic=true)
	public void setDying(Boolean dying)
	{
		this.dying = dying; 
	}
	
	protected long paymentInterval = 10000;
	protected Double paymentAmount = 2.0;
	
	public long getPaymentInterval()
	{
		return paymentInterval;
	}
	
	public Double getPaymentAmount()
	{
		return paymentAmount;
	}
	
	public IFuture<Void> setNextPayment()
	{
		nextPayment += paymentInterval;
		
		return IFuture.DONE;
	}
	
	public LifeExpensesCapability() {}
	
	public IFuture<Void> initialize(String name, String id,	Double startGold)
	{
		this.name = name;
		this.id = id;
		currentGold = startGold;
		//currentGold = startGold;
		nextPayment = System.currentTimeMillis() + paymentInterval;
		//currentTime = System.currentTimeMillis();
		
		return IFuture.DONE;
	}

	
	public Double payGold(Double gold)
	{
		//System.out.println("Adventurer " + name +  ": Paying " + gold + " gold (" + currentGold + ").");
		if (currentGold < gold)
		{
			return -1.0;
		}
		else
		{
			currentGold -= gold;
			goldDelta = -gold;
			return gold;
		}
	}
	
	public Double sendGold(Double gold)
	{
		//System.out.println("Adventurer " + name +  ": Paying " + gold + " gold (" + currentGold + ").");
		if (currentGold < gold)
		{
			return -1.0;
		}
		else
		{
			currentGold -= gold;
			goldDelta = 0.0;
			return gold;
		}
	}
	
	public void receiveGold(Double gold)
	{
		//System.out.println("Adventurer " + name +  ": Receiving " + gold + " gold.");
		currentGold += gold;
		goldDelta = gold;
	}
	
	@Goal(deliberation=@Deliberation(inhibits={ImproveGoal.class}), unique=true)//inhibits={DoQuestGoal.class, //FormGroupGoal.class,AcquireQuestGoal.class, 
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
	protected void payExpenses()
	{
		//System.out.println(capa.getCurrentGold());
		if (payGold(getPaymentAmount()) == -1.0)
		{
			System.out.println("<" +name + "> Not enough gold to pay life expenses, dying...");
			dying = true;
			return;// IFuture.DONE;
		}
		
		System.out.println("<" + name + "> Life expenses: " + paymentAmount + 
				", " + getCurrentGold() + " gold left.");
		
		setNextPayment().get();
		
		//return IFuture.DONE;
	}
	
	/*@Plan(trigger=@Trigger(goals=PayExpensesGoal.class))
	public class PayExpensesPlan 
	{
		//@PlanCapability
		//protected LifeExpensesCapability capa;
		
		//@PlanAPI
		//protected IPlan rplan;
		
		//@PlanReason
		//protected Common.PayExpensesGoal goal;
		
		@PlanBody
		public IFuture<Void> body(LifeExpensesCapability capa)
		{
			System.out.println(capa.getCurrentGold());
			//System.out.println(capa.getCurrentGold());
			if (payGold(getPaymentAmount()) == -1)
			{
				System.out.println("<" +name + "> Not enough gold to pay life expenses, dying...");
				setDying(true);
				return IFuture.DONE;
			}
			
			System.out.println("<" + name + "> Life expenses: " + paymentAmount + 
					", " + getCurrentGold() + " gold left.");
			
			setNextPayment().get();
			
			return IFuture.DONE;
		}
	}*/
}
