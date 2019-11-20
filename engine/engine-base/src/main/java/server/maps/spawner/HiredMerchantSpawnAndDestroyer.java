package server.maps.spawner;

import client.MapleClient;
import server.maps.MapleHiredMerchant;
import server.maps.MapleMapObject;
import tools.PacketCreator;
import tools.packet.spawn.SpawnHiredMerchant;

public class HiredMerchantSpawnAndDestroyer implements MapObjectSpawnAndDestroyer<MapleHiredMerchant> {
   private static HiredMerchantSpawnAndDestroyer instance;

   public static HiredMerchantSpawnAndDestroyer getInstance() {
      if (instance == null) {
         instance = new HiredMerchantSpawnAndDestroyer();
      }
      return instance;
   }

   private HiredMerchantSpawnAndDestroyer() {
   }

   @Override
   public void sendSpawnData(MapleHiredMerchant object, MapleClient client) {
      PacketCreator.announce(client, new SpawnHiredMerchant(object));
   }

   @Override
   public void sendDestroyData(MapleHiredMerchant object, MapleClient client) {
   }

   @Override
   public MapleHiredMerchant as(MapleMapObject object) {
      return (MapleHiredMerchant) object;
   }
}
