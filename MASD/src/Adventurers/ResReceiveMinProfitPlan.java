package Adventurers;

import Utilities.Message;
import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.annotation.PlanBody;
import jadex.bdiv3.annotation.PlanCapability;
import jadex.bdiv3.annotation.PlanPrecondition;
import jadex.bdiv3.runtime.IPlan;
import jadex.commons.Tuple2;
import jadex.commons.future.IFuture;

@Plan
public class ResReceiveMinProfitPlan 
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

		if (msg.getProtocol() == "FormGroup" && msg.getPerformative() == Message.Performatives.inform)
		{
			try 
			{
				Tuple2<Double, Double> temp = (Tuple2<Double, Double>)msg.getContent();
			}
			catch (Exception ex)
			{
				return false;
			}
			return true;
		}

		return false;
	}
	
	@PlanBody
	public IFuture<Void> body(IPlan plan, Message msg)
	{
		Tuple2<Double, Double> content = (Tuple2<Double, Double>)msg.getContent();
		
		if (capa.minProfit < content.getFirstEntity())
		{
			capa.minProfit = content.getFirstEntity();
		}
		
		if (capa.maxPrice > content.getSecondEntity())
		{
			capa.maxPrice = content.getSecondEntity();
		}
		
		capa.collectedResponces++;
		
		return IFuture.DONE;
	}
	
}
