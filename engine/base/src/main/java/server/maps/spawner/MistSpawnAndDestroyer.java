package server.maps.spawner;

import client.MapleClient;
import client.SkillFactory;
import server.maps.MapleMapObject;
import server.maps.MapleMist;
import tools.PacketCreator;
import tools.packet.remove.RemoveMist;
import tools.packet.spawn.SpawnMist;

public class MistSpawnAndDestroyer implements MapObjectSpawnAndDestroyer<MapleMist> {
   private static MistSpawnAndDestroyer instance;

   public static MistSpawnAndDestroyer getInstance() {
      if (instance == null) {
         instance = new MistSpawnAndDestroyer();
      }
      return instance;
   }

   private MistSpawnAndDestroyer() {
   }

   @Override
   public void sendSpawnData(MapleMist object, MapleClient client) {
      client.announce(makeSpawnData(object));
   }

   @Override
   public void sendDestroyData(MapleMist object, MapleClient client) {
      client.announce(makeDestroyData(object));
   }

   @Override
   public MapleMist as(MapleMapObject object) {
      return (MapleMist) object;
   }

   public byte[] makeSpawnData(MapleMist object) {
      if (object.getOwner() != null) {
         return SkillFactory.applyForSkill(object.getOwner(),
               object.getSource().getSourceId(),
               (skill, skillLevel) -> spawnMistForOwner(object, skillLevel),
               new byte[0]);
      }
      return PacketCreator.create(new SpawnMist(object.getObjectId(), object.getMobOwner().getId(), object.getSkill().skillId(), object.getSkill().level(), object));
   }

   private byte[] spawnMistForOwner(MapleMist object, Integer skillLevel) {
      return object.getSourceSkill().map(skill -> PacketCreator.create(new SpawnMist(object.getObjectId(), object.getOwner().getId(), skill.getId(), skillLevel, object))).orElse(new byte[0]);
   }

   public byte[] makeFakeSpawnData(MapleMist object, int level) {
      if (object.getOwner() != null) {
         return spawnMistForOwner(object, level);
      }
      return PacketCreator.create(new SpawnMist(object.getObjectId(), object.getMobOwner().getId(), object.getSkill().skillId(), object.getSkill().level(), object));
   }

   public byte[] makeDestroyData(MapleMist object) {
      return PacketCreator.create(new RemoveMist(object.getObjectId()));
   }
}
