package Common;

import jadex.base.PlatformConfiguration;
import jadex.base.Starter;

public class Main {
    public static void main(String[] args) {
        PlatformConfiguration   config  = PlatformConfiguration.getDefaultNoGui();

        config.addComponent("Utilities.MessageServerAgent.class");
        //config.addComponent("Informers.InformerBDI.class");
        //config.addComponent("Auctioneer.class");

        config.addComponent("Adventurers.AdventurerBDI.class");
        config.addComponent("Adventurers.AdventurerBDI.class");
        Starter.createPlatform(config).get();
    }
}
