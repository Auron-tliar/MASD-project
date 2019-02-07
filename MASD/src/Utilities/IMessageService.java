package Utilities;

import jadex.commons.future.IFuture;
import jadex.commons.future.ISubscriptionIntermediateFuture;
import javafx.util.Pair;

import java.util.List;
import java.util.Map;

public interface IMessageService 
{
	//public Pair<ISubscriptionIntermediateFuture<Map<String, Object>>, String> subscribe(String receiver);
	public ISubscriptionIntermediateFuture<Message> subscribe(String receiver);
	public ISubscriptionIntermediateFuture<Message> subscribeType(String receiver, String type);
	
	public void unsubscribe(String receiver);
	
	//public IFuture<Void> send(Map<String, Object> msg);
	public IFuture<Void> send(Message msg);
	
	public List<String> getSubscribersByType(String type);
}
