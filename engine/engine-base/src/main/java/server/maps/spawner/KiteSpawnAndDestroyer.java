package server.maps.spawner;

import client.MapleClient;
import server.maps.MapleKite;
import server.maps.MapleMapObject;
import tools.PacketCreator;
import tools.packet.remove.RemoveKite;
import tools.packet.spawn.SpawnKite;

public class KiteSpawnAndDestroyer implements MapObjectSpawnAndDestroyer<MapleKite> {
   private static KiteSpawnAndDestroyer instance;

   public static KiteSpawnAndDestroyer getInstance() {
      if (instance == null) {
         instance = new KiteSpawnAndDestroyer();
      }
      return instance;
   }

   private KiteSpawnAndDestroyer() {
   }

   @Override
   public void sendSpawnData(MapleKite object, MapleClient client) {
      client.announce(makeSpawnData(object));
   }

   @Override
   public void sendDestroyData(MapleKite object, MapleClient client) {
      client.announce(makeDestroyData(object));
   }

   @Override
   public MapleKite as(MapleMapObject object) {
      return (MapleKite) object;
   }

   public final byte[] makeSpawnData(MapleKite object) {
      return PacketCreator.create(new SpawnKite(object.objectId(), object.itemId(), object.ownerName(), object.text(), object.pos(), object.ft()));
   }

   public final byte[] makeDestroyData(MapleKite object) {
      return PacketCreator.create(new RemoveKite(object.objectId(), 0));
   }
}
