package server.maps.spawner;

import client.MapleClient;
import server.life.MaplePlayerNPC;
import server.maps.MapleMapObject;
import tools.PacketCreator;
import tools.packet.character.npc.GetPlayerNPC;
import tools.packet.character.npc.RemovePlayerNPC;
import tools.packet.spawn.RemoveNPCController;
import tools.packet.spawn.SpawnPlayerNPC;

public class PlayerNPCSpawnAndDestroyer implements MapObjectSpawnAndDestroyer<MaplePlayerNPC> {
   private static PlayerNPCSpawnAndDestroyer instance;

   public static PlayerNPCSpawnAndDestroyer getInstance() {
      if (instance == null) {
         instance = new PlayerNPCSpawnAndDestroyer();
      }
      return instance;
   }

   private PlayerNPCSpawnAndDestroyer() {
   }

   @Override
   public void sendSpawnData(MaplePlayerNPC object, MapleClient client) {
      PacketCreator.announce(client, new SpawnPlayerNPC(object));
      PacketCreator.announce(client, new GetPlayerNPC(object));
   }

   @Override
   public void sendDestroyData(MaplePlayerNPC object, MapleClient client) {
      PacketCreator.announce(client, new RemoveNPCController(object.objectId()));
      PacketCreator.announce(client, new RemovePlayerNPC(object.objectId()));
   }

   @Override
   public MaplePlayerNPC as(MapleMapObject object) {
      return (MaplePlayerNPC) object;
   }
}
