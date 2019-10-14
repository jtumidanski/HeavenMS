package server.maps.spawner;

import client.MapleCharacter;
import client.MapleClient;
import server.maps.MapleMapObject;
import server.maps.MapleReactor;
import tools.PacketCreator;
import tools.packet.reactor.DestroyReactor;
import tools.packet.reactor.SpawnReactor;

public class ReactorSpawnAndDestroyer implements MapObjectSpawnAndDestroyer<MapleReactor> {
   private static ReactorSpawnAndDestroyer instance;

   public static ReactorSpawnAndDestroyer getInstance() {
      if (instance == null) {
         instance = new ReactorSpawnAndDestroyer();
      }
      return instance;
   }

   private ReactorSpawnAndDestroyer() {
   }

   @Override
   public void sendSpawnData(MapleReactor object, MapleClient client) {
      if (object.isAlive()) {
         client.announce(makeSpawnData(object));
      }
   }

   @Override
   public void sendDestroyData(MapleReactor object, MapleClient client) {
      client.announce(makeDestroyData(object));
   }

   @Override
   public MapleReactor as(MapleMapObject object) {
      return (MapleReactor) object;
   }

   public byte[] makeDestroyData(MapleReactor object) {
      return PacketCreator.create(new DestroyReactor(object));
   }

   public byte[] makeSpawnData(MapleReactor object) {
      return PacketCreator.create(new SpawnReactor(object));
   }
}
