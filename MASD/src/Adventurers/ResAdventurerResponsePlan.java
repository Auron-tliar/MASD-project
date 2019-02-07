package Adventurers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import Common.Attributes;
import Utilities.Message;
import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.annotation.PlanBody;
import jadex.bdiv3.annotation.PlanCapability;
import jadex.bdiv3.annotation.PlanPrecondition;
import jadex.bdiv3.runtime.IPlan;
import jadex.commons.Tuple2;
import jadex.commons.future.IFuture;

@Plan
public class ResAdventurerResponsePlan 
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
		
		if (!capa.doneFormation && msg.getProtocol() == "FormGroup" && (msg.getPerformative() == Message.Performatives.agree ||
				msg.getPerformative() == Message.Performatives.confirm || msg.getPerformative() == Message.Performatives.disconfirm))
		{
			return true;
		}

		return false;
	}
	
	@PlanBody
	public IFuture<Void> body(IPlan plan, Message msg)
	{
		if (capa.id != capa.groupLeader)
		{
			capa.messageServer.send(new Message(msg.getSender(), capa.groupLeader, msg.getPerformative(), msg.getContent(),
					msg.getProtocol(), msg.getBroadcast()));
			
			return IFuture.DONE;
		}

		Tuple2<List<String>, Attributes> content;
		switch (msg.getPerformative())
		{
			case agree:
				content = (Tuple2<List<String>, Attributes>)msg.getContent();
				
				capa.group.add(msg.getSender());
					
				if (capa.id.compareTo(msg.getSender()) < 0)
				{
					capa.groupLeader = capa.id;
					capa.groupAtributes = capa.groupAtributes.add(content.getSecondEntity());
					capa.group.addAll(content.getFirstEntity());
				}
				else
				{
					capa.groupLeader = msg.getSender();
					capa.doneFormation = true;
				}
				capa.messageServer.send(new Message(capa.id, msg.getSender(), Message.Performatives.confirm, 
						new Tuple2<List<String>, Attributes>(capa.group, capa.groupAtributes), "FormGroup", false));
				
				if (capa.groupAtributes.greaterOrEqual(capa.announcedAuction.getRequirements()))
				{
					capa.doneFormation = true;
					capa.readyToAuction = true;
					System.out.println("Group formed! " + capa.group + " (" + capa.groupAtributes + "). For quest (" + 
							capa.announcedAuction.getRequirements() + ").");
				}
				else if (capa.groupLeader == capa.id)
				{
					Attributes questGoal = new Attributes(capa.announcedAuction.getRequirements().Values);
					questGoal = questGoal.subtract(capa.groupAtributes);
					
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
					
					capa.sendIndex = valuations.size() - 1;
					
					capa.advValuations = valuations;
				}
					
				
				capa.forming = false;
				capa.stopFormation = false;
				
				break;
			case confirm:
				content = (Tuple2<List<String>, Attributes>)msg.getContent();
				
				capa.group.add(msg.getSender());
					
				if (capa.id.compareTo(msg.getSender()) < 0)
				{
					capa.groupLeader = capa.id;
					capa.groupAtributes = capa.groupAtributes.add(content.getSecondEntity());
					capa.group.addAll(content.getFirstEntity());
				}
				else
				{
					capa.groupLeader = msg.getSender();
					capa.doneFormation = true;
				}
				capa.messageServer.send(new Message(capa.id, msg.getSender(), Message.Performatives.confirm, 
						new Tuple2<List<String>, Attributes>(capa.group, capa.groupAtributes), "FormGroup", false));
				
				if (capa.groupAtributes.greaterOrEqual(capa.announcedAuction.getRequirements()))
				{
					capa.doneFormation = true;
					capa.readyToAuction = true;
					System.out.println("Group formed! " + capa.group + " (" + capa.groupAtributes + "). For quest (" + 
							capa.announcedAuction.getRequirements() + ").");
				}
				else if (capa.groupLeader == capa.id)
				{
					Attributes questGoal = new Attributes(capa.announcedAuction.getRequirements().Values);
					questGoal = questGoal.subtract(capa.groupAtributes);
					
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
					
					capa.sendIndex = valuations.size() - 1;
					
					capa.advValuations = valuations;
				}
					
				
				capa.forming = false;
				capa.stopFormation = false;
				
				break;
			case disconfirm:
				capa.stopFormation = false;
				capa.forming = false;
				break;
			default:
				break;
		}
		
		
		return IFuture.DONE;
	}
}
