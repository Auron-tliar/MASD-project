package Adventurers;

import java.util.concurrent.ThreadLocalRandom;

import Utilities.Message;
import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.annotation.PlanBody;
import jadex.bdiv3.annotation.PlanCapability;
import jadex.bdiv3.annotation.PlanContextCondition;
import jadex.bdiv3.annotation.PlanPrecondition;
import jadex.bdiv3.runtime.IPlan;
import jadex.bdiv3.runtime.impl.PlanFailureException;
import jadex.commons.future.IFuture;

@Plan
public class FormGroupPlan
{
	@PlanPrecondition
	public Boolean checkPrecondition()
	{
		return capa.sendIndex >= -1;
	}
	
	@PlanCapability
	public AdventurerBDI capa;
	
	@PlanContextCondition(beliefs={"stopFormation","doneFormation"})
	public Boolean contextCheck()
	{
		return (!capa.stopFormation) && (!capa.doneFormation);
	}
	
	@PlanBody
	public IFuture<Void> body(IPlan plan)
	{
		if (capa.sendIndex == -1)
		{
			capa.announcedAuction = null;
			capa.forming = false;
			capa.doneFormation = true;
			capa.stopFormation = false;
			
			throw new PlanFailureException();
		}
		capa.forming = true;
		plan.waitFor(ThreadLocalRandom.current().nextInt((int)capa.waitTime/2, (int)(capa.waitTime * 1.5))).get();
		
		capa.messageServer.send(new Message(capa.id, capa.advValuations.get(capa.sendIndex).getFirstEntity(),
				Message.Performatives.propose, capa.totalAttributes, "FormGroup", false));
		
		capa.sendIndex--;
		capa.forming = false;
		return IFuture.DONE;
	}
}
