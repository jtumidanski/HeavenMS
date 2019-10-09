package tools.packet.factory;

import client.inventory.ScrollResult;
import net.opcodes.SendOpcode;
import tools.FilePrinter;
import tools.data.output.MaplePacketLittleEndianWriter;
import tools.packet.PacketInput;
import tools.packet.foreigneffect.CancelChair;
import tools.packet.foreigneffect.CancelSkill;
import tools.packet.foreigneffect.ShowBerserk;
import tools.packet.foreigneffect.ShowBlockedMessage;
import tools.packet.foreigneffect.ShowBuffEffect;
import tools.packet.foreigneffect.ShowBuffEffectWithLevel;
import tools.packet.foreigneffect.ShowChair;
import tools.packet.foreigneffect.ShowCombo;
import tools.packet.foreigneffect.ShowForeignBuybackEffect;
import tools.packet.foreigneffect.ShowForeignCardEffect;
import tools.packet.foreigneffect.ShowForeignEffect;
import tools.packet.foreigneffect.ShowForeignInfo;
import tools.packet.foreigneffect.ShowForeignMakerEffect;
import tools.packet.foreigneffect.ShowGuideHint;
import tools.packet.foreigneffect.ShowGuideTalk;
import tools.packet.foreigneffect.ShowHint;
import tools.packet.foreigneffect.ShowItemEffect;
import tools.packet.foreigneffect.ShowPetLevelUp;
import tools.packet.foreigneffect.ShowRecovery;
import tools.packet.foreigneffect.ShowScrollEffect;
import tools.packet.foreigneffect.ShowSkillBookResult;
import tools.packet.foreigneffect.ShowSkillEffect;
import tools.packet.foreigneffect.ShowTitleEarned;

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
      } else if (packetInput instanceof ShowCombo) {
         return create(this::showCombo, packetInput);
      } else if (packetInput instanceof ShowHint) {
         return create(this::sendHint, packetInput);
      } else if (packetInput instanceof ShowSkillEffect) {
         return create(this::skillEffect, packetInput);
      } else if (packetInput instanceof ShowItemEffect) {
         return create(this::itemEffect, packetInput);
      } else if (packetInput instanceof ShowGuideHint) {
         return create(this::guideHint, packetInput);
      } else if (packetInput instanceof ShowGuideTalk) {
         return create(this::talkGuide, packetInput);
      } else if (packetInput instanceof ShowChair) {
         return create(this::showChair, packetInput);
      } else if (packetInput instanceof CancelChair) {
         return create(this::cancelChair, packetInput);
      } else if (packetInput instanceof CancelSkill) {
         return create(this::skillCancel, packetInput);
      } else if (packetInput instanceof ShowTitleEarned) {
         return create(this::earnTitleMessage, packetInput);
      } else if (packetInput instanceof ShowBlockedMessage) {
         return create(this::blockedMessage, packetInput);
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

   protected byte[] showCombo(ShowCombo packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter(6);
      mplew.writeShort(SendOpcode.SHOW_COMBO.getValue());
      mplew.writeInt(packet.count());
      return mplew.getPacket();
   }

   /**
    * Sends a player hint.
    *
    * @return The player hint packet.
    */
   protected byte[] sendHint(ShowHint packet) {
      int width = packet.width();
      int height = packet.height();

      if (width < 1) {
         width = packet.hint().length() * 10;
         if (width < 40) {
            width = 40;
         }
      }
      if (height < 5) {
         height = 5;
      }
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.PLAYER_HINT.getValue());
      mplew.writeMapleAsciiString(packet.hint());
      mplew.writeShort(width);
      mplew.writeShort(height);
      mplew.write(1);
      return mplew.getPacket();
   }

   protected byte[] skillEffect(ShowSkillEffect packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.SKILL_EFFECT.getValue());
      mplew.writeInt(packet.characterId());
      mplew.writeInt(packet.skillId());
      mplew.write(packet.level());
      mplew.write(packet.flags());
      mplew.write(packet.speed());
      mplew.write(packet.direction()); //Mmmk
      return mplew.getPacket();
   }

   protected byte[] itemEffect(ShowItemEffect packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.SHOW_ITEM_EFFECT.getValue());
      mplew.writeInt(packet.characterId());
      mplew.writeInt(packet.itemId());
      return mplew.getPacket();
   }

   protected byte[] guideHint(ShowGuideHint packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter(11);
      mplew.writeShort(SendOpcode.TALK_GUIDE.getValue());
      mplew.write(1);
      mplew.writeInt(packet.hint());
      mplew.writeInt(7000);
      return mplew.getPacket();
   }

   protected byte[] talkGuide(ShowGuideTalk packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.TALK_GUIDE.getValue());
      mplew.write(0);
      mplew.writeMapleAsciiString(packet.talk());
      mplew.write(new byte[]{(byte) 0xC8, 0, 0, 0, (byte) 0xA0, (byte) 0x0F, 0, 0});
      return mplew.getPacket();
   }

   protected byte[] showChair(ShowChair packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.SHOW_CHAIR.getValue());
      mplew.writeInt(packet.characterId());
      mplew.writeInt(packet.itemId());
      return mplew.getPacket();
   }

   protected byte[] cancelChair(CancelChair packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.CANCEL_CHAIR.getValue());
      if (packet.itemId() < 0) {
         mplew.write(0);
      } else {
         mplew.write(1);
         mplew.writeShort(packet.itemId());
      }
      return mplew.getPacket();
   }

   protected byte[] skillCancel(CancelSkill packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.CANCEL_SKILL_EFFECT.getValue());
      mplew.writeInt(packet.fromCharacterId());
      mplew.writeInt(packet.skillId());
      return mplew.getPacket();
   }

   protected byte[] earnTitleMessage(ShowTitleEarned packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.SCRIPT_PROGRESS_MESSAGE.getValue());
      mplew.writeMapleAsciiString(packet.message());
      return mplew.getPacket();
   }

   /**
    * Gets a "block" packet (ie. the cash shop is unavailable, etc)
    * <p>
    * Possible values for <code>type</code>:<br> 1: The portal is closed for
    * now.<br> 2: You cannot go to that place.<br> 3: Unable to approach due to
    * the force of the ground.<br> 4: You cannot teleport to or on this
    * map.<br> 5: Unable to approach due to the force of the ground.<br> 6:
    * This map can only be entered by party members.<br> 7: The Cash Shop is
    * currently not available. Stay tuned...<br>
    */
   protected byte[] blockedMessage(ShowBlockedMessage packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.BLOCKED_MAP.getValue());
      mplew.write(packet.theType());
      return mplew.getPacket();
   }
}