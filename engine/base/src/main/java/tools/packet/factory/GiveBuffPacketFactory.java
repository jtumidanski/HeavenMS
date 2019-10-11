package tools.packet.factory;

import client.MapleBuffStat;
import client.MapleDisease;
import constants.skills.Buccaneer;
import constants.skills.Corsair;
import constants.skills.ThunderBreaker;
import tools.Pair;
import tools.data.output.MaplePacketLittleEndianWriter;
import tools.packet.buff.GiveBuff;
import tools.packet.buff.GiveDebuff;
import tools.packet.buff.GiveFinalAttack;
import tools.packet.buff.GiveForeignBuff;
import tools.packet.buff.GiveForeignChairSkillEffect;
import tools.packet.buff.GiveForeignDebuff;
import tools.packet.buff.GiveForeignPirateBuff;
import tools.packet.buff.GiveForeignSlowDebuff;
import tools.packet.buff.GivePirateBuff;
import tools.packet.buff.ShowMonsterRiding;

public class GiveBuffPacketFactory extends AbstractBuffPacketFactory {
   private static GiveBuffPacketFactory instance;

   public static GiveBuffPacketFactory getInstance() {
      if (instance == null) {
         instance = new GiveBuffPacketFactory();
      }
      return instance;
   }

   private GiveBuffPacketFactory() {
      Handler.handle(GiveBuff.class).decorate(this::giveBuff).register(registry);
      Handler.handle(GiveDebuff.class).decorate(this::giveDebuff).register(registry);
      Handler.handle(GiveForeignDebuff.class).decorate(this::giveForeignDebuff).register(registry);
      Handler.handle(GiveForeignBuff.class).decorate(this::giveForeignBuff).register(registry);
      Handler.handle(GiveForeignSlowDebuff.class).decorate(this::giveForeignSlowDebuff).register(registry);
      Handler.handle(GiveForeignChairSkillEffect.class).decorate(this::giveForeignChairSkillEffect).register(registry);
      Handler.handle(GivePirateBuff.class).decorate(this::givePirateBuff).register(registry);
      Handler.handle(GiveFinalAttack.class).decorate(this::giveFinalAttack).register(registry);
      Handler.handle(ShowMonsterRiding.class).decorate(this::showMonsterRiding).register(registry);
      Handler.handle(GiveForeignPirateBuff.class).decorate(this::giveForeignPirateBuff).register(registry);
   }

   /**
    * It is important that statups is in the correct order (see declaration
    * order in MapleBuffStat) since this method doesn't do automagical
    * reordering.
    */
   //1F 00 00 00 00 00 03 00 00 40 00 00 00 E0 00 00 00 00 00 00 00 00 E0 01 8E AA 4F 00 00 C2 EB 0B E0 01 8E AA 4F 00 00 C2 EB 0B 0C 00 8E AA 4F 00 00 C2 EB 0B 44 02 8E AA 4F 00 00 C2 EB 0B 44 02 8E AA 4F 00 00 C2 EB 0B 00 00 E0 7A 1D 00 8E AA 4F 00 00 00 00 00 00 00 00 03
   protected void giveBuff(MaplePacketLittleEndianWriter writer, GiveBuff packet) {
      boolean special = false;
      writeLongMask(writer, packet.statups());
      for (Pair<MapleBuffStat, Integer> statup : packet.statups()) {
         if (statup.getLeft().equals(MapleBuffStat.MONSTER_RIDING) || statup.getLeft().equals(MapleBuffStat.HOMING_BEACON)) {
            special = true;
         }
         writer.writeShort(statup.getRight().shortValue());
         writer.writeInt(packet.buffId());
         writer.writeInt(packet.buffLength());
      }
      writer.writeInt(0);
      writer.write(0);
      writer.writeInt(packet.statups().get(0).getRight()); //Homing beacon ...

      if (special) {
         writer.skip(3);
      }
   }

   protected void giveDebuff(MaplePacketLittleEndianWriter writer, GiveDebuff packet) {
      writeLongMaskD(writer, packet.getStatups());
      for (Pair<MapleDisease, Integer> statup : packet.getStatups()) {
         writer.writeShort(statup.getRight().shortValue());
         writer.writeShort(packet.getMobSkill().getSkillId());
         writer.writeShort(packet.getMobSkill().getSkillLevel());
         writer.writeInt((int) packet.getMobSkill().getDuration());
      }
      writer.writeShort(0); // ??? wk charges have 600 here o.o
      writer.writeShort(900);//Delay
      writer.write(1);
   }

