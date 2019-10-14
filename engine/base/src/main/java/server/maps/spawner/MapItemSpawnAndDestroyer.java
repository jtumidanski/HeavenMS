package server.maps.spawner;

import client.MapleCharacter;
import client.MapleClient;
import server.maps.MapleMapItem;
import server.maps.MapleMapObject;
import tools.PacketCreator;
import tools.packet.item.drop.DropItemFromMapObject;
import tools.packet.remove.RemoveItem;

public class MapItemSpawnAndDestroyer implements MapObjectSpawnAndDestroyer<MapleMapItem> {
   private static MapItemSpawnAndDestroyer instance;

   public static MapItemSpawnAndDestroyer getInstance() {
      if (instance == null) {
         instance = new MapItemSpawnAndDestroyer();
      }
      return instance;
   }

   private MapItemSpawnAndDestroyer() {
   }

   @Override
   public void sendSpawnData(MapleMapItem object, MapleClient client) {
      MapleCharacter chr = client.getPlayer();

      if (chr.needQuestItem(object.getQuest(), object.getItemId())) {
         object.lockItem();
         try {
            PacketCreator.announce(client, new DropItemFromMapObject(chr, object, null, object.getPosition(), (byte) 2));
         } finally {
            object.unlockItem();
         }
      }
   }

   @Override
   public void sendDestroyData(MapleMapItem object, MapleClient client) {
      PacketCreator.announce(client, new RemoveItem(object.getObjectId(), 1, 0));
   }

   @Override
   public MapleMapItem as(MapleMapObject object) {
      return (MapleMapItem) object;
   }
}
