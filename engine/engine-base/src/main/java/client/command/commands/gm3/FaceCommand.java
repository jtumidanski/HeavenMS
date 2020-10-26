package client.command.commands.gm3;

import client.MapleCharacter;
import client.MapleClient;
import client.MapleStat;
import client.command.Command;
import constants.ItemConstants;
import server.MapleItemInformationProvider;
import tools.I18nMessage;
import tools.MessageBroadcaster;

public class FaceCommand extends Command {
   {
      setDescription("");
   }

   @Override
   public void execute(MapleClient c, String[] params) {
      MapleCharacter player = c.getPlayer();
      if (params.length < 1) {
         MessageBroadcaster.getInstance().yellowMessage(player, I18nMessage.from("FACE_COMMAND_SYNTAX"));
         return;
      }

      try {
         if (params.length == 1) {
            int itemId = Integer.parseInt(params[0]);
            if (!ItemConstants.isFace(itemId) || MapleItemInformationProvider.getInstance().getName(itemId) == null) {
               MessageBroadcaster.getInstance()
                     .yellowMessage(player, I18nMessage.from("FACE_COMMAND_FACE_NOT_FOUND").with(params[0]));
               return;
            }

            player.setFace(itemId);
            player.updateSingleStat(MapleStat.FACE, itemId);
            player.equipChanged();
         } else {
            int itemId = Integer.parseInt(params[1]);
            if (!ItemConstants.isFace(itemId) || MapleItemInformationProvider.getInstance().getName(itemId) == null) {
               MessageBroadcaster.getInstance()
                     .yellowMessage(player, I18nMessage.from("FACE_COMMAND_FACE_NOT_FOUND").with(params[1]));
            }

            c.getChannelServer().getPlayerStorage().getCharacterByName(params[0]).ifPresentOrElse(victim -> {
               victim.setFace(itemId);
               victim.updateSingleStat(MapleStat.FACE, itemId);
               victim.equipChanged();
            }, () -> MessageBroadcaster.getInstance()
                  .yellowMessage(player, I18nMessage.from("FACE_COMMAND_PLAYER_NOT_FOUND").with(params[0])));
         }
      } catch (Exception ignored) {
      }
   }
}
