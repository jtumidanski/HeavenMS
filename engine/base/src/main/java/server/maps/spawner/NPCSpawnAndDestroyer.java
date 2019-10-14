package server.maps.spawner;

import client.MapleClient;
import server.life.MapleNPC;
import server.maps.MapleMapObject;
import tools.PacketCreator;
import tools.packet.remove.RemoveNPC;
import tools.packet.spawn.RemoveNPCController;
import tools.packet.spawn.SpawnNPC;
import tools.packet.spawn.SpawnNPCRequestController;

public class NPCSpawnAndDestroyer implements MapObjectSpawnAndDestroyer<MapleNPC> {
   private static NPCSpawnAndDestroyer instance;

   public static NPCSpawnAndDestroyer getInstance() {
      if (instance == null) {
         instance = new NPCSpawnAndDestroyer();
      }
      return instance;
   }

   private NPCSpawnAndDestroyer() {
   }

   @Override
   public void sendSpawnData(MapleNPC object, MapleClient client) {
      PacketCreator.announce(client, new SpawnNPC(object));
      PacketCreator.announce(client, new SpawnNPCRequestController(object, true));
   }

   @Override
   public void sendDestroyData(MapleNPC object, MapleClient client) {
      PacketCreator.announce(client, new RemoveNPCController(object.objectId()));
      PacketCreator.announce(client, new RemoveNPC(object.objectId()));
   }

   @Override
   public MapleNPC as(MapleMapObject object) {
      return (MapleNPC) object;
   }
}
