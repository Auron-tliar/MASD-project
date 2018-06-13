package Crafters;

import java.util.Map;

import Adventurers.AdventurerBDI;
import Common.Attributes;
import Utilities.Message;
import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.annotation.PlanCapability;
import jadex.commons.future.IFuture;

@Plan
public class SellEquipmentPlan {

	@PlanCapability
	protected CrafterBDI agent;

	protected void makeOffer(Message offer) {
		Map<String, Object> content = (Map<String, Object>) offer.getContent();
		Attributes attr = (Attributes) content.get("Attributes");
		Integer price = (Integer) content.get("Price");

		agent.messageServer
				.send(new Message(agent.id, "Adventureres", Message.Performatives.inform, "", "SellEquipment", true));
	}

}
