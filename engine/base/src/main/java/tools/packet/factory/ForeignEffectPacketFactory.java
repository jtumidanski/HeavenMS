package tools.packet.factory;

import client.inventory.ScrollResult;
import net.opcodes.SendOpcode;
import tools.data.output.MaplePacketLittleEndianWriter;
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
      registry.setHandler(ShowBuffEffect.class, packet -> create(SendOpcode.SHOW_FOREIGN_EFFECT, this::showBuffEffect, packet));
      registry.setHandler(ShowBuffEffectWithLevel.class, packet -> create(SendOpcode.SHOW_FOREIGN_EFFECT, this::showBuffEffectWithLevel, packet));
      registry.setHandler(ShowBerserk.class, packet -> create(SendOpcode.SHOW_FOREIGN_EFFECT, this::showBerserk, packet));
      registry.setHandler(ShowPetLevelUp.class, packet -> create(SendOpcode.SHOW_FOREIGN_EFFECT, this::showPetLevelUp, packet));
      registry.setHandler(ShowForeignCardEffect.class, packet -> create(SendOpcode.SHOW_FOREIGN_EFFECT, this::showForeignCardEffect, packet, 7));
      registry.setHandler(ShowForeignInfo.class, packet -> create(SendOpcode.SHOW_FOREIGN_EFFECT, this::showForeignInfo, packet));
      registry.setHandler(ShowForeignBuybackEffect.class, packet -> create(SendOpcode.SHOW_FOREIGN_EFFECT, this::showForeignBuybackEffect, packet));
      registry.setHandler(ShowForeignMakerEffect.class, packet -> create(SendOpcode.SHOW_FOREIGN_EFFECT, this::showForeignMakerEffect, packet));
      registry.setHandler(ShowForeignEffect.class, packet -> create(SendOpcode.SHOW_FOREIGN_EFFECT, this::showForeignEffect, packet));
      registry.setHandler(ShowRecovery.class, packet -> create(SendOpcode.SHOW_FOREIGN_EFFECT, this::showRecovery, packet));
      registry.setHandler(ShowScrollEffect.class, packet -> create(SendOpcode.SHOW_SCROLL_EFFECT, this::getScrollEffect, packet));
      registry.setHandler(ShowSkillBookResult.class, packet -> create(SendOpcode.SKILL_LEARN_ITEM_RESULT, this::skillBookResult, packet));
      registry.setHandler(ShowCombo.class, packet -> create(SendOpcode.SHOW_COMBO, this::showCombo, packet, 6));
      registry.setHandler(ShowHint.class, packet -> create(SendOpcode.PLAYER_HINT, this::sendHint, packet));
      registry.setHandler(ShowSkillEffect.class, packet -> create(SendOpcode.SKILL_EFFECT, this::skillEffect, packet));
      registry.setHandler(ShowItemEffect.class, packet -> create(SendOpcode.SHOW_ITEM_EFFECT, this::itemEffect, packet));
      registry.setHandler(ShowGuideHint.class, packet -> create(SendOpcode.TALK_GUIDE, this::guideHint, packet, 11));
      registry.setHandler(ShowGuideTalk.class, packet -> create(SendOpcode.TALK_GUIDE, this::talkGuide, packet));
      registry.setHandler(ShowChair.class, packet -> create(SendOpcode.SHOW_CHAIR, this::showChair, packet));
      registry.setHandler(CancelChair.class, packet -> create(SendOpcode.CANCEL_CHAIR, this::cancelChair, packet));
      registry.setHandler(CancelSkill.class, packet -> create(SendOpcode.CANCEL_SKILL_EFFECT, this::skillCancel, packet));
      registry.setHandler(ShowTitleEarned.class, packet -> create(SendOpcode.SCRIPT_PROGRESS_MESSAGE, this::earnTitleMessage, packet));
      registry.setHandler(ShowBlockedMessage.class, packet -> create(SendOpcode.BLOCKED_MAP, this::blockedMessage, packet));
   }

   protected void showBuffEffect(MaplePacketLittleEndianWriter writer, ShowBuffEffect packet) {
      writer.writeInt(packet.characterId());
      writer.write(packet.effectId()); //buff level
      writer.writeInt(packet.skillId());
      writer.write(packet.direction());
      writer.write(1);
      writer.writeLong(0);
   }

   protected void showBuffEffectWithLevel(MaplePacketLittleEndianWriter writer, ShowBuffEffectWithLevel packet) {   // updated packet structure found thanks to Rien dev team
      writer.writeInt(packet.characterId());
      writer.write(packet.effectId());
      writer.writeInt(packet.skillId());
      writer.write(0);
      writer.write(packet.skillLevel());
      writer.write(packet.direction());
   }

   protected void showBerserk(MaplePacketLittleEndianWriter writer, ShowBerserk packet) {
      writer.writeInt(packet.characterId());
      writer.write(1);
      writer.writeInt(1320006);
      writer.write(0xA9);
      writer.write(packet.skillLevel());
      writer.write(packet.berserk() ? 1 : 0);
   }

   protected void showPetLevelUp(MaplePacketLittleEndianWriter writer, ShowPetLevelUp packet) {
      writer.writeInt(packet.characterId());
      writer.write(4);
      writer.write(0);
      writer.write(packet.index());
   }

   protected void showForeignCardEffect(MaplePacketLittleEndianWriter writer, ShowForeignCardEffect packet) {
      writer.writeInt(packet.characterId());
      writer.write(0x0D);
   }

   protected void showForeignInfo(MaplePacketLittleEndianWriter writer, ShowForeignInfo packet) {
      writer.writeInt(packet.characterId());
      writer.write(0x17);
      writer.writeMapleAsciiString(packet.path());
      writer.writeInt(1);
   }

   protected void showForeignBuybackEffect(MaplePacketLittleEndianWriter writer, ShowForeignBuybackEffect packet) {
      writer.writeInt(packet.characterId());
      writer.write(11);
      writer.writeInt(0);
   }

   protected void showForeignMakerEffect(MaplePacketLittleEndianWriter writer, ShowForeignMakerEffect packet) {
      writer.writeInt(packet.characterId());
      writer.write(16);
      writer.writeInt(packet.success() ? 0 : 1);
   }

   protected void showForeignEffect(MaplePacketLittleEndianWriter writer, ShowForeignEffect packet) {
      writer.writeInt(packet.characterId());
      writer.write(packet.effect());
   }

   protected void showRecovery(MaplePacketLittleEndianWriter writer, ShowRecovery packet) {
      writer.writeInt(packet.characterId());
      writer.write(0x0A);
      writer.write(packet.amount());
   }

   protected void getScrollEffect(MaplePacketLittleEndianWriter writer, ShowScrollEffect packet) {   // thanks to Rien dev team
      writer.writeInt(packet.characterId());
      writer.writeBool(packet.success() == ScrollResult.SUCCESS);
      writer.writeBool(packet.success() == ScrollResult.CURSE);
      writer.writeBool(packet.legendarySpirit());
      writer.writeBool(packet.whiteScroll());
   }

   protected void skillBookResult(MaplePacketLittleEndianWriter writer, ShowSkillBookResult packet) {
      writer.writeInt(packet.characterId());
      writer.write(1);
      writer.writeInt(packet.skillId());
      writer.writeInt(packet.maxLevel());
      writer.write(packet.canUse() ? 1 : 0);
      writer.write(packet.success() ? 1 : 0);
   }

   protected void showCombo(MaplePacketLittleEndianWriter writer, ShowCombo packet) {
      writer.writeInt(packet.count());
   }

   /**
    * Sends a player hint.
    *
    * @return The player hint packet.
    */
   protected void sendHint(MaplePacketLittleEndianWriter writer, ShowHint packet) {
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
      writer.writeMapleAsciiString(packet.hint());
      writer.writeShort(width);
      writer.writeShort(height);
      writer.write(1);
   }

   protected void skillEffect(MaplePacketLittleEndianWriter writer, ShowSkillEffect packet) {
      writer.writeInt(packet.characterId());
      writer.writeInt(packet.skillId());
      writer.write(packet.level());
      writer.write(packet.flags());
      writer.write(packet.speed());
      writer.write(packet.direction()); //Mmmk
   }

   protected void itemEffect(MaplePacketLittleEndianWriter writer, ShowItemEffect packet) {
      writer.writeInt(packet.characterId());
      writer.writeInt(packet.itemId());
   }

   protected void guideHint(MaplePacketLittleEndianWriter writer, ShowGuideHint packet) {
      writer.write(1);
      writer.writeInt(packet.hint());
      writer.writeInt(7000);
   }

   protected void talkGuide(MaplePacketLittleEndianWriter writer, ShowGuideTalk packet) {
      writer.write(0);
      writer.writeMapleAsciiString(packet.talk());
      writer.write(new byte[]{(byte) 0xC8, 0, 0, 0, (byte) 0xA0, (byte) 0x0F, 0, 0});
   }

   protected void showChair(MaplePacketLittleEndianWriter writer, ShowChair packet) {
      writer.writeInt(packet.characterId());
      writer.writeInt(packet.itemId());
   }

   protected void cancelChair(MaplePacketLittleEndianWriter writer, CancelChair packet) {
      if (packet.itemId() < 0) {
         writer.write(0);
      } else {
         writer.write(1);
         writer.writeShort(packet.itemId());
      }
   }

   protected void skillCancel(MaplePacketLittleEndianWriter writer, CancelSkill packet) {
      writer.writeInt(packet.fromCharacterId());
      writer.writeInt(packet.skillId());
   }

   protected void earnTitleMessage(MaplePacketLittleEndianWriter writer, ShowTitleEarned packet) {
      writer.writeMapleAsciiString(packet.message());
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
   protected void blockedMessage(MaplePacketLittleEndianWriter writer, ShowBlockedMessage packet) {
      writer.write(packet.theType());
   }
}