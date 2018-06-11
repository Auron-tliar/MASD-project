package Utilities;

import java.util.Hashtable;
import java.util.Map;

public class Message
{
	protected final String Sender;
	protected final String Receiver;
	protected final Object Content;
	protected final String Protocol;
	protected final Boolean Broadcast;
	
	public String getSender()
	{
		return Sender;
	}
	
	public String getReceiver()
	{
		return Receiver;
	}
	
	public Object getContent()
	{
		return Content;
	}
	
	public String getProtocol()
	{
		return Protocol;
	}
	
	public Boolean getBroadcast()
	{
		return Broadcast;
	}
	
	public Message (String sender, String receiver, Object content, String protocol, Boolean broadcast)
	{
		Sender = sender;
		Receiver = receiver;
		Content = content;
		Protocol = protocol;
		Broadcast = broadcast;
	}
	
	public Message(Map<String, Object> map)
	{
		Sender = (String)map.get("Sender");
		Receiver = (String)map.get("Receiver");
		Content = map.get("Content");
		Protocol = (String)map.get("Protocol");
		Broadcast = (Boolean)map.get("Broadcast");
	}
	
	public String toString()
	{
		return ("[Message]\tSender: " + Sender + "\tReceiver: " + Receiver + "\tContent: " + Content + "\tProtocol: " + Protocol);
	}
	
	public Map<String, Object> toMap()
	{
		Map<String, Object> map = new Hashtable<String, Object>();
		
		map.put("Sender", Sender);
		map.put("Receiver", Receiver);
		map.put("Content", Content);
		map.put("Protocol", Protocol);
		map.put("Broadcast", Broadcast);
		
		return map;
	}
}
