package tools.packet.factory;

import client.inventory.ScrollResult;
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
      Handler.handle(ShowBuffEffect.class).decorate(this::showBuffEffect).register(registry);
      Handler.handle(ShowBuffEffectWithLevel.class).decorate(this::showBuffEffectWithLevel).register(registry);
      Handler.handle(ShowBerserk.class).decorate(this::showBerserk).register(registry);
      Handler.handle(ShowPetLevelUp.class).decorate(this::showPetLevelUp).register(registry);
      Handler.handle(ShowForeignCardEffect.class).decorate(this::showForeignCardEffect).size(7).register(registry);
      Handler.handle(ShowForeignInfo.class).decorate(this::showForeignInfo).register(registry);
      Handler.handle(ShowForeignBuybackEffect.class).decorate(this::showForeignBuybackEffect).register(registry);
      Handler.handle(ShowForeignMakerEffect.class).decorate(this::showForeignMakerEffect).register(registry);
      Handler.handle(ShowForeignEffect.class).decorate(this::showForeignEffect).register(registry);
      Handler.handle(ShowRecovery.class).decorate(this::showRecovery).register(registry);
      Handler.handle(ShowScrollEffect.class).decorate(this::getScrollEffect).register(registry);
      Handler.handle(ShowSkillBookResult.class).decorate(this::skillBookResult).register(registry);
      Handler.handle(ShowCombo.class).decorate(this::showCombo).size(6).register(registry);
      Handler.handle(ShowHint.class).decorate(this::sendHint).register(registry);
      Handler.handle(ShowSkillEffect.class).decorate(this::skillEffect).register(registry);
      Handler.handle(ShowItemEffect.class).decorate(this::itemEffect).register(registry);
      Handler.handle(ShowGuideHint.class).decorate(this::guideHint).size(11).register(registry);
      Handler.handle(ShowGuideTalk.class).decorate(this::talkGuide).register(registry);
      Handler.handle(ShowChair.class).decorate(this::showChair).register(registry);
      Handler.handle(CancelChair.class).decorate(this::cancelChair).register(registry);
      Handler.handle(CancelSkill.class).decorate(this::skillCancel).register(registry);
      Handler.handle(ShowTitleEarned.class).decorate(this::earnTitleMessage).register(registry);
      Handler.handle(ShowBlockedMessage.class).decorate(this::blockedMessage).register(registry);
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