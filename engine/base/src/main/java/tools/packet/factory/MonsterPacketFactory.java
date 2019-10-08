package tools.packet.factory;

import java.util.Map;

import client.status.MonsterStatus;
import net.opcodes.SendOpcode;
import server.life.MobSkill;
import tools.FilePrinter;
import tools.data.output.MaplePacketLittleEndianWriter;
import tools.packet.PacketInput;
import tools.packet.monster.ApplyMonsterStatus;
import tools.packet.monster.CancelMonsterStatus;
import tools.packet.monster.CatchMonster;
import tools.packet.monster.CatchMonsterFailure;
import tools.packet.monster.CatchMonsterWithItem;
import tools.packet.monster.DamageMonster;
import tools.packet.monster.DamageMonsterFriendly;
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
   }

   @Override
   public byte[] create(PacketInput packetInput) {
      if (packetInput instanceof KillMonster) {
         return create(this::killMonster, packetInput);
      } else if (packetInput instanceof ShowMonsterHP) {
         return create(this::showMonsterHP, packetInput);
      } else if (packetInput instanceof ApplyMonsterStatus) {
         return create(this::applyMonsterStatus, packetInput);
      } else if (packetInput instanceof CancelMonsterStatus) {
         return create(this::cancelMonsterStatus, packetInput);
      } else if (packetInput instanceof DamageMonster) {
         return create(this::damageMonster, packetInput);
      } else if (packetInput instanceof HealMonster) {
         return create(this::healMonster, packetInput);
      } else if (packetInput instanceof CatchMonster) {
         return create(this::catchMonster, packetInput);
      } else if (packetInput instanceof CatchMonsterWithItem) {
         return create(this::catchMonsterWithItem, packetInput);
      } else if (packetInput instanceof DamageMonsterFriendly) {
         return create(this::damageMonsterFriendly, packetInput);
      } else if (packetInput instanceof CatchMonsterFailure) {
         return create(this::catchMessage, packetInput);
      }
      FilePrinter.printError(FilePrinter.PACKET_LOGS + "generic.txt", "Trying to handle invalid input " + packetInput.toString());
      return new byte[0];
   }

   /**
    * Gets a packet telling the client that a monster was killed.
    * @return The kill monster packet.
    */
   protected byte[] killMonster(KillMonster packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.KILL_MONSTER.getValue());
      mplew.writeInt(packet.objectId());
      mplew.write(packet.animation());
      mplew.write(packet.animation());
      return mplew.getPacket();
   }

   protected byte[] showMonsterHP(ShowMonsterHP packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.SHOW_MONSTER_HP.getValue());
      mplew.writeInt(packet.objectId());
      mplew.write(packet.remainingHpPercentage());
      return mplew.getPacket();
   }

   protected byte[] applyMonsterStatusOther(int oid, Map<MonsterStatus, Integer> stats, int skill, boolean monsterSkill, int delay, MobSkill mobskill) {
      MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.APPLY_MONSTER_STATUS.getValue());
      mplew.writeInt(oid);
      int mask = 0;
      for (MonsterStatus stat : stats.keySet()) {
         mask |= stat.getValue();
      }
      mplew.writeInt(mask);
      for (Integer val : stats.values()) {
         mplew.writeShort(val);
         if (monsterSkill) {
            mplew.writeShort(mobskill.getSkillId());
            mplew.writeShort(mobskill.getSkillLevel());
         } else {
            mplew.writeInt(skill);
         }
         mplew.writeShort(0); // as this looks similar to giveBuff this
      }
      mplew.writeShort(delay); // delay in ms
      mplew.write(1); // ?
      return mplew.getPacket();
   }

   protected byte[] applyMonsterStatus(ApplyMonsterStatus packet) {
      Map<MonsterStatus, Integer> stati = packet.getStatusEffect().getStati();
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.APPLY_MONSTER_STATUS.getValue());
      mplew.writeInt(packet.getObjectId());
      mplew.writeLong(0);
      writeIntMask(mplew, stati);
      for (Map.Entry<MonsterStatus, Integer> stat : stati.entrySet()) {
         mplew.writeShort(stat.getValue());
         if (packet.getStatusEffect().isMonsterSkill()) {
            mplew.writeShort(packet.getStatusEffect().getMobSkill().getSkillId());
            mplew.writeShort(packet.getStatusEffect().getMobSkill().getSkillLevel());
         } else {
            mplew.writeInt(packet.getStatusEffect().getSkill().getId());
         }
         mplew.writeShort(-1); // might actually be the buffTime but it's not displayed anywhere
      }
      int size = stati.size(); // size
      if (packet.getReflection() != null) {
         for (Integer ref : packet.getReflection()) {
            mplew.writeInt(ref);
         }
         if (packet.getReflection().size() > 0) {
            size /= 2; // This gives 2 buffs per reflection but it's really one buff
         }
      }
      mplew.write(size); // size
      mplew.writeInt(0);
      return mplew.getPacket();
   }

   protected void writeIntMask(final MaplePacketLittleEndianWriter mplew, Map<MonsterStatus, Integer> stats) {
      int firstmask = 0;
      int secondmask = 0;
      for (MonsterStatus stat : stats.keySet()) {
         if (stat.isFirst()) {
            firstmask |= stat.getValue();
         } else {
            secondmask |= stat.getValue();
         }
      }
      mplew.writeInt(firstmask);
      mplew.writeInt(secondmask);
   }

   protected byte[] cancelMonsterStatus(CancelMonsterStatus packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.CANCEL_MONSTER_STATUS.getValue());
      mplew.writeInt(packet.objectId());
      mplew.writeLong(0);
      writeIntMask(mplew, packet.stats());
      mplew.writeInt(0);
      return mplew.getPacket();
   }

   protected byte[] damageMonster(DamageMonster packet) {
      return damageMonsterIntern(packet.objectId(), packet.damage(), 0, 0);
   }

   protected byte[] healMonster(HealMonster packet) {
      return damageMonsterIntern(packet.objectId(), -packet.heal(), packet.currentHp(), packet.maximumHp());
   }

   protected byte[] damageMonsterIntern(int oid, int damage, int curhp, int maxhp) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.DAMAGE_MONSTER.getValue());
      mplew.writeInt(oid);
      mplew.write(0);
      mplew.writeInt(damage);
      mplew.writeInt(curhp);
      mplew.writeInt(maxhp);
      return mplew.getPacket();
   }

   protected byte[] catchMonster(CatchMonster packet) {   // updated packet structure found thanks to Rien dev team
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.CATCH_MONSTER.getValue());
      mplew.writeInt(packet.objectId());
      mplew.write(packet.success());
      return mplew.getPacket();
   }

   protected byte[] catchMonsterWithItem(CatchMonsterWithItem packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.CATCH_MONSTER_WITH_ITEM.getValue());
      mplew.writeInt(packet.objectId());
      mplew.writeInt(packet.itemId());
      mplew.write(packet.success());
      return mplew.getPacket();
   }

   protected byte[] damageMonsterFriendly(DamageMonsterFriendly packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.DAMAGE_MONSTER.getValue());
      mplew.writeInt(packet.objectId());
      mplew.write(1); // direction ?
      mplew.writeInt(packet.damage());
      mplew.writeInt(packet.remainingHp());
      mplew.writeInt(packet.maximumHp());
      return mplew.getPacket();
   }

   protected byte[] catchMessage(CatchMonsterFailure packet) { // not done, I guess
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.BRIDLE_MOB_CATCH_FAIL.getValue());
      mplew.write(packet.message()); // 1 = too strong, 2 = Elemental Rock
      mplew.writeInt(0);//Maybe itemid?
      mplew.writeInt(0);
      return mplew.getPacket();
   }
}