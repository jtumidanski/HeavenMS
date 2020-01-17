package client.command.commands.gm3;

import client.MapleCharacter;
import client.MapleClient;
import client.command.Command;
import net.server.Server;
import tools.MessageBroadcaster;
import tools.ServerNoticeType;
import tools.I18nMessage;

public class FlyCommand extends Command {
   {
      setDescription("");
   }

   @Override
   public void execute(MapleClient c, String[] params) { // fly option will become available for any character of that account
      MapleCharacter player = c.getPlayer();
      if (params.length < 1) {
         MessageBroadcaster.getInstance().yellowMessage(player, I18nMessage.from("FLY_COMMAND_SYNTAX"));
         return;
      }

      Server srv = Server.getInstance();
      if (params[0].equalsIgnoreCase("on")) {
         MessageBroadcaster.getInstance().sendServerNotice(player, ServerNoticeType.LIGHT_BLUE, I18nMessage.from("FLY_COMMAND_ON"));
         srv.changeFly(c.getAccID(), true);
      } else {
         MessageBroadcaster.getInstance().sendServerNotice(player, ServerNoticeType.LIGHT_BLUE, I18nMessage.from("FLY_COMMAND_OFF"));
         srv.changeFly(c.getAccID(), false);
      }
   }
}
