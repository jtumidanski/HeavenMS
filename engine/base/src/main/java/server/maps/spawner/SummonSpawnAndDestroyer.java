package server.maps.spawner;

import client.MapleClient;
import server.maps.MapleMapObject;
import server.maps.MapleSummon;
import tools.PacketCreator;
import tools.packet.remove.RemoveSummon;
import tools.packet.spawn.SpawnSummon;

public class SummonSpawnAndDestroyer implements MapObjectSpawnAndDestroyer<MapleSummon> {
   private static SummonSpawnAndDestroyer instance;

   public static SummonSpawnAndDestroyer getInstance() {
      if (instance == null) {
         instance = new SummonSpawnAndDestroyer();
      }
      return instance;
   }

   private SummonSpawnAndDestroyer() {
   }

   @Override
   public void sendSpawnData(MapleSummon object, MapleClient client) {
      PacketCreator.announce(client, new SpawnSummon(object.getOwner().getId(), object.getObjectId(),
            object.getSkill(), object.getSkillLevel(), object.getPosition(), object.getStance(),
            object.getMovementType().getValue(), object.isPuppet(), false));
   }

   @Override
   public void sendDestroyData(MapleSummon object, MapleClient client) {
      PacketCreator.announce(client, new RemoveSummon(object.getOwner().getId(), object.getObjectId(), true));
   }

   @Override
   public MapleSummon as(MapleMapObject object) {
      return (MapleSummon) object;
   }
}
