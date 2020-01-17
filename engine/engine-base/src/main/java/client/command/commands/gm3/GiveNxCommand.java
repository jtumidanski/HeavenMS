package client.command.commands.gm3;

import java.util.Optional;

import client.MapleCharacter;
import client.MapleClient;
import client.command.Command;
import tools.MessageBroadcaster;
import tools.ServerNoticeType;
import tools.I18nMessage;

public class GiveNxCommand extends Command {
   {
      setDescription("");
   }

   @Override
   public void execute(MapleClient c, String[] params) {
      MapleCharacter player = c.getPlayer();
      if (params.length < 1) {
         MessageBroadcaster.getInstance().yellowMessage(player, I18nMessage.from("GIVE_NX_COMMAND_SYNTAX"));
         return;
      }

      String recv, typeStr = "nx";
      int value, type = 1;
      if (params.length > 1) {
         if (params[0].length() == 2) {
            switch (params[0]) {
               case "mp":  // maplePoint
                  type = 2;
                  break;
               case "np":  // nxPrepaid
                  type = 4;
                  break;
               default:
                  type = 1;
            }
            typeStr = params[0];

            if (params.length > 2) {
               recv = params[1];
               value = Integer.parseInt(params[2]);
            } else {
               recv = c.getPlayer().getName();
               value = Integer.parseInt(params[1]);
            }
         } else {
            recv = params[0];
            value = Integer.parseInt(params[1]);
         }
      } else {
         recv = c.getPlayer().getName();
         value = Integer.parseInt(params[0]);
      }

      Optional<MapleCharacter> victim = c.getWorldServer().getPlayerStorage().getCharacterByName(recv);
      if (victim.isPresent()) {
         victim.get().getCashShop().gainCash(type, value);
         MessageBroadcaster.getInstance().sendServerNotice(player, ServerNoticeType.PINK_TEXT, I18nMessage.from("GIVE_NX_COMMAND_SUCCESS").with(typeStr.toUpperCase()));
      } else {
         MessageBroadcaster.getInstance().sendServerNotice(player, ServerNoticeType.PINK_TEXT, I18nMessage.from("PLAYER_NOT_FOUND").with(params[0]));
      }
   }
}
