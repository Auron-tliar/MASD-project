package Adventurers;

import java.util.concurrent.ThreadLocalRandom;

import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.annotation.PlanBody;
import jadex.bdiv3.annotation.PlanCapability;
import jadex.bdiv3.runtime.IPlan;
import jadex.commons.future.IFuture;

@Plan
public class DoQuestPlan
{
	@PlanCapability
	protected AdventurerBDI capa;
	
	@PlanBody
	public IFuture<Void> body(IPlan plan)
	{
		System.out.println("Adventurer " + capa.name + " in a group of " + capa.group.size() + " companions has started "
				+ "doing quest \"" + capa.currentQuest.getName() + "\"!");
		
		plan.waitFor(((Double)(capa.currentQuest.getRequirements().total() * 1000 *
				ThreadLocalRandom.current().nextDouble(0.9, 1.1))).intValue()).get();

		System.out.println("The quest \"" + capa.currentQuest.getName() + "\" is done!");
		Double rew = capa.currentQuest.getReward() / capa.group.size();
		capa.leCapability.receiveGold(rew);
		System.out.println("Adventurer " + capa.name + " has received a reward of " + rew + "! " + 
				capa.name + " now has " + capa.leCapability.getCurrentGold() + " gold.");
		capa.lastQuestTime = System.currentTimeMillis();
		
		capa.currentQuest = null;
		capa.announcedAuction = null;
		
		
		return IFuture.DONE;
	}
}
