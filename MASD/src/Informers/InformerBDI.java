package Informers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import Adventurers.AdventurerBDI.AcquireQuestGoal;
import Adventurers.AdventurerBDI.DoQuestGoal;
import Adventurers.AdventurerBDI.FormGroupGoal;
import Adventurers.AdventurerBDI.ImproveGoal;
import Common.IExpensesAgent;
import Common.Quest;
import Utilities.IMessageService;
import Utilities.Message;
import jadex.bdiv3.annotation.Belief;
import jadex.bdiv3.annotation.Deliberation;
import jadex.bdiv3.annotation.Goal;
import jadex.bdiv3.annotation.GoalMaintainCondition;
import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.annotation.PlanAborted;
import jadex.bdiv3.annotation.PlanBody;
import jadex.bdiv3.annotation.PlanFailed;
import jadex.bdiv3.annotation.PlanPassed;
import jadex.bdiv3.annotation.Trigger;
import jadex.bdiv3.features.IBDIAgentFeature;
import jadex.bdiv3.model.MProcessableElement.ExcludeMode;
import jadex.bdiv3.runtime.ChangeEvent;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.commons.future.IFuture;
import jadex.commons.future.ISubscriptionIntermediateFuture;
import jadex.commons.future.IntermediateDefaultResultListener;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentArgument;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.AgentFeature;
import jadex.micro.annotation.AgentKilled;
import jadex.micro.annotation.Description;
import jadex.rules.eca.ChangeInfo;

@Agent
@Description("The informer agent")
public class InformerBDI implements IExpensesAgent {

	@AgentFeature 
	protected IBDIAgentFeature bdiFeature;

	@AgentFeature
	protected IRequiredServicesFeature requiredServicesFeature;
	
	@AgentArgument
	protected String name;

	@AgentArgument
	protected String id;

	public String getName()
	{
		return name;
	}
	
	protected IMessageService messageServer;

	protected long paymentInterval = 30000;
	
	protected Integer paymentAmount = 1;
	
	@Belief
	protected int currentGold;

	public Integer getPaymentAmount() {
		return this.paymentAmount;
	}

	public Integer getCurrentGold() {
		return this.currentGold;
	}
	
	@Belief(updaterate=1000)
	protected long currentTime = System.currentTimeMillis();

	@Belief
	protected long nextPayment;
	
	@Belief
	protected List<String> rawQuest;
	
	public Integer payGold(Integer gold)
	{
		//System.out.println("Paying gold.");
		if (currentGold < gold)
		{
			return -1;
		}
		else
		{
			currentGold -= gold;
			return gold;
		}
	}
	
	public void receiveGold(Integer gold)
	{
		System.out.println("Informer " + name + ": Receiving " + gold + " gold.");
		currentGold += gold;
	}
	
	public IFuture<Void> setNextPayment()
	{
		nextPayment += paymentInterval;
		
		return IFuture.DONE;
	}

	public void Die() {
		messageServer.send(new Message(id, "Overseer", Message.Performatives.request, "Remove", "", false));
	}
	
	@AgentBody
	public void body()
	{
		IFuture<Object> temp = requiredServicesFeature.getRequiredService("messageServer");
		messageServer = (IMessageService)temp.get();
		
		final ISubscriptionIntermediateFuture<Message> fut = messageServer.subscribe(name);
		
		/// Change Adventurers to other types ///
		final ISubscriptionIntermediateFuture<Message> futType = messageServer.subscribeType("Adventurers");
		
		fut.addResultListener(new IntermediateDefaultResultListener<Message>()
		{
			public void intermediateResultAvailable(Message msg)
			{
				System.out.println(msg);
			}
		});
		
		futType.addResultListener(new IntermediateDefaultResultListener<Message>()
		{
			public void intermediateResultAvailable(Message msg)
			{
				System.out.println(msg);
			}
		});
		
		
		/// Set the timer to count when to pay for life expenses
		nextPayment = System.currentTimeMillis() + paymentInterval; 

		/// Generate a small current gold amount
		currentGold = ThreadLocalRandom.current().nextInt(5, 10 + 1);
	}
	
	@AgentKilled
	public void shutdown()
	{
		
	}
	
	@Goal(deliberation=@Deliberation(inhibits={SellQuestGoal.class}), excludemode=ExcludeMode.Never, unique=true)
	public class PayExpensesGoal
	{
		@GoalMaintainCondition(beliefs= {"currentTime","nextPayment"})
		public Boolean checkMaintain()
		{
			return currentTime < nextPayment;
		}
	}
	
	@Goal
	public class SellQuestGoal {
		protected Quest quest;
		protected int price;
		protected String buyer; // Change type later
		
		public SellQuestGoal(Quest quest) {
			
		}
		
		public Quest getQuest() {
			return this.quest;
		}
		
		// Set after the sale? 
		public void setPrice(int price) {
			this.price = price;
		}

		public int getPrice() {
			return this.price;
		}
		
	}
	
	@Goal
	public class FindRawQuest {
		
	}
	
	@Plan(trigger=@Trigger(factaddeds="rawQuest"))
	public void receiveRawQuestPlan(ChangeEvent event)
	{
	    ChangeInfo<String> change = ((ChangeInfo<String>)event.getValue());
	}
	
	@Plan
	public class ProcessQuestPlan
	{
	
	  public ProcessQuestPlan()
	  {
	  }
	
	  @PlanBody
	  public void processQuest()
	  {
		  // Process
	  }
	  
	  @PlanPassed
	  public void passed()
	  {
	    System.out.println("Processing quest finished successfully.");
	  }

	  @PlanAborted
	  public void aborted()
	  {
	    System.out.println("Processing quest aborted.");
	  }

	  @PlanFailed
	  public void failed(Exception e)
	  {
	    System.out.println("Processing quest failed: "+e);
	  }
	}
	
	@Plan
	public class SellQuestPlan
	{
	
	  public SellQuestPlan()
	  {
		  // Get an auctioneer
	  }
	
	  @PlanBody
	  public void processQuest()
	  {
		  // Process
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
}
