package Utilities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import Utilities.Message;

import jadex.bridge.IInternalAccess;
import jadex.bridge.SFuture;
import jadex.bridge.fipa.FIPAMessageType;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.annotation.ServiceComponent;
import jadex.bridge.service.annotation.ServiceStart;
import jadex.commons.future.IFuture;
import jadex.commons.future.ISubscriptionIntermediateFuture;
import jadex.commons.future.SubscriptionIntermediateFuture;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentArgument;
import jadex.micro.annotation.AgentCreated;
import jadex.micro.annotation.Argument;
import jadex.micro.annotation.Arguments;
import jadex.micro.annotation.Implementation;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;

@Agent
@Service
@ProvidedServices(@ProvidedService(type=IMessageService.class, implementation=@Implementation(expression="$pojoagent")))
@Arguments(
{
	@Argument(name="id", clazz=String.class)
})
public class MessageServerAgent implements IMessageService
{
	@Agent
	protected IInternalAccess agent;
	
	@AgentArgument
	protected String id;
	
	//protected Integer lastId = -1;
	
	protected Map<String, SubscriptionIntermediateFuture<Message>> subscribers;
	protected Map<String, List<SubscriptionIntermediateFuture<Message>>> subscribersByType;
	
	@AgentCreated
	public void agentCreated()
	{
		this.subscribers = new HashMap<String, SubscriptionIntermediateFuture<Message>>();
		this.subscribersByType = new HashMap<String, List<SubscriptionIntermediateFuture<Message>>>();
	}

	public ISubscriptionIntermediateFuture<Message> subscribe(String receiver)
	{
		SubscriptionIntermediateFuture<Message> ret = 
				(SubscriptionIntermediateFuture<Message>)SFuture.getNoTimeoutFuture(SubscriptionIntermediateFuture.class, agent);
		
		//lastId++;
		//subscribers.put(lastId.toString(), ret);
		subscribers.put(receiver, ret);
		return ret;
	}
	
	public ISubscriptionIntermediateFuture<Message> subscribeType(String type)
	{
		SubscriptionIntermediateFuture<Message> ret = 
				(SubscriptionIntermediateFuture<Message>)SFuture.getNoTimeoutFuture(SubscriptionIntermediateFuture.class, agent);
		
		List<SubscriptionIntermediateFuture<Message>> subs = subscribersByType.get(type);
		if(subs==null)
		{
			subs = new ArrayList<SubscriptionIntermediateFuture<Message>>();
			subscribersByType.put(type, subs);
		}
		subs.add(ret);
		return ret;
	}

	//public IFuture<Void> send(Map<String, Object> msg)
	public IFuture<Void> send(Message msg)
	{
		if (msg.getBroadcast())
		{
			if (subscribersByType.containsKey(msg.getReceiver()))
			{
				for (Iterator<SubscriptionIntermediateFuture<Message>> it = subscribersByType.get(msg.getReceiver()).iterator(); it.hasNext(); )
				{
					SubscriptionIntermediateFuture<Message> sub = it.next();
					if(!sub.addIntermediateResultIfUndone(msg))
					{
						System.out.println("Removed: "+sub);
						it.remove();
					}
				}
			}
		}
		else
		{
			if (subscribers.containsKey(msg.getReceiver()))
			{
				if(!subscribers.get(msg.getReceiver()).addIntermediateResultIfUndone(msg))
				{
					System.out.println("Removed: "+subscribers.get(msg.getReceiver()));
					subscribers.remove(msg.getReceiver());
				}
			}
			else
			{
				System.out.println("Unknown message receiver: " + (String)msg.getReceiver());
			}
		}

		return IFuture.DONE;
	}

}
