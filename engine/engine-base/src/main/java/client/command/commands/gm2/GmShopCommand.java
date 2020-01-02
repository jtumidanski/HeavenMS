package client.command.commands.gm2;

import client.MapleClient;
import client.command.Command;
import server.MapleShopFactory;
import server.processor.MapleShopProcessor;

public class GmShopCommand extends Command {
   {
      setDescription("");
   }

   @Override
   public void execute(MapleClient c, String[] params) {
      MapleShopProcessor.getInstance().sendShop(MapleShopFactory.getInstance().getShop(1337), c);
   }
}
