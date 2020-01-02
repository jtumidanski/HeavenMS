package client.command.commands.gm3;

import client.MapleCharacter;
import client.MapleClient;
import client.command.Command;
import net.server.Server;
import tools.MessageBroadcaster;
import tools.ServerNoticeType;

public class FlyCommand extends Command {
   {
      setDescription("");
   }

   @Override
   public void execute(MapleClient c, String[] params) { // fly option will become available for any character of that account
      MapleCharacter player = c.getPlayer();
      if (params.length < 1) {
         player.yellowMessage("Syntax: !fly <on/off>");
         return;
      }

      Integer accountId = c.getAccID();
      Server srv = Server.getInstance();
      String sendStr = "";
      if (params[0].equalsIgnoreCase("on")) {
         sendStr += "Enabled Fly feature (F1). With fly active, you cannot attack.";
         if (!srv.canFly(accountId)) sendStr += " Re-login to take effect.";

         srv.changeFly(c.getAccID(), true);
      } else {
         sendStr += "Disabled Fly feature. You can now attack.";
         if (srv.canFly(accountId)) sendStr += " Re-login to take effect.";

         srv.changeFly(c.getAccID(), false);
      }

      MessageBroadcaster.getInstance().sendServerNotice(player, ServerNoticeType.LIGHT_BLUE, sendStr);
   }
}
