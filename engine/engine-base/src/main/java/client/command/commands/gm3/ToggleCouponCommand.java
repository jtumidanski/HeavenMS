package client.command.commands.gm3;

import client.MapleCharacter;
import client.MapleClient;
import client.command.Command;
import net.server.Server;

public class ToggleCouponCommand extends Command {
   {
      setDescription("");
   }

   @Override
   public void execute(MapleClient c, String[] params) {
      MapleCharacter player = c.getPlayer();
      if (params.length < 1) {
         player.yellowMessage("Syntax: !togglecoupon <item id>");
         return;
      }
      Server.getInstance().toggleCoupon(Integer.parseInt(params[0]));
   }
}