   protected void giveForeignDebuff(MaplePacketLittleEndianWriter writer, GiveForeignDebuff packet) {
      // Poison damage visibility and missing diseases status visibility, extended through map transitions thanks to Ronan
      writer.writeInt(packet.getCharacterId());
      writeLongMaskD(writer, packet.getStatups());
      for (Pair<MapleDisease, Integer> statup : packet.getStatups()) {
         if (statup.getLeft() == MapleDisease.POISON) {
            writer.writeShort(statup.getRight().shortValue());
         }
         writer.writeShort(packet.getMobSkill().getSkillId());
         writer.writeShort(packet.getMobSkill().getSkillLevel());
      }
      writer.writeShort(0); // same as give_buff
      writer.writeShort(900);//Delay
   }

   protected void giveForeignBuff(MaplePacketLittleEndianWriter writer, GiveForeignBuff packet) {
      writer.writeInt(packet.characterId());
      writeLongMask(writer, packet.statups());
      for (Pair<MapleBuffStat, Integer> statup : packet.statups()) {
         writer.writeShort(statup.getRight().shortValue());
      }
      writer.writeInt(0);
      writer.writeShort(0);
   }

   protected void giveForeignSlowDebuff(MaplePacketLittleEndianWriter writer, GiveForeignSlowDebuff packet) {
      writer.writeInt(packet.getCharacterId());
      writeLongMaskSlowD(writer);
      for (Pair<MapleDisease, Integer> statup : packet.getStatups()) {
         if (statup.getLeft() == MapleDisease.POISON) {
            writer.writeShort(statup.getRight().shortValue());
         }
         writer.writeShort(packet.getMobSkill().getSkillId());
         writer.writeShort(packet.getMobSkill().getSkillLevel());
      }
      writer.writeShort(0); // same as give_buff
      writer.writeShort(900);//Delay
   }

   protected void giveForeignChairSkillEffect(MaplePacketLittleEndianWriter writer, GiveForeignChairSkillEffect packet) {
      writer.writeInt(packet.characterId());
      writeLongMaskChair(writer);
      writer.writeShort(0);
      writer.writeShort(0);
      writer.writeShort(100);
      writer.writeShort(1);
      writer.writeShort(0);
      writer.writeShort(900);
      writer.skip(7);
   }

   protected void givePirateBuff(MaplePacketLittleEndianWriter writer, GivePirateBuff packet) {
      boolean infusion = packet.buffId() == Buccaneer.SPEED_INFUSION || packet.buffId() == ThunderBreaker.SPEED_INFUSION || packet.buffId() == Corsair.SPEED_INFUSION;
      writeLongMask(writer, packet.statups());
      writer.writeShort(0);
      for (Pair<MapleBuffStat, Integer> stat : packet.statups()) {
         writer.writeInt(stat.getRight().shortValue());
         writer.writeInt(packet.buffId());
         writer.skip(infusion ? 10 : 5);
         writer.writeShort(packet.duration());
      }
      writer.skip(3);
   }

   protected void giveFinalAttack(MaplePacketLittleEndianWriter writer, GiveFinalAttack packet) { // packets found thanks to lailainoob
      writer.writeLong(0);
      writer.writeShort(0);
      writer.write(0);//some 80 and 0 bs DIRECTION
      writer.write(0x80);//let's just do 80, then 0
      writer.writeInt(0);
      writer.writeShort(1);
      writer.writeInt(packet.skillId());
      writer.writeInt(packet.time());
      writer.writeInt(0);
   }

   protected void showMonsterRiding(MaplePacketLittleEndianWriter writer, ShowMonsterRiding packet) { //Gtfo with this, this is just giveForeignBuff
      writer.writeInt(packet.characterId());
      writer.writeLong(MapleBuffStat.MONSTER_RIDING.getValue());
      writer.writeLong(0);
      writer.writeShort(0);
      writer.writeInt(packet.mountId());
      writer.writeInt(packet.skillId());
      writer.writeInt(0); //Server Tick value.
      writer.writeShort(0);
      writer.write(0); //Times you have been buffed
   }

   protected void giveForeignPirateBuff(MaplePacketLittleEndianWriter writer, GiveForeignPirateBuff packet) {
      boolean infusion = packet.buffId() == Buccaneer.SPEED_INFUSION || packet.buffId() == ThunderBreaker.SPEED_INFUSION || packet.buffId() == Corsair.SPEED_INFUSION;
      writer.writeInt(packet.characterId());
      writeLongMask(writer, packet.statups());
      writer.writeShort(0);
      for (Pair<MapleBuffStat, Integer> statup : packet.statups()) {
         writer.writeInt(statup.getRight().shortValue());
         writer.writeInt(packet.buffId());
         writer.skip(infusion ? 10 : 5);
         writer.writeShort(packet.time());
      }
      writer.writeShort(0);
      writer.write(2);
   }
}