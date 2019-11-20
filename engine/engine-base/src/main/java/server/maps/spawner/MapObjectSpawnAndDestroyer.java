package server.maps.spawner;

import client.MapleClient;
import server.maps.MapleMapObject;

public interface MapObjectSpawnAndDestroyer<T extends MapleMapObject> {
   void sendSpawnData(T object, MapleClient client);

   void sendDestroyData(T object, MapleClient client);

   T as(MapleMapObject object);
}
