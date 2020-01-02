package client.command.commands.gm4;

import java.awt.Point;

import client.MapleCharacter;
import client.MapleClient;
import client.command.Command;
import server.maps.MapleMap;

public class HorntailCommand extends Command {
   {
      setDescription("");
   }

   @Override
   public void execute(MapleClient c, String[] params) {
      MapleCharacter player = c.getPlayer();
      final Point targetPoint = player.position();
      final MapleMap targetMap = player.getMap();

      targetMap.spawnHorntailOnGroundBelow(targetPoint);
   }
}
