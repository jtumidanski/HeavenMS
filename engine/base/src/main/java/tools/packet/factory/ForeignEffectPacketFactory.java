package tools.packet.factory;

import client.MapleCharacter;
import client.inventory.ScrollResult;
import net.opcodes.SendOpcode;
import tools.FilePrinter;
import tools.data.output.MaplePacketLittleEndianWriter;
import tools.packet.PacketInput;
import tools.packet.foreigneffect.ShowBerserk;
import tools.packet.foreigneffect.ShowBuffEffect;
import tools.packet.foreigneffect.ShowBuffEffectWithLevel;
import tools.packet.foreigneffect.ShowForeignBuybackEffect;
import tools.packet.foreigneffect.ShowForeignCardEffect;
import tools.packet.foreigneffect.ShowForeignEffect;
import tools.packet.foreigneffect.ShowForeignInfo;
import tools.packet.foreigneffect.ShowForeignMakerEffect;
import tools.packet.foreigneffect.ShowPetLevelUp;
import tools.packet.foreigneffect.ShowRecovery;
import tools.packet.foreigneffect.ShowScrollEffect;
import tools.packet.foreigneffect.ShowSkillBookResult;

public class ForeignEffectPacketFactory extends AbstractPacketFactory {
   private static ForeignEffectPacketFactory instance;

   public static ForeignEffectPacketFactory getInstance() {
      if (instance == null) {
         instance = new ForeignEffectPacketFactory();
      }
      return instance;
   }

   private ForeignEffectPacketFactory() {
   }

   @Override
   public byte[] create(PacketInput packetInput) {
      if (packetInput instanceof ShowBuffEffect) {
         return create(this::showBuffEffect, packetInput);
      } else if (packetInput instanceof ShowBuffEffectWithLevel) {
         return create(this::showBuffEffectWithLevel, packetInput);
      } else if (packetInput instanceof ShowBerserk) {
         return create(this::showBerserk, packetInput);
      } else if (packetInput instanceof ShowPetLevelUp) {
         return create(this::showPetLevelUp, packetInput);
      } else if (packetInput instanceof ShowForeignCardEffect) {
         return create(this::showForeignCardEffect, packetInput);
      } else if (packetInput instanceof ShowForeignInfo) {
         return create(this::showForeignInfo, packetInput);
      } else if (packetInput instanceof ShowForeignBuybackEffect) {
         return create(this::showForeignBuybackEffect, packetInput);
      } else if (packetInput instanceof ShowForeignMakerEffect) {
         return create(this::showForeignMakerEffect, packetInput);
      } else if (packetInput instanceof ShowForeignEffect) {
         return create(this::showForeignEffect, packetInput);
      } else if (packetInput instanceof ShowRecovery) {
         return create(this::showRecovery, packetInput);
      } else if (packetInput instanceof ShowScrollEffect) {
         return create(this::getScrollEffect, packetInput);
      } else if (packetInput instanceof ShowSkillBookResult) {
         return create(this::skillBookResult, packetInput);
      }
      FilePrinter.printError(FilePrinter.PACKET_LOGS + "generic.txt", "Trying to handle invalid input " + packetInput.toString());
      return new byte[0];
   }

   protected byte[] showBuffEffect(ShowBuffEffect packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.SHOW_FOREIGN_EFFECT.getValue());
      mplew.writeInt(packet.characterId());
      mplew.write(packet.effectId()); //buff level
      mplew.writeInt(packet.skillId());
      mplew.write(packet.direction());
      mplew.write(1);
      mplew.writeLong(0);
      return mplew.getPacket();
   }

   protected byte[] showBuffEffectWithLevel(ShowBuffEffectWithLevel packet) {   // updated packet structure found thanks to Rien dev team
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.SHOW_FOREIGN_EFFECT.getValue());
      mplew.writeInt(packet.characterId());
      mplew.write(packet.effectId());
      mplew.writeInt(packet.skillId());
      mplew.write(0);
      mplew.write(packet.skillLevel());
      mplew.write(packet.direction());
      return mplew.getPacket();
   }

   protected byte[] showBerserk(ShowBerserk packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.SHOW_FOREIGN_EFFECT.getValue());
      mplew.writeInt(packet.characterId());
      mplew.write(1);
      mplew.writeInt(1320006);
      mplew.write(0xA9);
      mplew.write(packet.skillLevel());
      mplew.write(packet.berserk() ? 1 : 0);
      return mplew.getPacket();
   }

   protected byte[] showPetLevelUp(ShowPetLevelUp packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.SHOW_FOREIGN_EFFECT.getValue());
      mplew.writeInt(packet.characterId());
      mplew.write(4);
      mplew.write(0);
      mplew.write(packet.index());
      return mplew.getPacket();
   }

   protected byte[] showForeignCardEffect(ShowForeignCardEffect packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter(7);
      mplew.writeShort(SendOpcode.SHOW_FOREIGN_EFFECT.getValue());
      mplew.writeInt(packet.characterId());
      mplew.write(0x0D);
      return mplew.getPacket();
   }

   protected byte[] showForeignInfo(ShowForeignInfo packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.SHOW_FOREIGN_EFFECT.getValue());
      mplew.writeInt(packet.characterId());
      mplew.write(0x17);
      mplew.writeMapleAsciiString(packet.path());
      mplew.writeInt(1);
      return mplew.getPacket();
   }

   protected byte[] showForeignBuybackEffect(ShowForeignBuybackEffect packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.SHOW_FOREIGN_EFFECT.getValue());
      mplew.writeInt(packet.characterId());
      mplew.write(11);
      mplew.writeInt(0);

      return mplew.getPacket();
   }

   protected byte[] showForeignMakerEffect(ShowForeignMakerEffect packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.SHOW_FOREIGN_EFFECT.getValue());
      mplew.writeInt(packet.characterId());
      mplew.write(16);
      mplew.writeInt(packet.success() ? 0 : 1);
      return mplew.getPacket();
   }

   protected byte[] showForeignEffect(ShowForeignEffect packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.SHOW_FOREIGN_EFFECT.getValue());
      mplew.writeInt(packet.characterId());
      mplew.write(packet.effect());
      return mplew.getPacket();
   }

   protected byte[] showRecovery(ShowRecovery packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.SHOW_FOREIGN_EFFECT.getValue());
      mplew.writeInt(packet.characterId());
      mplew.write(0x0A);
      mplew.write(packet.amount());
      return mplew.getPacket();
   }

   protected byte[] getScrollEffect(ShowScrollEffect packet) {   // thanks to Rien dev team
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.SHOW_SCROLL_EFFECT.getValue());
      mplew.writeInt(packet.characterId());
      mplew.writeBool(packet.success() == ScrollResult.SUCCESS);
      mplew.writeBool(packet.success() == ScrollResult.CURSE);
      mplew.writeBool(packet.legendarySpirit());
      mplew.writeBool(packet.whiteScroll());
      return mplew.getPacket();
   }

   protected byte[] skillBookResult(ShowSkillBookResult packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.SKILL_LEARN_ITEM_RESULT.getValue());
      mplew.writeInt(packet.characterId());
      mplew.write(1);
      mplew.writeInt(packet.skillId());
      mplew.writeInt(packet.maxLevel());
      mplew.write(packet.canUse() ? 1 : 0);
      mplew.write(packet.success() ? 1 : 0);
      return mplew.getPacket();
   }
}