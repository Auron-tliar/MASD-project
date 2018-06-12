package Common;

import java.util.ArrayList;
import java.util.List;

import jadex.commons.transformation.annotations.IncludeFields;

import java.util.Arrays;

@IncludeFields(includePrivate=true)
public class Attributes 
{
	private static List<String> attributeNames = Arrays.asList("Might", "Magic", "Cunning");
	private static Integer count = 3;
	
	public static List<String> getAttributeNames()
	{
		return attributeNames;		
	}
	
	public static Integer getCount()
	{
		return count;
	}
	
	public List<Double> Values; 

	public Attributes()
	{
		Values = new ArrayList<Double>(); 
		for (int i = 0; i < count; i++)
		{
			Values.add(0.0);
		}
	}
	
	public Attributes(List<Double> values)
	{
		Values = new ArrayList<Double>(); 
		for	(int i = 0; i < count; i++)
		{
			Values.add(values.get(i));
		}
	}
	
	public Attributes(Double val1, Double val2, Double val3)
	{
		Values = new ArrayList<Double>(); 
		Values.add(val1);
		Values.add(val2);
		Values.add(val3);
	}
	
	public String toString()
	{
		String descr = attributeNames.get(0) + ":\t" + Values.get(0);
		for	(int i = 1; i < count; i++)
		{
			descr += "\n" + attributeNames.get(i) + ": " + Values.get(i);
		}
		
		return descr;
	}
	
	public Boolean greaterOrEqual(Attributes attributes)
	{
		for (int i = 0; i < count; i++) 
		{
			if (Values.get(i) < attributes.Values.get(i))
			{
				return false;
			}
		}
		
		return true;
	}
	
	public Attributes sum(Attributes attributes)
	{
		List<Double> vals = new ArrayList<Double>();
		
		for (int i = 0; i < count; i++)
		{
			vals.add(Values.get(i) + attributes.Values.get(i));
		}
		
		return new Attributes(vals);
	}
	
	public Double mean()
	{
		Double res = 0.0;
		
		for (int i = 0; i < count; i++)
		{
			res += Values.get(i);
		}
		
		return res / count;
	}
	
	public Integer lowest()
	{
		Double minVal = 100000.0;
		Integer minInd = -1;
		
		for (int i = 0; i < count; i++)
		{
			if (Values.get(i) < minVal)
			{
				minVal = Values.get(i);
				minInd = i;
			}
		}
		
		return minInd;
	}
}
