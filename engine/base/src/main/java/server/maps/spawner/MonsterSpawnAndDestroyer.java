package server.maps.spawner;

import client.MapleClient;
import server.life.MapleMonster;
import server.maps.MapleMapObject;
import tools.PacketCreator;
import tools.packet.monster.KillMonster;
import tools.packet.spawn.SpawnFakeMonster;
import tools.packet.spawn.SpawnMonster;

public class MonsterSpawnAndDestroyer implements MapObjectSpawnAndDestroyer<MapleMonster> {
   private static MonsterSpawnAndDestroyer instance;

   public static MonsterSpawnAndDestroyer getInstance() {
      if (instance == null) {
         instance = new MonsterSpawnAndDestroyer();
      }
      return instance;
   }

   private MonsterSpawnAndDestroyer() {
   }

   @Override
   public void sendSpawnData(MapleMonster object, MapleClient client) {
      if (object.getHp() <= 0) { // mustn't monsterLock this function
         return;
      }
      if (object.isFake()) {
         PacketCreator.announce(client, new SpawnFakeMonster(object, 0));
      } else {
         PacketCreator.announce(client, new SpawnMonster(object, false));
      }

      object.announceMonsterStatus(client);

      if (object.hasBossHPBar()) {
         client.announceBossHpBar(object, object.hashCode(), object.makeBossHPBarPacket());
      }

   }

   @Override
   public void sendDestroyData(MapleMonster object, MapleClient client) {
      PacketCreator.announce(client, new KillMonster(object.getObjectId(), false));
      PacketCreator.announce(client, new KillMonster(object.getObjectId(), true));
   }

   @Override
   public MapleMonster as(MapleMapObject object) {
      return (MapleMonster) object;
   }
}
