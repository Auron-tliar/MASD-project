package Adventurers;

import java.util.Map;

import Common.Attributes;
import Utilities.Message;
import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.annotation.PlanBody;
import jadex.bdiv3.annotation.PlanCapability;
import jadex.commons.future.IFuture;

@Plan
public class ConsiderOfferPlan
{
	@PlanCapability
	protected AdventurerBDI capa;
	
	//@PlanAPI
	//protected IPlan rplan;
	
	//@PlanReason
	//protected PayExpences goal;
	
	/*@PlanBody
	public IFuture<Void> body()
	{
		
	}*/
	
	protected IFuture<Void> considerOffer(Message offer)
	{
		Map<String, Object> content = (Map<String, Object>)offer.getContent();
		Attributes attr = (Attributes)content.get("Attributes");
		Integer price = (Integer)content.get("Price");
		Double cap = 10.0 + capa.riskLevel;
		
		if (price < capa.leCapability.getCurrentGold() - capa.leCapability.getPaymentAmount()*(4 - capa.riskLevel) &&
				new Attributes(cap, cap, cap).greaterOrEqual(attr.sum(capa.attributes)) &&
				price < attr.mean() * (cap * 0.1))
		{
			capa.messageServer.send(new Message(capa.id, offer.getSender(), Message.Performatives.accept, 
					capa.leCapability.payGold(price), "AcquireEquipment", false));
		}
		else
		{
			capa.messageServer.send(new Message(capa.id, offer.getSender(), Message.Performatives.reject, 
					"", "AcquireEquipment", false));
		}
		
		return IFuture.DONE;
	}
}
