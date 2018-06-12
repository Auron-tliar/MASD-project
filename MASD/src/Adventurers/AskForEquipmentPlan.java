package Adventurers;

import Utilities.Message;

import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.annotation.PlanBody;
import jadex.bdiv3.annotation.PlanCapability;
import jadex.commons.future.IFuture;

@Plan
public class AskForEquipmentPlan
{
	@PlanCapability
	protected AdventurerBDI capa;
	
	@PlanBody
	public IFuture<Void> body()
	{
		capa.messageServer.send(new Message(capa.id, "Crafters", Message.Performatives.query, "", "AcquireEquipment", true));
		
		return IFuture.DONE;
	}
}
