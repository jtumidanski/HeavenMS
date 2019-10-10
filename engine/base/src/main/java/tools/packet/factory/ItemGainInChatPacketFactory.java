package tools.packet.factory;

import net.opcodes.SendOpcode;
import tools.data.output.MaplePacketLittleEndianWriter;
import tools.packet.showitemgaininchat.ShowBuybackEffect;
import tools.packet.showitemgaininchat.ShowGainCard;
import tools.packet.showitemgaininchat.ShowInfo;
import tools.packet.showitemgaininchat.ShowIntro;
import tools.packet.showitemgaininchat.ShowItemGainInChat;
import tools.packet.showitemgaininchat.ShowMakerEffect;
import tools.packet.showitemgaininchat.ShowOwnBerserk;
import tools.packet.showitemgaininchat.ShowOwnBuffEffect;
import tools.packet.showitemgaininchat.ShowOwnPetLevelUp;
import tools.packet.showitemgaininchat.ShowOwnRecovery;
import tools.packet.showitemgaininchat.ShowSpecialEffect;
import tools.packet.showitemgaininchat.ShowWheelsLeft;

public class ItemGainInChatPacketFactory extends AbstractPacketFactory {
   private static ItemGainInChatPacketFactory instance;

   public static ItemGainInChatPacketFactory getInstance() {
      if (instance == null) {
         instance = new ItemGainInChatPacketFactory();
      }
      return instance;
   }

   private ItemGainInChatPacketFactory() {
      registry.setHandler(ShowItemGainInChat.class, packet -> create(SendOpcode.SHOW_ITEM_GAIN_INCHAT, this::getShowItemGain, packet));
      registry.setHandler(ShowOwnBuffEffect.class, packet -> create(SendOpcode.SHOW_ITEM_GAIN_INCHAT, this::showOwnBuffEffect, packet));
      registry.setHandler(ShowOwnBerserk.class, packet -> create(SendOpcode.SHOW_ITEM_GAIN_INCHAT, this::showOwnBerserk, packet));
      registry.setHandler(ShowOwnPetLevelUp.class, packet -> create(SendOpcode.SHOW_ITEM_GAIN_INCHAT, this::showOwnPetLevelUp, packet));
      registry.setHandler(ShowGainCard.class, packet -> create(SendOpcode.SHOW_ITEM_GAIN_INCHAT, this::showGainCard, packet, 3));
      registry.setHandler(ShowIntro.class, packet -> create(SendOpcode.SHOW_ITEM_GAIN_INCHAT, this::showIntro, packet));
      registry.setHandler(ShowInfo.class, packet -> create(SendOpcode.SHOW_ITEM_GAIN_INCHAT, this::showInfo, packet));
      registry.setHandler(ShowBuybackEffect.class, packet -> create(SendOpcode.SHOW_ITEM_GAIN_INCHAT, this::showBuybackEffect, packet));
      registry.setHandler(ShowSpecialEffect.class, packet -> create(SendOpcode.SHOW_ITEM_GAIN_INCHAT, this::showSpecialEffect, packet));
      registry.setHandler(ShowMakerEffect.class, packet -> create(SendOpcode.SHOW_ITEM_GAIN_INCHAT, this::showMakerEffect, packet));
      registry.setHandler(ShowOwnRecovery.class, packet -> create(SendOpcode.SHOW_ITEM_GAIN_INCHAT, this::showOwnRecovery, packet));
      registry.setHandler(ShowWheelsLeft.class, packet -> create(SendOpcode.SHOW_ITEM_GAIN_INCHAT, this::showWheelsLeft, packet));
   }

   /**
    * Gets a packet telling the client to show an item gain.
    *
    * @return The item gain packet.
    */
   protected void getShowItemGain(MaplePacketLittleEndianWriter writer, ShowItemGainInChat packet) {
      writer.write(3);
      writer.write(1);
      writer.writeInt(packet.itemId());
      writer.writeInt(packet.quantity());
   }

   protected void showOwnBuffEffect(MaplePacketLittleEndianWriter writer, ShowOwnBuffEffect packet) {
      writer.write(packet.effectId());
      writer.writeInt(packet.skillId());
      writer.write(0xA9);
      writer.write(1);
   }

   protected void showOwnBerserk(MaplePacketLittleEndianWriter writer, ShowOwnBerserk packet) {
      writer.write(1);
      writer.writeInt(1320006);
      writer.write(0xA9);
      writer.write(packet.skillLevel());
      writer.write(packet.berserk() ? 1 : 0);
   }

   protected void showOwnPetLevelUp(MaplePacketLittleEndianWriter writer, ShowOwnPetLevelUp packet) {
      writer.write(4);
      writer.write(0);
      writer.write(packet.index()); // Pet Index
   }

   protected void showGainCard(MaplePacketLittleEndianWriter writer, ShowGainCard packet) {
      writer.write(0x0D);
   }

   protected void showIntro(MaplePacketLittleEndianWriter writer, ShowIntro packet) {
      writer.write(0x12);
      writer.writeMapleAsciiString(packet.path());
   }

   protected void showInfo(MaplePacketLittleEndianWriter writer, ShowInfo packet) {
      writer.write(0x17);
      writer.writeMapleAsciiString(packet.path());
      writer.writeInt(1);
   }

   protected void showBuybackEffect(MaplePacketLittleEndianWriter writer, ShowBuybackEffect packet) {
      writer.write(11);
      writer.writeInt(0);
   }

   /**
    * 0 = Levelup 6 = Exp did not drop (Safety Charms) 7 = Enter portal sound
    * 8 = Job change 9 = Quest complete 10 = Recovery 11 = Buff effect
    * 14 = Monster book pickup 15 = Equipment levelup 16 = Maker Skill Success
    * 17 = Buff effect w/ sfx 19 = Exp card [500, 200, 50] 21 = Wheel of destiny
    * 26 = Spirit Stone
    *
    * @return
    */
   protected void showSpecialEffect(MaplePacketLittleEndianWriter writer, ShowSpecialEffect packet) {
      writer.write(packet.effectId());
   }

   protected void showMakerEffect(MaplePacketLittleEndianWriter writer, ShowMakerEffect packet) {
      writer.write(16);
      writer.writeInt(packet.success() ? 0 : 1);
   }

   protected void showOwnRecovery(MaplePacketLittleEndianWriter writer, ShowOwnRecovery packet) {
      writer.write(0x0A);
      writer.write(packet.amount());
   }

   protected void showWheelsLeft(MaplePacketLittleEndianWriter writer, ShowWheelsLeft packet) {
      writer.write(0x15);
      writer.write(packet.remaining());
   }
}