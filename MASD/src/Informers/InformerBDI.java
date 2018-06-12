package Informers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Common.Quest;
import jadex.bdiv3.annotation.Belief;
import jadex.bdiv3.annotation.Goal;
import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.annotation.PlanAborted;
import jadex.bdiv3.annotation.PlanBody;
import jadex.bdiv3.annotation.PlanFailed;
import jadex.bdiv3.annotation.PlanPassed;
import jadex.bdiv3.annotation.Trigger;
import jadex.bdiv3.features.IBDIAgentFeature;
import jadex.bdiv3.runtime.ChangeEvent;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.AgentFeature;
import jadex.micro.annotation.AgentKilled;
import jadex.micro.annotation.Description;
import jadex.rules.eca.ChangeInfo;

@Agent
@Description("The informer agent")
public class InformerBDI {

	@AgentFeature 
	protected IBDIAgentFeature bdiFeature;
	
	@Belief
	protected int gold;
	
	@Belief
	protected List<String> rawQuest;
	
	@AgentBody
	public void body()
	{
	  //bdiFeature.adoptGoal(new MaintainGoldGoal());
	}
	
	@AgentKilled
	public void shutdown()
	{

	}
	
	
	@Goal
	public class SellQuest {
		protected Quest quest;
		protected int price;
		protected String buyer; // Change type later
		
		public SellQuest(Quest quest) {
			
		}
		
		public Quest getQuest() {
			return this.quest;
		}
		
		// Set after the sale? 
		public void setPrice(int price) {
			this.price = price;
		}

		public int getPrice() {
			return this.price;
		}
		
	}
	
	@Goal
	public class FindRawQuest {
		
	}
	
	@Plan(trigger=@Trigger(factaddeds="rawQuest"))
	public void receiveRawQuestPlan(ChangeEvent event)
	{
	    ChangeInfo<String> change = ((ChangeInfo<String>)event.getValue());
	}
	
	@Plan
	public class ProcessQuestPlan
	{
	
	  public ProcessQuestPlan()
	  {
	  }
	
	  @PlanBody
	  public void processQuest()
	  {
		  // Process
	  }
	  
	  @PlanPassed
	  public void passed()
	  {
	    System.out.println("Processing quest finished successfully.");
	  }

	  @PlanAborted
	  public void aborted()
	  {
	    System.out.println("Processing quest aborted.");
	  }

	  @PlanFailed
	  public void failed(Exception e)
	  {
	    System.out.println("Processing quest failed: "+e);
	  }
	}
	
	@Plan
	public class SellQuestPlan
	{
	
	  public SellQuestPlan()
	  {
		  // Get an auctioneer
	  }
	
	  @PlanBody
	  public void processQuest()
	  {
		  // Process
	  }
	  
	  @PlanPassed
	  public void passed()
	  {
	    System.out.println("Plan finished successfully.");
	  }

	  @PlanAborted
	  public void aborted()
	  {
	    System.out.println("Plan aborted.");
	  }

	  @PlanFailed
	  public void failed(Exception e)
	  {
	    System.out.println("Plan failed: "+e);
	  }
	}
}
