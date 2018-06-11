package Common;

public class Quest {
	public final String Name;
	
	public final Integer Reward;
	
	public final Attributes Requirements;
	
	public Quest(String name, Integer reward, Attributes requirements)
	{
		Name = name;
		Reward = reward;
		Requirements = requirements;
	}
}
