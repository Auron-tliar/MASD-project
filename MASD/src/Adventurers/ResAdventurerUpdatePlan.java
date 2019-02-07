package Adventurers;

import Common.Attributes;
import Utilities.Message;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.annotation.PlanBody;
import jadex.bdiv3.annotation.PlanCapability;
import jadex.bdiv3.annotation.PlanPrecondition;
import jadex.bdiv3.runtime.IPlan;
import jadex.commons.Tuple2;
import jadex.commons.future.IFuture;

@Plan
public class ResAdventurerUpdatePlan
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
		
		if (!capa.doneFormation && msg.getProtocol() == "FormGroup" && msg.getPerformative() == Message.Performatives.update)
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
			return IFuture.DONE;
		}
		
		capa.forming = true;
		
		Tuple2<List<String>, Attributes> content = (Tuple2<List<String>, Attributes>)msg.getContent();
		
		for (String id : content.getFirstEntity())
		{
			capa.lfgAdventurers.remove(id);
		}

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
		
		capa.forming = false;
		
		return IFuture.DONE;
	}
}
