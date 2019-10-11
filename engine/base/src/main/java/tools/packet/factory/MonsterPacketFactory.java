package tools.packet.factory;

import java.util.Map;

import client.status.MonsterStatus;
import server.life.MobSkill;
import tools.data.output.MaplePacketLittleEndianWriter;
import tools.packet.monster.ApplyMonsterStatus;
import tools.packet.monster.CancelMonsterStatus;
import tools.packet.monster.CatchMonster;
import tools.packet.monster.CatchMonsterFailure;
import tools.packet.monster.CatchMonsterWithItem;
import tools.packet.monster.DamageMonster;
import tools.packet.monster.DamageMonsterFriendly;
import tools.packet.monster.DamageSummon;
import tools.packet.monster.HealMonster;
import tools.packet.monster.KillMonster;
import tools.packet.monster.ShowMonsterHP;

public class MonsterPacketFactory extends AbstractPacketFactory {
   private static MonsterPacketFactory instance;

   public static MonsterPacketFactory getInstance() {
      if (instance == null) {
         instance = new MonsterPacketFactory();
      }
      return instance;
   }

   private MonsterPacketFactory() {
      Handler.handle(KillMonster.class).decorate(this::killMonster).register(registry);
      Handler.handle(ShowMonsterHP.class).decorate(this::showMonsterHP).register(registry);
      Handler.handle(ApplyMonsterStatus.class).decorate(this::applyMonsterStatus).register(registry);
      Handler.handle(CancelMonsterStatus.class).decorate(this::cancelMonsterStatus).register(registry);
      Handler.handle(DamageMonster.class).decorate(this::damageMonster).register(registry);
      Handler.handle(HealMonster.class).decorate(this::healMonster).register(registry);
      Handler.handle(CatchMonster.class).decorate(this::catchMonster).register(registry);
      Handler.handle(CatchMonsterWithItem.class).decorate(this::catchMonsterWithItem).register(registry);
      Handler.handle(DamageMonsterFriendly.class).decorate(this::damageMonsterFriendly).register(registry);
      Handler.handle(CatchMonsterFailure.class).decorate(this::catchMessage).register(registry);
      Handler.handle(DamageSummon.class).decorate(this::damageSummon).register(registry);
   }

   /**
    * Gets a packet telling the client that a monster was killed.
    *
    * @return The kill monster packet.
    */
   protected void killMonster(MaplePacketLittleEndianWriter writer, KillMonster packet) {
      writer.writeInt(packet.objectId());
      writer.write(packet.animation());
      writer.write(packet.animation());
   }

   protected void showMonsterHP(MaplePacketLittleEndianWriter writer, ShowMonsterHP packet) {
      writer.writeInt(packet.objectId());
      writer.write(packet.remainingHpPercentage());
   }

   protected void applyMonsterStatusOther(MaplePacketLittleEndianWriter writer, int oid, Map<MonsterStatus, Integer> stats, int skill, boolean monsterSkill, int delay, MobSkill mobskill) {
      writer.writeInt(oid);
      int mask = 0;
      for (MonsterStatus stat : stats.keySet()) {
         mask |= stat.getValue();
      }
      writer.writeInt(mask);
      for (Integer val : stats.values()) {
         writer.writeShort(val);
         if (monsterSkill) {
            writer.writeShort(mobskill.getSkillId());
            writer.writeShort(mobskill.getSkillLevel());
         } else {
            writer.writeInt(skill);
         }
         writer.writeShort(0); // as this looks similar to giveBuff this
      }
      writer.writeShort(delay); // delay in ms
      writer.write(1); // ?
   }

