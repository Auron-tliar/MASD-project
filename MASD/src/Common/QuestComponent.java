package Common;

public class QuestComponent
{
	public enum Types {Arcana, History, Nature, Religion}
	
	protected Integer questId;
	protected Types type;
	protected Attributes requirements;
	protected Double time;
	protected Boolean isDone;
	
	public Integer GetQuestId()
	{
		return questId;
	}	
	
	public Types getType()
	{
		return type;
	}
	
	public Attributes getRequirements()
	{
		return requirements;
	}
	
	public Double getTime()
	{
		return time;
	}
	
	public Boolean getIsDone()
	{
		return isDone;
	}
	
	public QuestComponent(Integer questId, Types type, Attributes requirements, Double time, Boolean isDone)
	{
		this.questId = questId;
		this.type = type;
		this.requirements = requirements;
		this.time = time;
		this.isDone = isDone;
	}
	
	public void Researched()
	{
		isDone = true;
	}
}
