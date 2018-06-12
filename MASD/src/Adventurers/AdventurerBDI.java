package Adventurers;

import javafx.util.Pair;
import java.util.Collection;
import java.util.Hashtable;
import java.util.concurrent.ThreadLocalRandom;

import Informers.Auction;

import java.util.Iterator;
import java.util.Map;

import Utilities.IMessageService;
import Utilities.Message;
import jadex.bdiv3.annotation.Belief;
import jadex.bdiv3.annotation.Body;
import jadex.bdiv3.annotation.Deliberation;
import jadex.bdiv3.annotation.Goal;
import jadex.bdiv3.annotation.GoalCreationCondition;
import jadex.bdiv3.annotation.GoalMaintainCondition;
import jadex.bdiv3.annotation.GoalParameter;
import jadex.bdiv3.annotation.GoalRecurCondition;
import jadex.bdiv3.annotation.GoalTargetCondition;
import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.annotation.Plans;
import jadex.bdiv3.annotation.Trigger;
import jadex.bdiv3.features.IBDIAgentFeature;
import jadex.bdiv3.model.MProcessableElement.ExcludeMode;
import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IExecutionFeature;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.ISubscriptionIntermediateFuture;
import jadex.commons.future.IntermediateDefaultResultListener;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentArgument;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.AgentFeature;
import jadex.micro.annotation.Argument;
import jadex.micro.annotation.Arguments;
import jadex.micro.annotation.Binding;
import jadex.micro.annotation.Description;
import jadex.micro.annotation.RequiredService;
import jadex.micro.annotation.RequiredServices;

@Agent
@Description("Adventurers buy and do quests, increasing their attrbutes and buying equipment "
		+ "in the mean time.")
@RequiredServices(
{
	@RequiredService(name="messageServer", type=IMessageService.class, multiple=false,
	binding=@Binding(scope=RequiredServiceInfo.SCOPE_PLATFORM, dynamic=true))
})
@Arguments(
{
	@Argument(name="name", clazz=String.class, defaultvalue="\"default_name\""),
	@Argument(name="attributes", clazz=Common.Attributes.class),
	@Argument(name="id", clazz=String.class)
})
@Plans(
{
	@Plan(trigger=@Trigger(goals=AdventurerBDI.AcquireQuestGoal.class), body=@Body(AcquireQuestPlan.class)),
	@Plan(trigger=@Trigger(goals=AdventurerBDI.ConsiderOffer.class), body=@Body(ConsiderOfferPlan.class)),
	@Plan(trigger=@Trigger(goals=AdventurerBDI.DoQuestGoal.class), body=@Body(DoQuestPlan.class)),
	@Plan(trigger=@Trigger(goals=AdventurerBDI.FormGroupGoal.class), body=@Body(FormGroupPlan.class)),
	@Plan(trigger=@Trigger(goals=AdventurerBDI.ImproveGoal.class), body=@Body(AskForEquipmentPlan.class), priority=10),
	@Plan(trigger=@Trigger(goals=AdventurerBDI.ImproveGoal.class), body=@Body(TrainPlan.class), priority=0),
	@Plan(trigger=@Trigger(goals=AdventurerBDI.PayExpensesGoal.class), body=@Body(PayExpensesPlan.class)),
	@Plan(trigger=@Trigger(goals=AdventurerBDI.RetireGoal.class), body=@Body(RetirePlan.class))
})

public class AdventurerBDI 
{
	@Agent
	IInternalAccess agent;
	
	@AgentFeature 
	protected IBDIAgentFeature bdiFeature;
	
	@AgentFeature
	protected IRequiredServicesFeature requiredServicesFeature;
	
	@AgentArgument
	protected String name;
	
	@AgentArgument
	protected String id;
	
	protected PayExpensesGoal payExpensesGoal;
	protected ImproveGoal improveGoal;
	
	public String getName()
	{
		return name;
	}
	
	protected static Integer minRetireGold = 20;
	protected static Integer maxRetireGold = 50;
	
	protected long paymentInterval = 30000;
	
	protected Integer paymentAmount = 2;
	
	protected Integer riskLevel;
	
