package Informers;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import Common.Quest;
import Utilities.IMessageService;
import Utilities.Message;
import jadex.bdiv3.annotation.Belief;
import jadex.bdiv3.annotation.Goal;
import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.annotation.Trigger;
import jadex.bdiv3.runtime.ChangeEvent;
import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IExecutionFeature;
import jadex.bridge.service.RequiredServiceInfo;
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
import jadex.rules.eca.ChangeInfo;

@Agent
@Description("The auctioneer agent (non-BDI)")
@RequiredServices(
{
	@RequiredService(name="messageServer", type=IMessageService.class, multiple=false,
	binding=@Binding(scope=RequiredServiceInfo.SCOPE_PLATFORM, dynamic=true))
})
@Arguments(
{
	@Argument(name="quest", clazz=Quest.class),
	@Argument(name="minPrice", clazz=Double.class, defaultvalue="0.0"),
	@Argument(name="minIncrease", clazz=Double.class, defaultvalue="1.0"),
	@Argument(name="owner", clazz=String.class),
	@Argument(name="id", clazz=String.class)
})
public class AuctioneerAgent
{
	@Agent
	IInternalAccess agent;
	
	@AgentFeature
	IExecutionFeature exec;
	
	@AgentArgument
	protected Quest quest;
	
	@AgentArgument
	protected String id;
	
	@AgentArgument
	protected Double minPrice;
	
	@AgentArgument
	protected Double minIncrease;
	
	@AgentArgument
	protected String owner;
	
	protected Auction auction;
	
	protected Integer auctionTimeout = 10000;
	
	
	protected Boolean active = false;
	protected Boolean finished = false;
	protected Boolean sent = false;
	
	protected Boolean gotNewBid = false;
	
	protected Double currentBid = 0.0;
	
	protected String bestBidder;
	
	protected long lastBidTime = 0;
	
	protected Map<String, Object> update;
	
	
	@AgentService
	protected IMessageService messageServer;
	
	@AgentBody
	public void body()
	{
		final ISubscriptionIntermediateFuture<Message> fut = messageServer.subscribe(id);
		
		/// Change Adventurers to other types ///
		final ISubscriptionIntermediateFuture<Message> futType = messageServer.subscribeType(id, "Auctioneer");
		
		System.out.println("Auctioneer (" + id + ") has arrived!");
		
		auction = new Auction(quest, minPrice, System.currentTimeMillis() + 6000, minIncrease, 5000);
		currentBid = minPrice - minIncrease;
		
		//System.out.println("Auction start time: " + auction.getStartTime());
		
		fut.addResultListener(new IntermediateDefaultResultListener<Message>()
		{
			public void intermediateResultAvailable(Message msg)
			{
				System.out.println("Auctioneer " + id + " has received a message: " + msg);
				if (System.currentTimeMillis() < auction.getStartTime())
				{
					messageServer.send(new Message(id, msg.getSender(), Message.Performatives.refuse, "Auction hasn't started",
							"Auction", false));
				}
				else 
				{	
					if(!active && !finished)
					{
						active = true;
						System.out.println("Auction " + id + " has begun!");
						lastBidTime = System.currentTimeMillis();
						exec.waitForDelay(100).get();
					}
					
					if (active && msg.getPerformative() == Message.Performatives.propose)
					{
						if ((Double)msg.getContent() - minIncrease >= currentBid)
						{
							currentBid = (Double)msg.getContent();
							bestBidder = msg.getSender();
							lastBidTime = System.currentTimeMillis();
							gotNewBid = true;
							update = new HashMap<String, Object>();
							update.put("id", auction.getId());
							update.put("highestBid", currentBid);
							update.put("timestamp", System.currentTimeMillis());
							update.put("currentLeader", bestBidder);
							
							messageServer.send(new Message(id, "Adventurers", Message.Performatives.inform, update, "Auction", true));
						}
					}
					else if (finished && msg.getPerformative() == Message.Performatives.payment)
					{
						if ((Double)msg.getContent() == currentBid)
						{
							messageServer.send(new Message(id, bestBidder, Message.Performatives.parcel, quest, "Auction", false));
							messageServer.send(new Message(id, owner, Message.Performatives.payment, currentBid, "Auction", false));
							sent = true;
							System.out.println("Auction " + id + " finished");
						}
						else if ((Double)msg.getContent() > currentBid)
						{
							messageServer.send(new Message(id, bestBidder, Message.Performatives.parcel, quest, "Auction", false));
							messageServer.send(new Message(id, bestBidder, Message.Performatives.payment, 
									((Integer)msg.getContent() - currentBid), "Auction", false));
							messageServer.send(new Message(id, owner, Message.Performatives.payment, currentBid, "Auction", false));
							sent = true;
	
							System.out.println("Auction " + id + " finished");
						}
						else if ((Double)msg.getContent() < currentBid)
						{
							messageServer.send(new Message(id, bestBidder, Message.Performatives.payment, 
									(Integer)msg.getContent(), "Auction", false));
							messageServer.send(new Message(id, bestBidder, Message.Performatives.failure, "", "Auction", false));
						}
					}
				}
			}
		});
		
		futType.addResultListener(new IntermediateDefaultResultListener<Message>()
		{
			public void intermediateResultAvailable(Message msg)
			{
				//System.out.println(msg);
				System.out.println("Auctioneer " + id + " has heard a broadcast: " + msg);
			}
		});
		
		messageServer.send(new Message(id, "Adventurers", Message.Performatives.inform, auction, "Auction", true));
		messageServer.send(new Message(id, "Crafters", Message.Performatives.inform, auction, "Auction", true));
		
		IComponentStep<Void> step = new IComponentStep<Void>()
		{
			public IFuture<Void> execute(IInternalAccess agent)
			{

				
				while (!sent)
				{
					if (System.currentTimeMillis() >= auction.getStartTime())
					{
						if (!finished && !active)
						{
							active = true;
							System.out.println("Auction " + id + " has begun!");
							lastBidTime = System.currentTimeMillis();
							exec.waitForDelay(100).get();
						}
						
						if (active)
						{
							if((System.currentTimeMillis() - lastBidTime) > auction.getMaxWait())
							{
								active = false;
								finished = true;
								List<String> advs = messageServer.getSubscribersByType("Adventurers");
								for	(String adv : advs)
								{
									if (adv != bestBidder)
									{
										messageServer.send(new Message(id, adv, Message.Performatives.reject, currentBid, "Auction", false));
									}
									else
									{
										messageServer.send(new Message(id, adv, Message.Performatives.accept, currentBid, "Auction", false));
									}
								}
							}
						}
					}
					
					exec.waitForDelay(10).get();
				}
				

				messageServer.send(new Message(id, "Overseer", Message.Performatives.inform, "Auctioneer", "Death", false));
				
				return IFuture.DONE;
			}
		};
		
		exec.waitForDelay(100, step).get();
	}
	
	
	@AgentKilled
	public void shutdown()
	{
		messageServer.unsubscribe(id);
		agent.killComponent();
	}
}
