package Adventurers;

import javafx.util.Pair;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import Utilities.IMessageService;
import Utilities.Message;
import jadex.bdiv3.features.IBDIAgentFeature;
import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IExecutionFeature;
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
@Description("Adventurers buy and do quests, increasing their attrbutes and buying equipment "
		+ "in the mean time.")
@RequiredServices(@RequiredService(name="messageServer", type=IMessageService.class, multiple=true,
	binding=@Binding(scope=RequiredServiceInfo.SCOPE_PLATFORM, dynamic=true)))
@Arguments(@Argument(name="name", clazz=String.class, defaultvalue="\"default_name\""))
public class AdventurerBDI 
{
	@Agent
	IInternalAccess agent;
	
	//@AgentFeature 
	//protected IBDIAgentFeature bdiFeature;
	
	@AgentFeature
	protected IRequiredServicesFeature requiredServicesFeature;
	
	@AgentArgument
	protected String name;
	
	public String getName()
	{
		return name;
	}
	
	protected String id;
	
	protected IMessageService messageServer;
	
	@AgentBody
	public void body()
	{
		System.out.println("Body...");
		IFuture<Object> temp = requiredServicesFeature.getRequiredService("messageServer");
		messageServer = (IMessageService)temp.get();
		System.out.println("Bump 1...");
		
		Pair<ISubscriptionIntermediateFuture<Map<String, Object>>, String> sFut = messageServer.subscribe(name);
		final ISubscriptionIntermediateFuture<Map<String, Object>> fut = sFut.getKey();
		id = sFut.getValue();
		System.out.append("My ID is " + id);
		final ISubscriptionIntermediateFuture<Map<String, Object>> futType = messageServer.subscribeType("Adventurers");
		
		fut.addResultListener(new IntermediateDefaultResultListener<Map<String, Object>>()
		{
			public void intermediateResultAvailable(Map<String, Object> msg)
			{
				System.out.println(new Message(msg));
			}
		});
		
		futType.addResultListener(new IntermediateDefaultResultListener<Map<String, Object>>()
		{
			public void intermediateResultAvailable(Map<String, Object> msg)
			{
				System.out.println(new Message(msg));
			}
		});
		
		IComponentStep<Void> step = new IComponentStep<Void>()
		{
			final int[] cnt = new int[1];
			public IFuture<Void> execute(IInternalAccess ia)
			{
				messageServer.send(new Message(id, "Adventurers", cnt[0], "Test protocol", true).toMap());//msg);
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
	}

}
