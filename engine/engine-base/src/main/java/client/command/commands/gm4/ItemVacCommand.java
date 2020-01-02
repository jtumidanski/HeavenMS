package client.command.commands.gm4;

import java.util.Collections;
import java.util.List;

import client.MapleCharacter;
import client.MapleClient;
import client.command.Command;
import server.maps.MapleMapObject;
import server.maps.MapleMapObjectType;

public class ItemVacCommand extends Command {
   {
      setDescription("");
   }

   @Override
   public void execute(MapleClient c, String[] params) {
      MapleCharacter player = c.getPlayer();
      List<MapleMapObject> list = player.getMap().getMapObjectsInRange(player.position(), Double.POSITIVE_INFINITY, Collections.singletonList(MapleMapObjectType.ITEM));
      for (MapleMapObject item : list) {
         player.pickupItem(item);
      }
   }
}
