package Overseers;

import java.util.Hashtable;
import java.util.Map;

import Utilities.IMessageService;
import Utilities.Message;
import Common.Attributes;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.bridge.service.types.cms.CreationInfo;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.commons.SUtil;
import jadex.commons.future.IFuture;
import jadex.commons.future.ISubscriptionIntermediateFuture;
import jadex.commons.future.ITuple2Future;
import jadex.commons.future.IntermediateDefaultResultListener;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.AgentFeature;
import jadex.micro.annotation.Binding;
import jadex.micro.annotation.Description;
import jadex.micro.annotation.RequiredService;
import jadex.micro.annotation.RequiredServices;

@Agent
@Description("Overseer reads the input from player and sends the corresponding parameter "
		+ "updates to the agents")
@RequiredServices(
{
	@RequiredService(name="cms", type=IComponentManagementService.class, multiple=false,
	binding=@Binding(scope=RequiredServiceInfo.SCOPE_PLATFORM)),
	@RequiredService(name="messageServer", type=IMessageService.class, multiple=false,
	binding=@Binding(scope=RequiredServiceInfo.SCOPE_PLATFORM, dynamic=true))
})
public class OverseerAgent 
{
	@Agent
	IInternalAccess agent;

	@AgentFeature
	protected IRequiredServicesFeature requiredServicesFeature;
	
	protected IComponentManagementService cms;
	protected IMessageService messageServer;
	
	protected Map<String, IComponentIdentifier> agentsList;
	
	protected Integer lastIndex = -1;
	
	@AgentBody
	public void body()
	{
		agentsList = new Hashtable<String, IComponentIdentifier>();
		
		IFuture<Object> temp = requiredServicesFeature.getRequiredService("cms");
		cms = (IComponentManagementService)temp.get();
		
		createAgent("Utilities.MessageServerAgent.class", new String[]{}, new Object[]{});
		createAgent("Adventurers.AdventurerBDI.class", new String[]{"name", "attributes"}, new Object[]{"Alice", new Attributes()});
		createAgent("Adventurers.AdventurerBDI.class", new String[]{"name", "attributes"}, new Object[]{"Bob", new Attributes()});
		

		
		temp = requiredServicesFeature.getRequiredService("messageServer");
		messageServer = (IMessageService)temp.get();
		
		
		final ISubscriptionIntermediateFuture<Message> sub = messageServer.subscribe("Overseer");
		
		/// Change Adventurers to other types ///
		final ISubscriptionIntermediateFuture<Message> subType = messageServer.subscribeType("Overseers");
		
		sub.addResultListener(new IntermediateDefaultResultListener<Message>()
		{
			public void intermediateResultAvailable(Message msg)
			{
				if (msg.getPerformative() == Message.Performatives.request && (String)msg.getContent() == "Remove")
				{
					cms.destroyComponent(agentsList.get(msg.getSender()));
					createAgent("Adventurers.AdventurerBDI.class", new String[]{"name", "attributes"},
							new Object[]{"Herbert", new Attributes()});
				}
				else if (msg.getPerformative() == Message.Performatives.inform && msg.getProtocol() == "Retirement")
				{
					cms.destroyComponent(agentsList.get(msg.getSender()));

					createAgent("Adventurers.AdventurerBDI.class", new String[]{"name", "attributes"},
							new Object[]{"Wilbur", new Attributes()});
				}
				
			}
		});
		
		subType.addResultListener(new IntermediateDefaultResultListener<Message>()
		{
			public void intermediateResultAvailable(Message msg)
			{
				System.out.println(msg);
			}
		});
	}
	
	
	protected void createAgent(String agentName, String[] paramNames,  Object[] paramValues)
	{
		lastIndex++;
		String[] fullParamNames = new String[paramNames.length + 1]; 
		Object[] fullParamValues = new Object[paramValues.length + 1]; 
		
		for (int i = 0; i < paramNames.length; i++)
		{
			fullParamNames[i] = paramNames[i];
			fullParamValues[i] = paramValues[i];
		}
		fullParamNames[paramNames.length] = "id";
		fullParamValues[paramValues.length] = lastIndex.toString();
		
		CreationInfo ci = new CreationInfo(SUtil.createHashMap(fullParamNames, fullParamValues));
		ITuple2Future<IComponentIdentifier, Map<String, Object>> fut = cms.createComponent(agentName, ci);
		IComponentIdentifier cid = fut.getFirstResult();
		agentsList.put(lastIndex.toString(), cid);
	}
}
