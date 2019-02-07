package Adventurers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.concurrent.ThreadLocalRandom;

//import Common.PayExpensesPlan;
import Informers.Auction;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import Common.Attributes;
import Common.Equipment;
import Common.LifeExpensesCapability;
import Common.Quest;
import Utilities.IMessageService;
import Utilities.Message;
import jadex.bdiv3.annotation.Belief;
import jadex.bdiv3.annotation.Body;
import jadex.bdiv3.annotation.Capability;
import jadex.bdiv3.annotation.Deliberation;
import jadex.bdiv3.annotation.Goal;
import jadex.bdiv3.annotation.GoalCreationCondition;
import jadex.bdiv3.annotation.GoalMaintainCondition;
import jadex.bdiv3.annotation.GoalParameter;
import jadex.bdiv3.annotation.GoalRecurCondition;
import jadex.bdiv3.annotation.GoalTargetCondition;
import jadex.bdiv3.annotation.Mapping;
import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.annotation.Plans;
import jadex.bdiv3.annotation.Trigger;
import jadex.bdiv3.features.IBDIAgentFeature;
import jadex.bdiv3.model.MProcessableElement.ExcludeMode;
import jadex.bdiv3.runtime.impl.GoalFailureException;
import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IExecutionFeature;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.commons.Tuple2;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.ISubscriptionIntermediateFuture;
import jadex.commons.future.IntermediateDefaultResultListener;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentArgument;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.AgentFeature;
import jadex.micro.annotation.AgentKilled;
import jadex.micro.annotation.AgentService;
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
	@Argument(name="baseAttributes", clazz=Common.Attributes.class),
	@Argument(name="startingGold", clazz=Double.class, defaultvalue="5"),
	@Argument(name="id", clazz=String.class)
})
@Plans(
{
	@Plan(trigger=@Trigger(goals=AdventurerBDI.AcquireQuestGoal.class), body=@Body(AcquireQuestPlan.class)),
	//@Plan(trigger=@Trigger(goals=AdventurerBDI.AuctionWaitGoal.class), body=@Body(WaitPlan.class)),
	//@Plan(trigger=@Trigger(goals=AdventurerBDI.ConsiderOffer.class), body=@Body(ConsiderOfferPlan.class)),
	@Plan(trigger=@Trigger(goals=AdventurerBDI.DoQuestGoal.class), body=@Body(DoQuestPlan.class)),
	//@Plan(trigger=@Trigger(goals=AdventurerBDI.FormGroupGoal.class), body=@Body(FormGroupPlan.class)),
	@Plan(trigger=@Trigger(goals=AdventurerBDI.ImproveGoal.class), body=@Body(AskForEquipmentPlan.class), priority=10),
	@Plan(trigger=@Trigger(goals=AdventurerBDI.ImproveGoal.class), body=@Body(TrainPlan.class), priority=0),
	@Plan(trigger=@Trigger(goals=AdventurerBDI.LiveGoal.class), body=@Body(DiePlan.class)),
	@Plan(trigger=@Trigger(goals=AdventurerBDI.PayTaxGoal.class), body=@Body(PayTaxPlan.class)),
	@Plan(trigger=@Trigger(goals=AdventurerBDI.RetireGoal.class), body=@Body(RetirePlan.class)),
	
	@Plan(trigger=@Trigger(goals=AdventurerBDI.ProcessMail.class), body=@Body(Adventurers.ResAcquireOfferPlan.class)),
	@Plan(trigger=@Trigger(goals=AdventurerBDI.ProcessMail.class), body=@Body(Adventurers.ResAdventurerDescriptionPlan.class)),
	@Plan(trigger=@Trigger(goals=AdventurerBDI.ProcessMail.class), body=@Body(Adventurers.ResAdventurerProposalPlan.class)),
	@Plan(trigger=@Trigger(goals=AdventurerBDI.ProcessMail.class), body=@Body(Adventurers.ResAdventurerResponsePlan.class)),
	@Plan(trigger=@Trigger(goals=AdventurerBDI.ProcessMail.class), body=@Body(Adventurers.ResAdventurerUpdatePlan.class)),
	@Plan(trigger=@Trigger(goals=AdventurerBDI.ProcessMail.class), body=@Body(Adventurers.ResAuctionAnnouncedPlan.class)),
	@Plan(trigger=@Trigger(goals=AdventurerBDI.ProcessMail.class), body=@Body(Adventurers.ResAuctionCollectPlan.class)),
	@Plan(trigger=@Trigger(goals=AdventurerBDI.ProcessMail.class), body=@Body(Adventurers.ResAuctionResultPlan.class)),
	@Plan(trigger=@Trigger(goals=AdventurerBDI.ProcessMail.class), body=@Body(Adventurers.ResAuctionStartPlan.class)),
	@Plan(trigger=@Trigger(goals=AdventurerBDI.ProcessMail.class), body=@Body(Adventurers.ResAuctionUpdatePlan.class)),
	@Plan(trigger=@Trigger(goals=AdventurerBDI.ProcessMail.class), body=@Body(Adventurers.ResMinProfitRequestPlan.class)),
	@Plan(trigger=@Trigger(goals=AdventurerBDI.ProcessMail.class), body=@Body(Adventurers.ResReceiveEquipmentPlan.class)),
	@Plan(trigger=@Trigger(goals=AdventurerBDI.ProcessMail.class), body=@Body(Adventurers.ResReceiveMinProfitPlan.class)),
	@Plan(trigger=@Trigger(goals=AdventurerBDI.ProcessMail.class), body=@Body(Adventurers.ResReceiveQuestPlan.class)),
	@Plan(trigger=@Trigger(goals=AdventurerBDI.ProcessMail.class), body=@Body(Adventurers.ResRequestPaymentPlan.class)),
	@Plan(trigger=@Trigger(goals=AdventurerBDI.ProcessMail.class), body=@Body(Adventurers.ResUpdateTaxPlan.class)),
	
	@Plan(trigger=@Trigger(goals=AdventurerBDI.ProcessMail.class), body=@Body(Adventurers.ResDefaultPlan.class), 
	priority=-100)
})