	/*public Integer getPaymentAmount()
	{
		//System.out.println("Getting payment info.");
		return paymentAmount;
	}*/
	
	protected IMessageService messageServer;
	
	
	/// Beliefs
	
	@AgentArgument
	@Belief
	protected Common.Attributes attributes;
	
	@Belief(updaterate=1000)
	protected long currentTime = System.currentTimeMillis();
	
	@Belief
	protected long nextPayment;
	
	@Belief
	protected Integer currentGold;
	
	@Belief
	protected Integer retirementGold;
	
	@Belief
	protected Boolean readyToRetire = false;
	
	@Belief
	protected Map<String, Auction> announcedAuctions;
	
	@Belief(updaterate=100)
	protected long timer100 = System.currentTimeMillis();
	
	
	
	

	/*public Integer getCurrentGold()
	{
		//System.out.println("Getting current gold.");
		return currentGold;
	}
	
	public Integer getRetirementGold()
	{
		return retirementGold;
	}
	
	public Map<String, Informers.Auction> getAnnouncedAuctions()
	{
		return announcedAuctions;
	}*/
	
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
		System.out.println("Adventurer " + name + ": Receiving " + gold + " gold.");
		currentGold += gold;
		
		if (currentGold >= retirementGold)
		{
			readyToRetire = true;
		}
	}
	
	public IFuture<Void> setNextPayment()
	{
		nextPayment += paymentInterval;
		
		return IFuture.DONE;
	}
	
	public void Die()
	{
		bdiFeature.dropGoal(payExpensesGoal);
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
		
		/// Generate the gold amount to retire
		retirementGold = ThreadLocalRandom.current().nextInt(minRetireGold, maxRetireGold + 1);
		
		/// Generate a small current gold amount
		currentGold = ThreadLocalRandom.current().nextInt(5, 10 + 1);
		
		announcedAuctions = new Hashtable<String, Auction>();
		
		riskLevel = ThreadLocalRandom.current().nextInt(1, 4);
		
		payExpensesGoal = new PayExpensesGoal();
		bdiFeature.dispatchTopLevelGoal(payExpensesGoal);
		improveGoal = new ImproveGoal();
		bdiFeature.dispatchTopLevelGoal(improveGoal);
		
		System.out.println(name);
		
		System.out.println("Adventurer " + name + " has arrived! Goal: " + retirementGold + ". Abilities:\n" + attributes.toString());
		
		
		// Uncomment for communication testing
		/*
		IComponentStep<Void> step = new IComponentStep<Void>()
		{
			final int[] cnt = new int[1];
			public IFuture<Void> execute(IInternalAccess ia)
			{
				messageServer.send(new Message(id, "Adventurers", Message.Performatives.inform,
						cnt[0], "Test protocol", true));//msg);
				if(cnt[0]<10)
				{
					agent.getComponentFeature(IExecutionFeature.class).waitForDelay(1000, this);
				}
				else
				{
					fut.terminate();
					futType.terminate();
				}
				return IFuture.DONE;
			}
		};
		agent.getComponentFeature(IExecutionFeature.class).waitForDelay(1000, step);
		*/
	}
	
	
	//// Goals
	
	
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
	
	@Goal(deliberation=@Deliberation(inhibits={PayExpensesGoal.class}), unique=true)
	public class RetireGoal 
	{
		@GoalCreationCondition(beliefs= {"readyToRetire"})
		public RetireGoal() {}
	}
	
	@Goal(unique=true, recur=true, recurdelay=1)
	public class ImproveGoal
	{
		@GoalMaintainCondition(beliefs= {"timer100"})
		public Boolean checkMaintain()
		{
			return false;
		}
		
		/*@GoalRecurCondition(beliefs= {"timer100"})
		public Boolean checkRecur()
		{
			return true;
		}*/
	}
	
	@Goal
	public class AcquireQuestGoal
	{

	}
	
	@Goal
	public class DoQuestGoal
	{

	}
	
	@Goal
	public class FormGroupGoal 
	{

	}
	
	@Goal
	public class ConsiderOffer
	{
		@GoalParameter
		protected Message msg;
	}

}
