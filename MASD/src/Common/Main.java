package Common;

import jadex.base.PlatformConfiguration;
import jadex.base.Starter;

public class Main {
    public static void main(String[] args) {
        //PlatformConfiguration   config  = PlatformConfiguration.getDefault();
        PlatformConfiguration   config  = PlatformConfiguration.getDefaultNoGui();

        config.setChat(false);
        //config.addComponent("Utilities.MessageServerAgent.class");
        //config.addComponent("Informers.InformerBDI.class");
        //config.addComponent("Auctioneer.class");

        //config.addComponent("Adventurers.AdventurerBDI.class");
        //config.addComponent("Adventurers.AdventurerBDI.class");
        config.addComponent("Overseers.OverseerAgent.class");
        Starter.createPlatform(config).get();
    }
}
