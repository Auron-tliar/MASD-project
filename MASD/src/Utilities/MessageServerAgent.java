package Utilities;

import javafx.util.Pair;
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
import jadex.micro.annotation.AgentCreated;
import jadex.micro.annotation.Implementation;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;

@Agent
@Service
@ProvidedServices(@ProvidedService(type=IMessageService.class, implementation=@Implementation(expression="$pojoagent")))
public class MessageServerAgent implements IMessageService
{
	@Agent
	protected IInternalAccess agent;
	
	protected Integer lastId = -1;
	
	protected Map<String, SubscriptionIntermediateFuture<Map<String, Object>>> subscribers;
	protected Map<String, List<SubscriptionIntermediateFuture<Map<String, Object>>>> subscribersByType;
	
	@AgentCreated
	public void agentCreated()
	{
		this.subscribers = new HashMap<String, SubscriptionIntermediateFuture<Map<String, Object>>>();
		this.subscribersByType = new HashMap<String, List<SubscriptionIntermediateFuture<Map<String, Object>>>>();
	}

	public Pair<ISubscriptionIntermediateFuture<Map<String, Object>>, String> subscribe(String receiver)
	{
		SubscriptionIntermediateFuture<Map<String, Object>> ret = 
				(SubscriptionIntermediateFuture<Map<String, Object>>)SFuture.getNoTimeoutFuture(SubscriptionIntermediateFuture.class, agent);
		
		lastId++;
		subscribers.put(lastId.toString(), ret);
		return new Pair<ISubscriptionIntermediateFuture<Map<String, Object>>, String>(ret, lastId.toString());
	}
	
	public ISubscriptionIntermediateFuture<Map<String, Object>> subscribeType(String type)
	{
		SubscriptionIntermediateFuture<Map<String, Object>> ret = 
				(SubscriptionIntermediateFuture<Map<String, Object>>)SFuture.getNoTimeoutFuture(SubscriptionIntermediateFuture.class, agent);
		
		List<SubscriptionIntermediateFuture<Map<String, Object>>> subs = subscribersByType.get(type);
		if(subs==null)
		{
			subs = new ArrayList<SubscriptionIntermediateFuture<Map<String, Object>>>();
			subscribersByType.put(type, subs);
		}
		subs.add(ret);
		return ret;
	}

	public IFuture<Void> send(Map<String, Object> msg)
	{
		if ((Boolean)msg.get("Broadcast"))
		{
			if (subscribersByType.containsKey((String)msg.get("Receiver")))
			{
				for (Iterator<SubscriptionIntermediateFuture<Map<String, Object>>> it = subscribersByType.get((String)msg.get("Receiver")).iterator(); it.hasNext(); )
				{
					SubscriptionIntermediateFuture<Map<String, Object>> sub = it.next();
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
			if (subscribers.containsKey((String)msg.get("Receiver")))
			{
				if(!subscribers.get((String)msg.get("Receiver")).addIntermediateResultIfUndone(msg))
				{
					System.out.println("Removed: "+subscribers.get((String)msg.get("Receiver")));
					subscribers.remove((String)msg.get("Receiver"));
				}
			}
			else
			{
				System.out.println("Unknown message receiver: " + (String)msg.get("Receiver"));
			}
		}
		
		return IFuture.DONE;
	}

}
