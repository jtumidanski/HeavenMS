package client.command.commands.gm3;

import java.util.Optional;

import client.MapleCharacter;
import client.MapleClient;
import client.MapleStat;
import client.command.Command;
import constants.inventory.ItemConstants;
import server.MapleItemInformationProvider;

public class HairCommand extends Command {
   {
      setDescription("");
   }

   @Override
   public void execute(MapleClient c, String[] params) {
      MapleCharacter player = c.getPlayer();
      if (params.length < 1) {
         player.yellowMessage("Syntax: !hair [<player name>] <hair id>");
         return;
      }

      try {
         if (params.length == 1) {
            int itemId = Integer.parseInt(params[0]);
            if (!ItemConstants.isHair(itemId) || MapleItemInformationProvider.getInstance().getName(itemId) == null) {
               player.yellowMessage("Hair id '" + params[0] + "' does not exist.");
               return;
            }

            player.setHair(itemId);
            player.updateSingleStat(MapleStat.HAIR, itemId);
            player.equipChanged();
         } else {
            int itemId = Integer.parseInt(params[1]);
            if (!ItemConstants.isHair(itemId) || MapleItemInformationProvider.getInstance().getName(itemId) == null) {
               player.yellowMessage("Hair id '" + params[1] + "' does not exist.");
               return;
            }

            Optional<MapleCharacter> victim = c.getChannelServer().getPlayerStorage().getCharacterByName(params[0]);
            if (victim.isPresent()) {
               victim.get().setHair(itemId);
               victim.get().updateSingleStat(MapleStat.HAIR, itemId);
               victim.get().equipChanged();
            } else {
               player.yellowMessage("Player '" + params[0] + "' has not been found on this channel.");
            }
         }
      } catch (Exception ignored) {
      }
   }
}
