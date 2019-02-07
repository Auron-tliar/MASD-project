package Adventurers;

import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import Utilities.Message;
import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.annotation.PlanBody;
import jadex.bdiv3.annotation.PlanCapability;
import jadex.bdiv3.annotation.PlanPrecondition;
import jadex.bdiv3.runtime.IPlan;
import jadex.commons.future.IFuture;

@Plan
public class ResAuctionUpdatePlan
{
	@PlanCapability
	protected AdventurerBDI capa;
	
	@PlanPrecondition
	public Boolean checkPreconditions(Message msg)
	{
		if (msg == null)
		{
			return false;
		}
		
		if (msg.getProtocol() == "Auction" && msg.getPerformative() == Message.Performatives.inform && 
				capa.groupLeader == capa.id && capa.auctioneerId == msg.getSender())
		{
			return true;
		}

		return false;
	}
	
	@PlanBody
	public IFuture<Void> body(IPlan plan, Message msg)
	{
		Map<String, Object> content = (Map<String, Object>)msg.getContent();
		
		if ((String)content.get("currentLeader") != capa.id)
		{
			plan.waitFor(ThreadLocalRandom.current().nextInt(500, 1501)).get();
			Double nextBid = (Double)content.get("highestBid") + capa.announcedAuction.getMinimalBid();
			//nextBid += (capa.group.size() - nextBid % capa.group.size());
			if ((nextBid / capa.group.size() <= capa.maxPrice) && 
					((capa.announcedAuction.getReward() - nextBid) / capa.group.size() >= capa.minProfit))
			{
				capa.messageServer.send(new Message(capa.id, capa.auctioneerId, Message.Performatives.propose, 
						nextBid, "Auction", false));
			}
		}
		
		return IFuture.DONE;
	}
}
