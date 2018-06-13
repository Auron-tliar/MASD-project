package Crafters;

import java.awt.List;
import java.util.ArrayList;

import Adventurers.AdventurerBDI.AcquireQuestGoal;
import Adventurers.AdventurerBDI.ConsiderOffer;
import Adventurers.AdventurerBDI.DoQuestGoal;
import Adventurers.AdventurerBDI.FormGroupGoal;
import Adventurers.AdventurerBDI.ImproveGoal;
import Adventurers.AdventurerBDI.LiveGoal;
import Adventurers.AdventurerBDI.RetireGoal;
import Common.Equipment;
import Common.LifeExpensesCapability;
import Common.LifeExpensesCapability.PayExpensesGoal;
import Utilities.IMessageService;
import Utilities.Message;
import jadex.bdiv3.annotation.Belief;
import jadex.bdiv3.annotation.Capability;
import jadex.bdiv3.annotation.Deliberation;
import jadex.bdiv3.annotation.Goal;
import jadex.bdiv3.annotation.GoalMaintainCondition;
import jadex.bdiv3.annotation.GoalParameter;
import jadex.bdiv3.features.IBDIAgentFeature;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.component.IRequiredServicesFeature;
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
@Description("Crafters make items and sell them to adventureres. ")
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

public class CrafterBDI {

	@Agent
	IInternalAccess agent;
	@AgentArgument
	protected String name;
	@AgentFeature 
	protected IBDIAgentFeature bdiFeature;
	@AgentFeature
	protected IRequiredServicesFeature requiredServicesFeature;
	@Capability
	protected LifeExpensesCapability capability = new LifeExpensesCapability();
	
	@Belief
	protected Integer gold;
	
	@Belief
	protected ArrayList<Equipment> equipment;
	
	protected IMessageService messageServer;
	@AgentArgument
	protected String id;
	
	@AgentBody
	public void body(){
		IFuture<Object> temp = requiredServicesFeature.getRequiredService("messageServer");
		messageServer = (IMessageService)temp.get();
		
		final ISubscriptionIntermediateFuture<Message> fut = messageServer.subscribe(id);
		final ISubscriptionIntermediateFuture<Message> futType = messageServer.subscribeType("Crafters");
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
		
		//bdiFeature.dispatchTopLevelGoal(capability.new PayExpensesGoal());

		bdiFeature.dispatchTopLevelGoal(new EarningGold());
		bdiFeature.dispatchTopLevelGoal(new MantainGold());
		
	}
	
	@Goal
	public class MantainGold
	{
		@GoalMaintainCondition(beliefs= {"capability.currentGold"})
		public Boolean checkGold()
		{
			System.out.println("Gold = " + capability.getCurrentGold());
			return capability.getCurrentGold() > 0;
		}
	}

	
	@Goal
	public class EarningGold
	{
		
	}
	

	
	@Goal
	public class CraftEquipment
	{
		
	}	
	
	@Goal
	public class ResearchMarket
	{
		
	}
	
	
}