public class AdventurerBDI 
{
	@Agent
	IInternalAccess agent;
	
	@AgentFeature 
	protected IBDIAgentFeature bdiFeature;
	
	//@AgentFeature
	//protected IRequiredServicesFeature requiredServicesFeature;
	
	@AgentArgument
	protected String name;
	
	@AgentArgument
	protected String id;
	
	@AgentArgument
	protected Double startingGold;
	
	@Capability
	protected LifeExpensesCapability leCapability = new LifeExpensesCapability();
	
	protected LifeExpensesCapability.PayExpensesGoal payExpensesGoal;
	protected ImproveGoal improveGoal;
	
	public String getName()
	{
		return name;
	}
	
	protected static Integer minRetireGold = 100;
	protected static Integer maxRetireGold = 200;
	
	
	protected Integer riskLevel;
	
	
	@AgentService
	protected IMessageService messageServer;// = (IMessageService)(requiredServicesFeature.getRequiredService("messageServer").get());
	
	
	/// Beliefs
	
	@Belief
	@AgentArgument
	protected Attributes baseAttributes;
	
	@Belief
	protected List<Equipment> equipmentList = new ArrayList<Equipment>();
	
	@Belief
	protected Attributes equipmentSum = new Attributes();
	
	
	@Belief(dynamic=true)
	protected Attributes totalAttributes = sum(baseAttributes, equipmentSum);

	@Belief
	protected Quest currentQuest = null;

	protected Integer retirementGold;
	
	@Belief
	protected Auction announcedAuction;
	
	@Belief
	protected List<Message> receivedMessages = new ArrayList<Message>();

	@Belief
	protected Boolean forming = false;
	
	@Belief
	protected Boolean doneFormation = true;
	
	@Belief
	protected Boolean stopFormation = false;
	
	@Belief
	protected Integer sendIndex = -1;
	
	@Belief
	protected Boolean readyToAuction = false;
	
	@Belief
	protected Integer collectedResponces = 0;
	
	
	protected Boolean auctioning = false;
	
	protected String auctioneerId;
	
	protected List<String> sentAdventurers;
	
	protected Integer prevMessageCount = 0;
	
	protected Double taxPercentage = 0.1;
	
	protected Boolean collecting = false;
	
	protected long collectingTime = 3000;
	protected long waitTime = 2000;
	
	protected Map<String, Attributes> lfgAdventurers = new HashMap<String, Attributes>();
	
	protected List<Tuple2<String, Double>> advValuations;
	
	@Belief
	protected String groupLeader;
	
	protected List<String> group;
	
	protected Attributes groupAtributes;
	
	protected Double minProfit = 1.0;
	
	protected Double maxPrice = 1000.0;
	
	protected Double currentBid = 1.0;
	
	protected long lastQuestTime;
	
	protected Attributes sum(Attributes attr1, Attributes attr2)
	{
		if (attr1 != null && attr2 != null)
		{
			return attr1.add(attr2);
		}
		else if (attr1 == null)
		{
			return (new Attributes()).add(attr2);
		}
		else
		{
			return attr1.add(new Attributes());
		}
	}
	
	public void Die()
	{
		messageServer.send(new Message(id, "Overseer", Message.Performatives.inform, "Adventurer", "Death", false));
	}
	
