package Utilities;

import java.util.Hashtable;
import java.util.Map;

public class Message
{
	public enum Performatives {accept, agree, confirm, failure, inform, not_understood, propose, query, refuse, reject}
	
	protected final String sender;
	protected final String receiver;
	protected final Performatives performative;
	protected final Object content;
	protected final String protocol;
	protected final Boolean broadcast;
	
	public String getSender()
	{
		return sender;
	}
	
	public String getReceiver()
	{
		return receiver;
	}
	
	public Performatives getPerformative()
	{
		return performative;
	}
	
	public Object getContent()
	{
		return content;
	}
	
	public String getProtocol()
	{
		return protocol;
	}
	
	public Boolean getBroadcast()
	{
		return broadcast;
	}
	
	public Message (String sender, String receiver, Performatives performative, Object content, String protocol, Boolean broadcast)
	{
		this.sender = sender;
		this.receiver = receiver;
		this.performative = performative;
		this.content = content;
		this.protocol = protocol;
		this.broadcast = broadcast;
	}
	
	public Message(Map<String, Object> map)
	{
		this.sender = (String)map.get("Sender");
		this.receiver = (String)map.get("Receiver");
		this.performative = (Performatives)map.get("Performative");
		this.content = map.get("Content");
		this.protocol = (String)map.get("Protocol");
		this.broadcast = (Boolean)map.get("Broadcast");
	}
	
	public String toString()
	{
		return ("[Message: " + performative.toString() + "]\tSender: " + sender + "\tReceiver: " + receiver +
				"\tContent: " + content + "\tProtocol: " + protocol + "\tBroadcast: " + broadcast);
	}
	
	public Map<String, Object> toMap()
	{
		Map<String, Object> map = new Hashtable<String, Object>();
		
		map.put("Sender", sender);
		map.put("Receiver", receiver);
		map.put("Performative", performative);
		map.put("Content", content);
		map.put("Protocol", protocol);
		map.put("Broadcast", broadcast);
		
		return map;
	}
}
