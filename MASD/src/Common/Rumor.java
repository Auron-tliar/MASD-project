package Common;

import java.util.List;

import jadex.commons.transformation.annotations.IncludeFields;

@IncludeFields(includePrivate=true)
public class Rumor 
{
	private Integer questId;
	private List<QuestComponent> components;
	
	private Quest quest;
	
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
		questId = quest.getQuestId();
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
