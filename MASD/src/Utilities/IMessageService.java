package Utilities;

import jadex.commons.future.IFuture;
import jadex.commons.future.ISubscriptionIntermediateFuture;
import javafx.util.Pair;

import java.util.Map;

public interface IMessageService 
{
	public Pair<ISubscriptionIntermediateFuture<Map<String, Object>>, String> subscribe(String receiver);
	public ISubscriptionIntermediateFuture<Map<String, Object>> subscribeType(String type);
	
	public IFuture<Void> send(Map<String, Object> msg);
}
