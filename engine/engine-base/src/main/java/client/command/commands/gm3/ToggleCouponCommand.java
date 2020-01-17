package client.command.commands.gm3;

import client.MapleCharacter;
import client.MapleClient;
import client.command.Command;
import net.server.Server;
import tools.MessageBroadcaster;
import tools.I18nMessage;

public class ToggleCouponCommand extends Command {
   {
      setDescription("");
   }

   @Override
   public void execute(MapleClient c, String[] params) {
      MapleCharacter player = c.getPlayer();
      if (params.length < 1) {
         MessageBroadcaster.getInstance().yellowMessage(player, I18nMessage.from("TOGGLE_COUPON_COMMAND_SYNTAX"));
         return;
      }
      Server.getInstance().toggleCoupon(Integer.parseInt(params[0]));
   }
}
