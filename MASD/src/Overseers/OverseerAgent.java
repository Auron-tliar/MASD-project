package Overseers;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Stream;

import Utilities.IMessageService;
import Utilities.Message;
import Common.Attributes;
import Common.Quest;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IExecutionFeature;
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
import jadex.micro.annotation.AgentService;
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
	
	@AgentFeature
	protected IExecutionFeature exec;
	
	@AgentService
	protected IComponentManagementService cms;
	
	protected IMessageService messageServer;
	
	protected Map<String, IComponentIdentifier> agentsList;
	
	protected Integer lastIndex = -1;
	
	protected Integer lastQId = -1;
	
	protected Double earnings = 0.0;
	
	
	protected Integer nAdventurers = 3;
	protected Integer nInformers = 3;
	protected Integer nCrafters = 2;
	protected double minAttr = 0.0;
	protected double maxAttr = 10.0;
	protected double minStartingGold = 15.0;
	protected double maxStartingGold = 30.0;
	protected Double minPrice = 0.1;
	protected Double minIncrease = 0.1;
	protected List<String> questNames = new ArrayList<String>();
	protected List<String> charNames  = new ArrayList<String>();
	
	
	@AgentBody
	public void body()
	{
		agentsList = new Hashtable<String, IComponentIdentifier>();
		
		readConfig("D:\\Dropbox\\UPC\\MASD\\MASD-project\\MASD\\config.txt");
		
		createAgent("Utilities.MessageServerAgent.class", new String[]{}, new Object[]{});
		
		for (int i = 0; i < nAdventurers; i++)
		{
			createAgent("Adventurers.AdventurerBDI.class", new String[]{"name", "baseAttributes", "startingGold"},
				new Object[]{charNames.get(ThreadLocalRandom.current().nextInt(charNames.size())),
						new Attributes((double)ThreadLocalRandom.current().nextInt((int)minAttr, (int)maxAttr + 1),
								(double)ThreadLocalRandom.current().nextInt((int)minAttr, (int)maxAttr + 1),
								(double)ThreadLocalRandom.current().nextInt((int)minAttr, (int)maxAttr + 1)),
						ThreadLocalRandom.current().nextDouble(minStartingGold, maxStartingGold)});
		}
		
		/*createAgent("Adventurers.AdventurerBDI.class", new String[]{"name", "baseAttributes", "startingGold"},
				new Object[]{"Alice", new Attributes(10.0,10.0,10.0), 30.0});
		createAgent("Adventurers.AdventurerBDI.class", new String[]{"name", "baseAttributes", "startingGold"},
				new Object[]{"Bob", new Attributes(10.0,10.0,10.0), 25.0});*/

		
		IFuture<Object> temp = requiredServicesFeature.getRequiredService("messageServer");
		messageServer = (IMessageService)temp.get();
		
		
		final ISubscriptionIntermediateFuture<Message> sub = messageServer.subscribe("Overseer");
		final ISubscriptionIntermediateFuture<Message> subType = messageServer.subscribeType("Overseer", "Overseers");
		
		sub.addResultListener(new IntermediateDefaultResultListener<Message>()
		{
			public void intermediateResultAvailable(Message msg)
			{
				if (msg.getPerformative() == Message.Performatives.inform && msg.getProtocol() == "Death")
				{
					cms.destroyComponent(agentsList.get(msg.getSender()));
					if ((String)msg.getContent() == "Adventurer")
					{
						createAgent("Adventurers.AdventurerBDI.class", new String[]{"name", "attributes", "startingGold"},
							new Object[]{"Herbert", new Attributes(), ThreadLocalRandom.current().nextDouble(10.0, 20.0)});
					}
				}
				else if (msg.getPerformative() == Message.Performatives.inform && msg.getProtocol() == "Retirement")
				{
					cms.destroyComponent(agentsList.get(msg.getSender()));

					createAgent("Adventurers.AdventurerBDI.class", new String[]{"name", "attributes", "startingGold"},
							new Object[]{"Wilbur", new Attributes(), ThreadLocalRandom.current().nextDouble(10.0, 20.0)});
				}
				else if (msg.getPerformative() == Message.Performatives.payment)
				{
					earnings += (Double)msg.getContent();
					System.out.println("Overseer: total earnings = " + earnings);
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
		
		System.out.println("Overseer has arrived!");
		
		

		
		/*Quest tempQuest1 = new Quest(1, "Dummy quest 1", 50.0, new Common.Attributes(3.0,3.0,3.0));
		Common.Quest tempQuest2 = new Common.Quest(2, "Dummy quest 2", 500.0, new Common.Attributes(8.0,8.0,8.0));*/
		
		
		
		IComponentStep<Void> step = new IComponentStep<Void>()
		{
			public IFuture<Void> execute(IInternalAccess agent)
			{
				while(true)
				{
					lastQId++;
					Attributes attrs = new Attributes((double)ThreadLocalRandom.current().nextInt(0, (int)maxAttr + 1),
							(double)ThreadLocalRandom.current().nextInt(0, (int)maxAttr + 1),
							(double)ThreadLocalRandom.current().nextInt(0, (int)maxAttr + 1));
					Double reward = ThreadLocalRandom.current().nextDouble(2.0, 4.0) * attrs.total();
					Quest tempQuest = new Quest(lastQId, "Quest " + lastQId, reward, attrs);
					
					createAgent("Informers.AuctioneerAgent.class", new String[]{"quest", "minPrice", "minIncrease", "owner"},
							new Object[]{tempQuest, Math.max((int)(reward * minPrice), 1.0), 
									Math.max((int)(reward * minPrice * minIncrease), 1.0), "Overseer"});
					
					exec.waitForDelay(ThreadLocalRandom.current().nextLong(15000, 40000)).get();
				}
				//return IFuture.DONE;
			}
		};
		exec.waitForDelay(5000, step);

		
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
	
	protected String readConfig(String filePath)
	{
	    StringBuilder contentBuilder = new StringBuilder();
	    try (Stream<String> stream = Files.lines( Paths.get(filePath), StandardCharsets.UTF_8))
	    {
	        stream.forEach(s -> {
	        	String[] split = s.split("=");
	        	
	        	switch (split[0])
	        	{
	        	case "Adventurers":
	        		nAdventurers = Integer.parseInt(split[1]);
	        		break;
	        	case "Informers":
        			nInformers = Integer.parseInt(split[1]);
	        		break;
	        	case "Crafters":
        			nCrafters = Integer.parseInt(split[1]);
	        		break;
	        	case "MinStartingAttributes":
	        		minAttr = Double.parseDouble(split[1]);
	        		break;
	        	case "MaxStartingAttributes":
	        		maxAttr = Double.parseDouble(split[1]);
	        		break;
	        	case "MinStartingGold":
	        		minStartingGold = Double.parseDouble(split[1]);
	        		break;
	        	case "MaxStartingGold":
	        		maxStartingGold = Double.parseDouble(split[1]);
	        		break;
	        	case "MinPrice":
	        		minPrice = Double.parseDouble(split[1]);
	        		break;
	        	case "MinIncrease":
	        		minIncrease = Double.parseDouble(split[1]);
	        		break;
	        	case "QuestNames":
	        		if (split.length > 1)
	        		{
	        			
	        		}
	        		break;
	        	case "Names":
	        		charNames = Arrays.asList(split[1].split(","));
	        		break;
        		default:
	        		break;
	        	}
	        });
	    }
	    catch (IOException e)
	    {
	        e.printStackTrace();
	    }
	    return contentBuilder.toString();
	}
}
