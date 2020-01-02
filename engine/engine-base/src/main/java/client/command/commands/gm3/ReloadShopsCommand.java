package client.command.commands.gm3;

import client.MapleClient;
import client.command.Command;
import server.MapleShopFactory;


public class ReloadShopsCommand extends Command {
   {
      setDescription("");
   }

   @Override
   public void execute(MapleClient c, String[] params) {
      MapleShopFactory.getInstance().reloadShops();
   }
}
