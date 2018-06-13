package Informers;

import java.util.Map;

import Adventurers.AdventurerBDI;
import Common.Attributes;
import Common.Quest;
import Utilities.IMessageService;
import Utilities.Message;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.commons.future.IFuture;
import jadex.commons.future.ISubscriptionIntermediateFuture;
import jadex.commons.future.IntermediateDefaultResultListener;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentArgument;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.AgentFeature;

@Agent
public class AuctioneerAgent {
	@Agent
	IInternalAccess agent;
	
	@AgentArgument
	protected String id;
	
	@AgentArgument
	protected String name;
	
	@AgentFeature
	protected IRequiredServicesFeature requiredServicesFeature;
	
	protected Quest quest;
	
	protected AuctionInfo auctionInfo;
	
	protected Integer highestBid;

	protected String highestBidAuctioneerId;

	protected String informerId;
	
	protected IMessageService messageServer;
	
	@AgentBody
	public void body() {
		IFuture<Object> temp = requiredServicesFeature.getRequiredService("messageServer");
		messageServer = (IMessageService)temp.get();
		
		final ISubscriptionIntermediateFuture<Message> fut = messageServer.subscribe(id);
		final ISubscriptionIntermediateFuture<Message> futType = messageServer.subscribeType("Auctioneers");
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
	
	}
	
	public IFuture<Void> announceAuction()
	{
		this.messageServer.send(new Message(this.id, "Adventurers", Message.Performatives.inform, this.auctionInfo, "NewAuction", true));
		
		return IFuture.DONE;
	}
	
	protected IFuture<Void> considerBid(Message bid)
	{
		Map<String, Object> content = (Map<String, Object>)bid.getContent();
		Attributes attr = (Attributes)content.get("Attributes");
		Integer price = (Integer)content.get("Price");


		if (price < this.highestBid)
		{
			this.messageServer.send(new Message(this.id, bid.getSender(), Message.Performatives.accept, 
					"", "BidAccepted", false));
			this.highestBid = price;
			this.highestBidAuctioneerId = bid.getSender();
			this.messageServer.send(new Message(this.id, "Adventurers", Message.Performatives.inform, 
					"", "newPrice" + price.toString(), true));
		}
		else
		{
			this.messageServer.send(new Message(this.id, bid.getSender(), Message.Performatives.accept, 
					"", "BidDeclined", false));
		}
		
		return IFuture.DONE;
	}
	
	protected IFuture<Void> declareWinner()
	{
		this.messageServer.send(new Message(this.id, "Auctioneers", Message.Performatives.inform, 
					"", "AuctionEnded", true));
		this.messageServer.send(new Message(this.id, "Informers", Message.Performatives.inform, 
				"", "AuctionEnded", true));
		this.messageServer.send(new Message(this.id, this.highestBidAuctioneerId, Message.Performatives.inform, 
				this.quest, "AuctionWon", false));
		this.messageServer.send(new Message(this.id, this.informerId, Message.Performatives.inform, 
				this.highestBid, "AuctionWon", false));
		
		
		return IFuture.DONE;
	}
}
