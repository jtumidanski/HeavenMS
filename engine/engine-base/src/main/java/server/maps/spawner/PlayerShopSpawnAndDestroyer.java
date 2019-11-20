package server.maps.spawner;

import client.MapleClient;
import server.maps.MapleMapObject;
import server.maps.MaplePlayerShop;
import tools.PacketCreator;
import tools.packet.character.box.RemovePlayerShop;
import tools.packet.character.box.UpdatePlayerShopBox;

public class PlayerShopSpawnAndDestroyer implements MapObjectSpawnAndDestroyer<MaplePlayerShop> {
   private static PlayerShopSpawnAndDestroyer instance;

   public static PlayerShopSpawnAndDestroyer getInstance() {
      if (instance == null) {
         instance = new PlayerShopSpawnAndDestroyer();
      }
      return instance;
   }

   private PlayerShopSpawnAndDestroyer() {
   }

   @Override
   public void sendSpawnData(MaplePlayerShop object, MapleClient client) {
      PacketCreator.announce(client, new UpdatePlayerShopBox(object));
   }

   @Override
   public void sendDestroyData(MaplePlayerShop object, MapleClient client) {
      PacketCreator.announce(client, new RemovePlayerShop(object.getOwner().getId()));
   }

   @Override
   public MaplePlayerShop as(MapleMapObject object) {
      return (MaplePlayerShop) object;
   }
}
