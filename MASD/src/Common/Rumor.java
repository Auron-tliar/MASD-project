package Common;

import java.util.List;

public class Rumor 
{
	protected Integer questId;
	protected List<QuestComponent> components;
	
	protected Quest quest;
	
	public Integer getQuestId()
	{
		return questId;
	}
	
	public List<QuestComponent> getComponents()
	{
		return components;
	}
	
	public Rumor(Quest quest, List<QuestComponent> components)
	{
		questId = quest.questId;
		this.quest = quest;
		this.components = components;
	}
	
	public Quest Finish()
	{
		for (QuestComponent comp : components)
		{
			if (!comp.getIsDone())
			{
				return null;
			}
		}
		
		return quest;
	}
}
