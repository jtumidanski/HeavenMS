package client.command.commands.gm3;

import client.MapleCharacter;
import client.MapleClient;
import client.MapleStat;
import client.command.Command;
import constants.inventory.ItemConstants;
import server.MapleItemInformationProvider;

public class FaceCommand extends Command {
   {
      setDescription("");
   }

   @Override
   public void execute(MapleClient c, String[] params) {
      MapleCharacter player = c.getPlayer();
      if (params.length < 1) {
         player.yellowMessage("Syntax: !face [<player name>] <face id>");
         return;
      }

      try {
         if (params.length == 1) {
            int itemId = Integer.parseInt(params[0]);
            if (!ItemConstants.isFace(itemId) || MapleItemInformationProvider.getInstance().getName(itemId) == null) {
               player.yellowMessage("Face id '" + params[0] + "' does not exist.");
               return;
            }

            player.setFace(itemId);
            player.updateSingleStat(MapleStat.FACE, itemId);
            player.equipChanged();
         } else {
            int itemId = Integer.parseInt(params[1]);
            if (!ItemConstants.isFace(itemId) || MapleItemInformationProvider.getInstance().getName(itemId) == null) {
               player.yellowMessage("Face id '" + params[1] + "' does not exist.");
            }

            c.getChannelServer().getPlayerStorage().getCharacterByName(params[0]).ifPresentOrElse(victim -> {
               victim.setFace(itemId);
               victim.updateSingleStat(MapleStat.FACE, itemId);
               victim.equipChanged();
            }, () -> player.yellowMessage("Player '" + params[0] + "' has not been found on this channel."));
         }
      } catch (Exception ignored) {
      }

   }
}
