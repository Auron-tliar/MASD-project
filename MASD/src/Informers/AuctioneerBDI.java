package Informers;

import Common.Quest;
import jadex.bdiv3.annotation.Belief;
import jadex.bdiv3.annotation.Goal;
import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.annotation.Trigger;
import jadex.bdiv3.runtime.ChangeEvent;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.AgentKilled;
import jadex.micro.annotation.Description;
import jadex.rules.eca.ChangeInfo;

@Agent
@Description("The auctioneer agent")
public class AuctioneerBDI {
	
	@Belief
	protected Quest quest;
	
	@Belief
	protected Integer auctionTimeout = 10000;
	
	@AgentBody
	public void body()
	{
	  //bdiFeature.adoptPlan(new ProcessQuestPlan());
	}
	
	
	@AgentKilled
	public void shutdown()
	{

	}
	
	@Goal
	public class ManageAuction {
		
	}
	
	
	@Goal
	public class SellQuest {
		
	}

	@Plan
	public class SetupAuction
	{
		public SetupAuction() {
			
		}
	}
}
