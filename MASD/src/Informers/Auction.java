package Informers;

import Common.Quest;
import jadex.commons.transformation.annotations.IncludeFields;

@IncludeFields(includePrivate=true)
public class Auction
{
	private Integer id;
	private Integer startCost;
	private Integer reward;
	private Common.Attributes requirements;
	private Common.Quest quest;
	private long startTime;
	private Integer minimalBid;
	private long maxWait;
	
	public Integer getId()
	{
		return id;
	}
	
	public Integer getStartCost()
	{
		return startCost;
	}
	
	public Integer getReward()
	{
		return reward;
	}
	
	public Common.Attributes getRequirements()
	{
		return requirements;
	}
	
	public long getStartTime()
	{
		return startTime;
	}
	
	public Integer getMinimalBid()
	{
		return minimalBid;
	}
	
	public long getMaxWait()
	{
		return maxWait;
	}
	
	public Auction() {}
	
	public Auction(Quest quest, Integer startCost, long startTime, Integer minimalBid, long maxWait)
	{
		id = quest.getQuestId();
		this.startCost = startCost;
		this.quest = quest;
		reward = quest.getReward();
		requirements = quest.getRequirements();
		this.startTime = startTime;
		this.minimalBid = minimalBid;
		this.maxWait = maxWait;
	}
	
}
