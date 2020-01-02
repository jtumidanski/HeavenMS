package client.command.commands.gm4;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import client.MapleCharacter;
import client.MapleClient;
import client.command.Command;
import database.provider.PlayerLifeProvider;
import database.DatabaseConnection;
import tools.Pair;

public class RemovePlayerNpcCommand extends Command {
   {
      setDescription("");
   }

   @Override
   public void execute(MapleClient c, String[] params) {
      MapleCharacter player = c.getPlayer();

      int mapId = player.getMapId();
      int npcId = params.length > 0 ? Integer.parseInt(params[0]) : -1;

      Point pos = player.position();
      int xPosition = pos.x;
      int yPosition = pos.y;

      List<Pair<Integer, Pair<Integer, Integer>>> toRemove = DatabaseConnection.getInstance().withConnectionResult(connection -> {
         if (npcId > -1) {
            return PlayerLifeProvider.getInstance().get(connection, player.getWorld(), mapId, "n", npcId);
         } else {
            return PlayerLifeProvider.getInstance().get(connection, player.getWorld(), mapId, "n", xPosition - 50, xPosition + 50, yPosition - 50, yPosition + 50);
         }
      }).orElse(new ArrayList<>());

      if (!toRemove.isEmpty()) {
         player.getWorldServer().getChannels().stream()
               .map(channel -> channel.getMapFactory().getMap(mapId))
               .forEach(map -> toRemove.forEach(pair -> map.destroyNPC(pair.getLeft())));
      }

      player.yellowMessage("Cleared " + toRemove.size() + " pNPC placements.");
   }
}