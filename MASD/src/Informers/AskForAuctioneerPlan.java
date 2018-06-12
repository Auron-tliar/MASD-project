package Informers;

import Adventurers.AdventurerBDI;
import Utilities.Message;
import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.annotation.PlanBody;
import jadex.bdiv3.annotation.PlanCapability;
import jadex.commons.future.IFuture;

@Plan
public class AskForAuctioneerPlan {

	@PlanCapability
	protected InformerBDI capa;
	
	@PlanBody
	public IFuture<Void> body()
	{
		capa.messageServer.send(new Message(capa.id, "Informers", Message.Performatives.query, "", "SetUpAuction", true));
		
		return IFuture.DONE;
	}
}