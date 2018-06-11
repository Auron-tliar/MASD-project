package Common;

import jadex.commons.transformation.annotations.IncludeFields;

@IncludeFields(includePrivate=true)
public class Equipment 
{
	private String name;
	private Attributes attributes;
	
	public String getName()
	{
		return name;
	}
	
	public Attributes getAttributes()
	{
		return attributes;
	}
	
	public Equipment() {}
	
	public Equipment(String name, Attributes attributes)
	{
		this.name = name;
		this.attributes = attributes;
	}
}
