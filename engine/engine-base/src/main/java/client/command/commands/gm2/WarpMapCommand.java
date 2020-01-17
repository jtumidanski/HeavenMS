package client.command.commands.gm2;

import client.MapleCharacter;
import client.MapleClient;
import client.command.Command;
import server.maps.MapleMap;
import tools.MessageBroadcaster;
import tools.I18nMessage;

public class WarpMapCommand extends Command {
   {
      setDescription("");
   }

   @Override
   public void execute(MapleClient c, String[] params) {
      MapleCharacter player = c.getPlayer();
      if (params.length < 1) {
         MessageBroadcaster.getInstance().yellowMessage(player, I18nMessage.from("WARP_MAP_COMMAND_SYNTAX"));
         return;
      }

      try {
         MapleMap target = c.getChannelServer().getMapFactory().getMap(Integer.parseInt(params[0]));
         if (target == null) {
            MessageBroadcaster.getInstance().yellowMessage(player, I18nMessage.from("WARP_MAP_COMMAND_INVALID_MAP").with(params[0]));
            return;
         }

         player.getMap().getAllPlayers().forEach(victim -> {
            victim.saveLocationOnWarp();
            victim.changeMap(target, target.getRandomPlayerSpawnPoint());
         });
      } catch (Exception ex) {
         MessageBroadcaster.getInstance().yellowMessage(player, I18nMessage.from("WARP_MAP_COMMAND_INVALID_MAP").with(params[0]));
      }
   }
}
