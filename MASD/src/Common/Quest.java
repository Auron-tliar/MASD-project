package Common;

import jadex.commons.transformation.annotations.IncludeFields;

@IncludeFields(includePrivate=true)
public class Quest
{
	private Integer questId;
	private String name;
	private Integer reward;
	private Attributes requirements;
	
	public Integer getQuestId()
	{
		return questId;
	}
	
	public String getName()
	{
		return name;
	}
	
	public Integer getReward()
	{
		return reward;
	}
	
	public Attributes getRequirements()
	{
		return requirements;
	}
	
	public Quest() {}
	
	public Quest(Integer questId, String name, Integer reward, Attributes requirements)
	{
		this.questId = questId;
		this.name = name;
		this.reward = reward;
		this.requirements = requirements;
	}
}
