package server.maps.spawner;

import client.MapleClient;
import server.maps.MapleMapObject;
import server.maps.MapleMiniGame;

public class MiniGameSpawnAndDestroyer implements MapObjectSpawnAndDestroyer<MapleMiniGame> {
   private static MiniGameSpawnAndDestroyer instance;

   public static MiniGameSpawnAndDestroyer getInstance() {
      if (instance == null) {
         instance = new MiniGameSpawnAndDestroyer();
      }
      return instance;
   }

   private MiniGameSpawnAndDestroyer() {
   }

   @Override
   public void sendSpawnData(MapleMiniGame object, MapleClient client) {

   }

   @Override
   public void sendDestroyData(MapleMiniGame object, MapleClient client) {

   }

   @Override
   public MapleMiniGame as(MapleMapObject object) {
      return (MapleMiniGame) object;
   }
}
