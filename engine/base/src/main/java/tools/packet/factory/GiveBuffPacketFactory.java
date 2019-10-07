package tools.packet.factory;

import client.MapleBuffStat;
import client.MapleDisease;
import constants.skills.Buccaneer;
import constants.skills.Corsair;
import constants.skills.ThunderBreaker;
import net.opcodes.SendOpcode;
import tools.FilePrinter;
import tools.Pair;
import tools.data.output.MaplePacketLittleEndianWriter;
import tools.packet.PacketInput;
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
   }

   @Override
   public byte[] create(PacketInput packetInput) {
      if (packetInput instanceof GiveBuff) {
         return create(this::giveBuff, packetInput);
      } else if (packetInput instanceof GiveDebuff) {
         return create(this::giveDebuff, packetInput);
      } else if (packetInput instanceof GiveForeignDebuff) {
         return create(this::giveForeignDebuff, packetInput);
      } else if (packetInput instanceof GiveForeignBuff) {
         return create(this::giveForeignBuff, packetInput);
      } else if (packetInput instanceof GiveForeignSlowDebuff) {
         return create(this::giveForeignSlowDebuff, packetInput);
      } else if (packetInput instanceof GiveForeignChairSkillEffect) {
         return create(this::giveForeignChairSkillEffect, packetInput);
      } else if (packetInput instanceof GivePirateBuff) {
         return create(this::givePirateBuff, packetInput);
      } else if (packetInput instanceof GiveFinalAttack) {
         return create(this::giveFinalAttack, packetInput);
      } else if (packetInput instanceof ShowMonsterRiding) {
         return create(this::showMonsterRiding, packetInput);
      } else if (packetInput instanceof GiveForeignPirateBuff) {
         return create(this::giveForeignPirateBuff, packetInput);
      }
      FilePrinter.printError(FilePrinter.PACKET_LOGS + "generic.txt", "Trying to handle invalid input " + packetInput.toString());
      return new byte[0];
   }

   /**
    * It is important that statups is in the correct order (see declaration
    * order in MapleBuffStat) since this method doesn't do automagical
    * reordering.
    */
   //1F 00 00 00 00 00 03 00 00 40 00 00 00 E0 00 00 00 00 00 00 00 00 E0 01 8E AA 4F 00 00 C2 EB 0B E0 01 8E AA 4F 00 00 C2 EB 0B 0C 00 8E AA 4F 00 00 C2 EB 0B 44 02 8E AA 4F 00 00 C2 EB 0B 44 02 8E AA 4F 00 00 C2 EB 0B 00 00 E0 7A 1D 00 8E AA 4F 00 00 00 00 00 00 00 00 03
   protected byte[] giveBuff(GiveBuff packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.GIVE_BUFF.getValue());
      boolean special = false;
      writeLongMask(mplew, packet.statups());
      for (Pair<MapleBuffStat, Integer> statup : packet.statups()) {
         if (statup.getLeft().equals(MapleBuffStat.MONSTER_RIDING) || statup.getLeft().equals(MapleBuffStat.HOMING_BEACON)) {
            special = true;
         }
         mplew.writeShort(statup.getRight().shortValue());
         mplew.writeInt(packet.buffId());
         mplew.writeInt(packet.buffLength());
      }
      mplew.writeInt(0);
      mplew.write(0);
      mplew.writeInt(packet.statups().get(0).getRight()); //Homing beacon ...

      if (special) {
         mplew.skip(3);
      }
      return mplew.getPacket();
   }

   protected byte[] giveDebuff(GiveDebuff packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.GIVE_BUFF.getValue());
      writeLongMaskD(mplew, packet.getStatups());
      for (Pair<MapleDisease, Integer> statup : packet.getStatups()) {
         mplew.writeShort(statup.getRight().shortValue());
         mplew.writeShort(packet.getMobSkill().getSkillId());
         mplew.writeShort(packet.getMobSkill().getSkillLevel());
         mplew.writeInt((int) packet.getMobSkill().getDuration());
      }
      mplew.writeShort(0); // ??? wk charges have 600 here o.o
      mplew.writeShort(900);//Delay
      mplew.write(1);
      return mplew.getPacket();
   }

   protected byte[] giveForeignDebuff(GiveForeignDebuff packet) {
      // Poison damage visibility and missing diseases status visibility, extended through map transitions thanks to Ronan

      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.GIVE_FOREIGN_BUFF.getValue());
      mplew.writeInt(packet.getCharacterId());
      writeLongMaskD(mplew, packet.getStatups());
      for (Pair<MapleDisease, Integer> statup : packet.getStatups()) {
         if (statup.getLeft() == MapleDisease.POISON) {
            mplew.writeShort(statup.getRight().shortValue());
         }
         mplew.writeShort(packet.getMobSkill().getSkillId());
         mplew.writeShort(packet.getMobSkill().getSkillLevel());
      }
      mplew.writeShort(0); // same as give_buff
      mplew.writeShort(900);//Delay
      return mplew.getPacket();
   }

   protected byte[] giveForeignBuff(GiveForeignBuff packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.GIVE_FOREIGN_BUFF.getValue());
      mplew.writeInt(packet.characterId());
      writeLongMask(mplew, packet.statups());
      for (Pair<MapleBuffStat, Integer> statup : packet.statups()) {
         mplew.writeShort(statup.getRight().shortValue());
      }
      mplew.writeInt(0);
      mplew.writeShort(0);
      return mplew.getPacket();
   }

   protected byte[] giveForeignSlowDebuff(GiveForeignSlowDebuff packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.GIVE_FOREIGN_BUFF.getValue());
      mplew.writeInt(packet.getCharacterId());
      writeLongMaskSlowD(mplew);
      for (Pair<MapleDisease, Integer> statup : packet.getStatups()) {
         if (statup.getLeft() == MapleDisease.POISON) {
            mplew.writeShort(statup.getRight().shortValue());
         }
         mplew.writeShort(packet.getMobSkill().getSkillId());
         mplew.writeShort(packet.getMobSkill().getSkillLevel());
      }
      mplew.writeShort(0); // same as give_buff
      mplew.writeShort(900);//Delay
      return mplew.getPacket();
   }

   protected byte[] giveForeignChairSkillEffect(GiveForeignChairSkillEffect packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.GIVE_FOREIGN_BUFF.getValue());
      mplew.writeInt(packet.characterId());
      writeLongMaskChair(mplew);

      mplew.writeShort(0);
      mplew.writeShort(0);
      mplew.writeShort(100);
      mplew.writeShort(1);

      mplew.writeShort(0);
      mplew.writeShort(900);

      mplew.skip(7);

      return mplew.getPacket();
   }

   protected byte[] givePirateBuff(GivePirateBuff packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      boolean infusion = packet.buffId() == Buccaneer.SPEED_INFUSION || packet.buffId() == ThunderBreaker.SPEED_INFUSION || packet.buffId() == Corsair.SPEED_INFUSION;
      mplew.writeShort(SendOpcode.GIVE_BUFF.getValue());
      writeLongMask(mplew, packet.statups());
      mplew.writeShort(0);
      for (Pair<MapleBuffStat, Integer> stat : packet.statups()) {
         mplew.writeInt(stat.getRight().shortValue());
         mplew.writeInt(packet.buffId());
         mplew.skip(infusion ? 10 : 5);
         mplew.writeShort(packet.duration());
      }
      mplew.skip(3);
      return mplew.getPacket();
   }

   protected byte[] giveFinalAttack(GiveFinalAttack packet) { // packets found thanks to lailainoob
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.GIVE_BUFF.getValue());
      mplew.writeLong(0);
      mplew.writeShort(0);
      mplew.write(0);//some 80 and 0 bs DIRECTION
      mplew.write(0x80);//let's just do 80, then 0
      mplew.writeInt(0);
      mplew.writeShort(1);
      mplew.writeInt(packet.skillId());
      mplew.writeInt(packet.time());
      mplew.writeInt(0);
      return mplew.getPacket();
   }

   protected byte[] showMonsterRiding(ShowMonsterRiding packet) { //Gtfo with this, this is just giveForeignBuff
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.GIVE_FOREIGN_BUFF.getValue());
      mplew.writeInt(packet.characterId());
      mplew.writeLong(MapleBuffStat.MONSTER_RIDING.getValue());
      mplew.writeLong(0);
      mplew.writeShort(0);
      mplew.writeInt(packet.mountId());
      mplew.writeInt(packet.skillId());
      mplew.writeInt(0); //Server Tick value.
      mplew.writeShort(0);
      mplew.write(0); //Times you have been buffed
      return mplew.getPacket();
   }

   protected byte[] giveForeignPirateBuff(GiveForeignPirateBuff packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      boolean infusion = packet.buffId() == Buccaneer.SPEED_INFUSION || packet.buffId() == ThunderBreaker.SPEED_INFUSION || packet.buffId() == Corsair.SPEED_INFUSION;
      mplew.writeShort(SendOpcode.GIVE_FOREIGN_BUFF.getValue());
      mplew.writeInt(packet.characterId());
      writeLongMask(mplew, packet.statups());
      mplew.writeShort(0);
      for (Pair<MapleBuffStat, Integer> statup : packet.statups()) {
         mplew.writeInt(statup.getRight().shortValue());
         mplew.writeInt(packet.buffId());
         mplew.skip(infusion ? 10 : 5);
         mplew.writeShort(packet.time());
      }
      mplew.writeShort(0);
      mplew.write(2);
      return mplew.getPacket();
   }
}