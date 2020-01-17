package client.command.commands.gm2;

import java.util.Collections;
import java.util.List;

import client.MapleClient;
import client.command.Command;
import server.maps.MapleMapItem;
import server.maps.MapleMapObject;
import server.maps.MapleMapObjectType;

public class LootCommand extends Command {

   {
      setDescription("Loots all items that belong to you.");
   }

   @Override
   public void execute(MapleClient c, String[] params) {
      List<MapleMapObject> items = c.getPlayer().getMap().getMapObjectsInRange(c.getPlayer().position(), Double.POSITIVE_INFINITY, Collections.singletonList(MapleMapObjectType.ITEM));
      for (MapleMapObject item : items) {
         MapleMapItem mapItem = (MapleMapItem) item;
         if (mapItem.getOwnerId() == c.getPlayer().getId() || mapItem.getOwnerId() == c.getPlayer().getPartyId()) {
            c.getPlayer().pickupItem(mapItem);
         }
      }
   }
}
