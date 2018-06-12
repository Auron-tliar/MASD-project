package Adventurers;

import Utilities.Message;
import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.annotation.PlanBody;
import jadex.bdiv3.annotation.PlanCapability;
import jadex.commons.future.IFuture;

@Plan
public class RetirePlan
{
	@PlanCapability
	protected AdventurerBDI capa;
	
	//@PlanAPI
	//protected IPlan rplan;
	
	//@PlanReason
	//protected PayExpences goal;
	
	@PlanBody
	public IFuture<Void> body()
	{
		System.out.println("Checking gold...");
		if (capa.currentGold < capa.retirementGold)
		{
			return IFuture.DONE;
		}
		
		System.out.println("Adventurer " + capa.name + " has successfully saved " + capa.currentGold + 
				" gold and now retires! Attributes:\n" + capa.attributes);
		
		capa.messageServer.send(new Message(capa.id, "Overseer", Message.Performatives.inform, capa.currentGold, "Retirement", false));
		
		return IFuture.DONE;
	}
}
