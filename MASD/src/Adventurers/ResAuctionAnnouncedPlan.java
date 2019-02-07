package Adventurers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import Common.Attributes;
import Informers.Auction;
import Utilities.Message;
import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.annotation.PlanBody;
import jadex.bdiv3.annotation.PlanCapability;
import jadex.bdiv3.annotation.PlanPrecondition;
import jadex.bdiv3.runtime.IPlan;
import jadex.commons.Tuple2;
import jadex.commons.future.IFuture;

@Plan
public class ResAuctionAnnouncedPlan
{
	@PlanCapability
	AdventurerBDI capa;
	
	@PlanPrecondition
	public Boolean checkPreconditions(Message msg)
	{
		if (msg == null)
		{
			return false;
		}
		
		if (capa.collecting)
		{
			return false;
		}
		
		if (msg.getProtocol() == "Auction" && msg.getPerformative() == Message.Performatives.inform)
		{
			try
			{
				Auction temp = (Auction)msg.getContent();
				return true;
			}
			catch (Exception ex)
			{
				return false;
			}
		}
		return false;
	}
	
	@PlanBody
	public IFuture<Void> body(IPlan plan, Message msg)
	{
		if (capa.announcedAuction != null)
		{
			return IFuture.DONE;
		}
		capa.collecting = true;
		capa.lfgAdventurers = new HashMap<String, Attributes>();
		
		capa.announcedAuction = (Auction)msg.getContent();
		capa.auctioneerId = msg.getSender();
		//Integer reward = capa.announcedAuction.getReward();
		capa.groupLeader = capa.id;
		capa.group = new ArrayList<String>();
		capa.group.add(capa.id);
		capa.groupAtributes = (new Attributes()).add(capa.totalAttributes);
		
		if (capa.totalAttributes.greaterOrEqual(capa.announcedAuction.getRequirements()))
		{
			capa.doneFormation = true;
			capa.forming = false;
			capa.stopFormation = false;
			capa.readyToAuction = true;
			capa.collecting = false;
			
			plan.dispatchSubgoal(capa.new AcquireQuestGoal());
			
			return IFuture.DONE;
		}
		
		capa.messageServer.send(new Message(capa.id, "Adventurers", Message.Performatives.inform, 
				new Tuple2<List<String>, Attributes>(capa.group, capa.groupAtributes), "FormGroup", true));
		
		plan.waitFor(capa.collectingTime).get();
		
		capa.collecting = false;
		
		capa.sendIndex = capa.lfgAdventurers.size() - 1;
		
		Attributes questGoal = new Attributes(capa.announcedAuction.getRequirements().Values);
		questGoal = questGoal.subtract(capa.groupAtributes);
		//Double cap = 10.0 + capa.riskLevel;
		//Double minReward = ((capa.announcedAuction.getStartTime() - System.currentTimeMillis() + 
		//		cap*capa.announcedAuction.getMaxWait()) / 
		//		capa.leCapability.getPaymentInterval()) * capa.leCapability.getPaymentAmount()*0.1*cap;
		//System.out.println("Adventurer " + capa.name + ": minReward = " + minReward);
		
		List<Tuple2<String, Double>> valuations = new ArrayList<Tuple2<String, Double>>();
		Iterator<Map.Entry<String, Attributes>> iter = capa.lfgAdventurers.entrySet().iterator();
		
		while (iter.hasNext()) 
		{
			Map.Entry<String, Attributes> entr = iter.next();
			valuations.add(new Tuple2<String, Double>(entr.getKey(), questGoal.coveredBy(entr.getValue())));
		}
		
		Collections.sort(valuations, new Comparator<Tuple2<String, Double>>() 
		{
			public int compare(Tuple2<String, Double> a, Tuple2<String, Double> b)
			{
				return a.getSecondEntity().compareTo(b.getSecondEntity());
			}
		});
		
		capa.advValuations = valuations;
		
		capa.doneFormation = false;
		capa.forming = false;
		
		//capa.agreedAdventurers = new ArrayList<String>();
		/*long timeoutLeft = capa.waitTime;
		long start = System.currentTimeMillis();
		
		for (int i = valuations.size() - 1; i >= 0; i--)
		{
			capa.messageServer.send(new Message(capa.id, valuations.get(i).getFirstEntity(), Message.Performatives.propose, 
					capa.totalAttributes, "FormGroup", false));
			
			while (timeoutLeft > 0)
			{
				plan.waitForFactAdded("capa.agreedAdventurers", timeoutLeft);
				
				timeoutLeft -= (System.currentTimeMillis() - start);
				
				for (int j = valuations.size() - 1; j >= i; j--)
				{
					if (capa.agreedAdventurers.contains(valuations.get(j).getFirstEntity()))
					{
						
					}
				}
			}
		}*/
		
		
		return IFuture.DONE;
	}
}