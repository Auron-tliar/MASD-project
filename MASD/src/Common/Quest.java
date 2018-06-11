package Common;

public class Quest
{
	protected Integer questId;
	protected String name;
	protected Integer reward;
	protected Attributes requirements;
	
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
	
	public Quest(Integer questId, String name, Integer reward, Attributes requirements)
	{
		this.questId = questId;
		this.name = name;
		this.reward = reward;
		this.requirements = requirements;
	}
}
