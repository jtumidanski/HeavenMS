package client.command.commands.gm2;

import java.awt.Point;
import java.util.Collection;

import client.MapleCharacter;
import client.MapleClient;
import client.command.Command;
import server.maps.MapleMap;

public class WarpAreaCommand extends Command {
   {
      setDescription("");
   }

   @Override
   public void execute(MapleClient c, String[] params) {
      MapleCharacter player = c.getPlayer();
      if (params.length < 1) {
         player.yellowMessage("Syntax: !warparea <map id>");
         return;
      }

      try {
         MapleMap target = c.getChannelServer().getMapFactory().getMap(Integer.parseInt(params[0]));
         if (target == null) {
            player.yellowMessage("Map ID " + params[0] + " is invalid.");
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
         player.yellowMessage("Map ID " + params[0] + " is invalid.");
      }
   }
}