	public void payTax(Double sum)
	{
		messageServer.send(new Message(id, "Overseer", Message.Performatives.payment, leCapability.payGold(sum), "Tax", false));
	}
	
	
	@AgentBody
	public void body()
	{
		final ISubscriptionIntermediateFuture<Message> fut = messageServer.subscribe(id);
		
		/// Change Adventurers to other types ///
		final ISubscriptionIntermediateFuture<Message> futType = messageServer.subscribeType(id, "Adventurers");
		
		fut.addResultListener(new IntermediateDefaultResultListener<Message>()
		{
			public void intermediateResultAvailable(Message msg)
			{
				//System.out.println(msg);
				System.out.println("Adventurer " + name + " has received a message: " + msg);
				receivedMessages.add(msg);
			}
		});
		
		futType.addResultListener(new IntermediateDefaultResultListener<Message>()
		{
			public void intermediateResultAvailable(Message msg)
			{
				//System.out.println(msg);
				System.out.println("Adventurer " + name + " has heard a broadcast: " + msg);
				receivedMessages.add(msg);
			}
		});
		
		
		
		/// Generate the gold amount to retire
		retirementGold = ThreadLocalRandom.current().nextInt(minRetireGold, maxRetireGold + 1);
		
		
		// very unsafe...
		
		leCapability.initialize(name, id, startingGold);
		
		announcedAuction = null;
		
		lastQuestTime = System.currentTimeMillis();
		
		riskLevel = ThreadLocalRandom.current().nextInt(1, 4);
		
		if (baseAttributes == null)
		{
			baseAttributes = new Attributes();
		}
		
		//payExpensesGoal = new lifeExpensesCapability.PayExpensesGoal();
		improveGoal = new ImproveGoal();
		//bdiFeature.dispatchTopLevelGoal(new AuctionWaitGoal());
		bdiFeature.dispatchTopLevelGoal(improveGoal);
		bdiFeature.dispatchTopLevelGoal(new LiveGoal());
		bdiFeature.dispatchTopLevelGoal(new RetireGoal());
		bdiFeature.dispatchTopLevelGoal(leCapability.new PayExpensesGoal());
		//bdiFeature.dispatchTopLevelGoal(new FormGroupGoal());
		bdiFeature.dispatchTopLevelGoal(new AcquireQuestGoal());
		bdiFeature.dispatchTopLevelGoal(new DoQuestGoal());
		
		System.out.println("Adventurer " + name + " (" + id + ") has arrived! Goal: " + retirementGold + " (" + 
				leCapability.getCurrentGold() + "). Attributes: " + baseAttributes.toString());
	}
	

	@AgentKilled
	public void shutdown()
	{
		messageServer.unsubscribe(id);
		agent.killComponent();
	}
	
	
	//// Goals
	
	@Goal(deliberation=@Deliberation(inhibits={LifeExpensesCapability.PayExpensesGoal.class, AcquireQuestGoal.class, 
			DoQuestGoal.class, ImproveGoal.class, RetireGoal.class}),//, ConsiderOffer.class}),
			unique=true)
	public class LiveGoal
	{
		@GoalMaintainCondition(beliefs= {"leCapability.dying"})
		public Boolean checkMaintain()
		{
			return !leCapability.getDying();
		}
	}
	
	
	@Goal(unique=true)
	public class RetireGoal 
	{
		@GoalMaintainCondition(beliefs={"leCapability.currentGold"})
		public Boolean checkMaintain()
		{
			return ((leCapability.getCurrentGold()) < retirementGold);
		}
	}
	
	@Goal(unique=true, recur=true, recurdelay=10)
	public class ImproveGoal
	{}
	
	@Goal
	public class ProcessMail
	{
		@GoalParameter
		protected Message message = null;
		
		@GoalCreationCondition(beliefs="receivedMessages")
		public ProcessMail()
		{
			if (prevMessageCount >= (receivedMessages.size()))
			{
				prevMessageCount = receivedMessages.size();
				return;
			}
			this.message = receivedMessages.get(prevMessageCount);
			prevMessageCount = receivedMessages.size();
		}
	}
	
	@Goal
	public class PayTaxGoal
	{
		@GoalParameter
		protected Integer taxAmount = 0;
		
		@GoalCreationCondition(beliefs="leCapability.currentGold")
		public PayTaxGoal()
		{
			if (leCapability.getGoldDelta() > 0)
			{
				taxAmount = (int)(Math.ceil(leCapability.getGoldDelta() * taxPercentage));
			}
			else
			{
				taxAmount = 0;
			}
		}
	}
	
	@Goal(deliberation=@Deliberation(inhibits={ImproveGoal.class}), unique=true)
	public class AcquireQuestGoal{ }
	
	@Goal(deliberation=@Deliberation(inhibits={ImproveGoal.class}))
	public class DoQuestGoal { }

}
