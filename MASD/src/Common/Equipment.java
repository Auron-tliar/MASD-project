package Common;

public class Equipment 
{
	protected String name;
	protected Attributes attributes;
	
	public String getName()
	{
		return name;
	}
	
	public Attributes getAttributes()
	{
		return attributes;
	}
	
	public Equipment(String name, Attributes attributes)
	{
		this.name = name;
		this.attributes = attributes;
	}
}
