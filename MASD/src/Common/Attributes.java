package Common;

import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;

public class Attributes {
	public static final List<String> AttributeNames = Arrays.asList("Might", "Magic", "Cunning");
	
	public static final Integer Count = 3;
	
	public List<Double> Values = new ArrayList<Double>(); 

	public Attributes()
	{
		for (int i = 0; i < Count; i++)
		{
			Values.set(i, 0.0);
		}
	}
	
	public Attributes(List<Double> values)
	{
		for	(int i = 0; i < Count; i++)
		{
			Values.set(i, values.get(i));
		}
	}
	
	public Attributes(Double val1, Double val2, Double val3)
	{
		Values.set(0, val1);
		Values.set(1, val2);
		Values.set(2, val3);
	}
	
	public String ToString()
	{
		String descr = AttributeNames.get(0) + ":\t" + Values.get(0);
		for	(int i = 1; i < Count; i++)
		{
			descr += "\n" + AttributeNames.get(i) + ":\t" + Values.get(i);
		}
		
		return descr;
	}
}
