package client.command.commands.gm3;

import java.util.Optional;

import client.MapleCharacter;
import client.MapleClient;
import client.MapleStat;
import client.command.Command;
import constants.ItemConstants;
import server.MapleItemInformationProvider;
import tools.I18nMessage;
import tools.MessageBroadcaster;

public class HairCommand extends Command {
   {
      setDescription("");
   }

   @Override
   public void execute(MapleClient c, String[] params) {
      MapleCharacter player = c.getPlayer();
      if (params.length < 1) {
         MessageBroadcaster.getInstance().yellowMessage(player, I18nMessage.from("HAIR_COMMAND_SYNTAX"));
         return;
      }

      try {
         if (params.length == 1) {
            int itemId = Integer.parseInt(params[0]);
            if (!ItemConstants.isHair(itemId) || MapleItemInformationProvider.getInstance().getName(itemId) == null) {
               MessageBroadcaster.getInstance()
                     .yellowMessage(player, I18nMessage.from("HAIR_COMMAND_HAIR_DOES_NOT_EXIST").with(params[0]));
               return;
            }

            player.setHair(itemId);
            player.updateSingleStat(MapleStat.HAIR, itemId);
            player.equipChanged();
         } else {
            int itemId = Integer.parseInt(params[1]);
            if (!ItemConstants.isHair(itemId) || MapleItemInformationProvider.getInstance().getName(itemId) == null) {
               MessageBroadcaster.getInstance()
                     .yellowMessage(player, I18nMessage.from("HAIR_COMMAND_HAIR_DOES_NOT_EXIST").with(params[1]));
               return;
            }

            Optional<MapleCharacter> victim = c.getChannelServer().getPlayerStorage().getCharacterByName(params[0]);
            if (victim.isPresent()) {
               victim.get().setHair(itemId);
               victim.get().updateSingleStat(MapleStat.HAIR, itemId);
               victim.get().equipChanged();
            } else {
               MessageBroadcaster.getInstance()
                     .yellowMessage(player, I18nMessage.from("HAIR_COMMAND_PLAYER_NOT_FOUND").with(params[0]));
            }
         }
      } catch (Exception ignored) {
      }
   }
}
