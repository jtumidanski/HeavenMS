package server.maps.spawner;

import client.MapleClient;
import server.maps.MapleDragon;
import server.maps.MapleMapObject;
import tools.PacketCreator;
import tools.packet.remove.RemoveDragon;
import tools.packet.spawn.SpawnDragon;

public class DragonSpawnAndDestroyer implements MapObjectSpawnAndDestroyer<MapleDragon> {
   private static DragonSpawnAndDestroyer instance;

   public static DragonSpawnAndDestroyer getInstance() {
      if (instance == null) {
         instance = new DragonSpawnAndDestroyer();
      }
      return instance;
   }

   private DragonSpawnAndDestroyer() {
   }

   @Override
   public void sendSpawnData(MapleDragon object, MapleClient client) {
      PacketCreator.announce(client, new SpawnDragon(object));
   }

   @Override
   public void sendDestroyData(MapleDragon object, MapleClient client) {
      PacketCreator.announce(client, new RemoveDragon(object.ownerId()));
   }

   @Override
   public MapleDragon as(MapleMapObject object) {
      return (MapleDragon) object;
   }
}