   protected void applyMonsterStatus(MaplePacketLittleEndianWriter writer, ApplyMonsterStatus packet) {
      Map<MonsterStatus, Integer> stati = packet.getStatusEffect().getStati();
      writer.writeInt(packet.getObjectId());
      writer.writeLong(0);
      writeIntMask(writer, stati);
      for (Map.Entry<MonsterStatus, Integer> stat : stati.entrySet()) {
         writer.writeShort(stat.getValue());
         if (packet.getStatusEffect().isMonsterSkill()) {
            writer.writeShort(packet.getStatusEffect().getMobSkill().getSkillId());
            writer.writeShort(packet.getStatusEffect().getMobSkill().getSkillLevel());
         } else {
            writer.writeInt(packet.getStatusEffect().getSkill().getId());
         }
         writer.writeShort(-1); // might actually be the buffTime but it's not displayed anywhere
      }
      int size = stati.size(); // size
      if (packet.getReflection() != null) {
         for (Integer ref : packet.getReflection()) {
            writer.writeInt(ref);
         }
         if (packet.getReflection().size() > 0) {
            size /= 2; // This gives 2 buffs per reflection but it's really one buff
         }
      }
      writer.write(size); // size
      writer.writeInt(0);
   }

   protected void writeIntMask(final MaplePacketLittleEndianWriter writer, Map<MonsterStatus, Integer> stats) {
      int firstmask = 0;
      int secondmask = 0;
      for (MonsterStatus stat : stats.keySet()) {
         if (stat.isFirst()) {
            firstmask |= stat.getValue();
         } else {
            secondmask |= stat.getValue();
         }
      }
      writer.writeInt(firstmask);
      writer.writeInt(secondmask);
   }

   protected void cancelMonsterStatus(MaplePacketLittleEndianWriter writer, CancelMonsterStatus packet) {
      writer.writeInt(packet.objectId());
      writer.writeLong(0);
      writeIntMask(writer, packet.stats());
      writer.writeInt(0);
   }

   protected void damageMonster(MaplePacketLittleEndianWriter writer, DamageMonster packet) {
      damageMonsterIntern(writer, packet.objectId(), packet.damage(), 0, 0);
   }

   protected void healMonster(MaplePacketLittleEndianWriter writer, HealMonster packet) {
      damageMonsterIntern(writer, packet.objectId(), -packet.heal(), packet.currentHp(), packet.maximumHp());
   }

   protected void damageMonsterIntern(MaplePacketLittleEndianWriter writer, int oid, int damage, int curhp, int maxhp) {
      writer.writeInt(oid);
      writer.write(0);
      writer.writeInt(damage);
      writer.writeInt(curhp);
      writer.writeInt(maxhp);
   }

   protected void catchMonster(MaplePacketLittleEndianWriter writer, CatchMonster packet) {
      // updated packet structure found thanks to Rien dev team
      writer.writeInt(packet.objectId());
      writer.write(packet.success());
   }

   protected void catchMonsterWithItem(MaplePacketLittleEndianWriter writer, CatchMonsterWithItem packet) {
      writer.writeInt(packet.objectId());
      writer.writeInt(packet.itemId());
      writer.write(packet.success());
   }

   protected void damageMonsterFriendly(MaplePacketLittleEndianWriter writer, DamageMonsterFriendly packet) {
      writer.writeInt(packet.objectId());
      writer.write(1); // direction ?
      writer.writeInt(packet.damage());
      writer.writeInt(packet.remainingHp());
      writer.writeInt(packet.maximumHp());
   }

   protected void catchMessage(MaplePacketLittleEndianWriter writer, CatchMonsterFailure packet) { // not done, I guess
      writer.write(packet.message()); // 1 = too strong, 2 = Elemental Rock
      writer.writeInt(0);//Maybe itemid?
      writer.writeInt(0);
   }

   protected void damageSummon(MaplePacketLittleEndianWriter writer, DamageSummon packet) {
      writer.writeInt(packet.characterId());
      writer.writeInt(packet.objectId());
      writer.write(12);
      writer.writeInt(packet.damage());         // damage display doesn't seem to work...
      writer.writeInt(packet.monsterIdFrom());
      writer.write(0);
   }
}