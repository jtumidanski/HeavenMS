package client.command.commands.gm2;

import java.awt.Point;
import java.util.Collection;

import client.MapleCharacter;
import client.MapleClient;
import client.command.Command;
import server.maps.MapleMap;
import tools.MessageBroadcaster;
import tools.I18nMessage;

public class WarpAreaCommand extends Command {
   {
      setDescription("");
   }

   @Override
   public void execute(MapleClient c, String[] params) {
      MapleCharacter player = c.getPlayer();
      if (params.length < 1) {
         MessageBroadcaster.getInstance().yellowMessage(player, I18nMessage.from("WARP_AREA_COMMAND_SYNTAX"));
         return;
      }

      try {
         MapleMap target = c.getChannelServer().getMapFactory().getMap(Integer.parseInt(params[0]));
         if (target == null) {
            MessageBroadcaster.getInstance().yellowMessage(player, I18nMessage.from("WARP_AREA_COMMAND_INVALID_MAP").with(params[0]));
            return;
         }

         Point pos = player.position();

         Collection<MapleCharacter> characters = player.getMap().getAllPlayers();

         for (MapleCharacter victim : characters) {
            if (victim.position().distanceSq(pos) <= 50000) {
               victim.saveLocationOnWarp();
               victim.changeMap(target, target.getRandomPlayerSpawnPoint());
            }
         }
      } catch (Exception ex) {
         MessageBroadcaster.getInstance().yellowMessage(player, I18nMessage.from("WARP_AREA_COMMAND_INVALID_MAP").with(params[0]));
      }
   }
}
