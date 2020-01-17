package client.command.commands.gm2;

import client.MapleCharacter;
import client.MapleClient;
import client.command.Command;
import tools.MessageBroadcaster;
import tools.I18nMessage;

public class SetStatCommand extends Command {
   {
      setDescription("");
   }

   @Override
   public void execute(MapleClient c, String[] params) {
      MapleCharacter player = c.getPlayer();
      if (params.length < 1) {
         MessageBroadcaster.getInstance().yellowMessage(player, I18nMessage.from("SET_STAT_COMMAND_SYNTAX"));
         return;
      }

      try {
         int x = Integer.parseInt(params[0]);

         if (x > Short.MAX_VALUE) {
            x = Short.MAX_VALUE;
         } else if (x < 4) {
            x = 4;
         }

         player.updateStrDexIntLuk(x);
      } catch (NumberFormatException ignored) {
      }
   }
}
