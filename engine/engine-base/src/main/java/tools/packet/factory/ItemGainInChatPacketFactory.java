package tools.packet.factory;

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
      Handler.handle(ShowItemGainInChat.class).decorate(this::getShowItemGain).register(registry);
      Handler.handle(ShowOwnBuffEffect.class).decorate(this::showOwnBuffEffect).register(registry);
      Handler.handle(ShowOwnBerserk.class).decorate(this::showOwnBerserk).register(registry);
      Handler.handle(ShowOwnPetLevelUp.class).decorate(this::showOwnPetLevelUp).register(registry);
      Handler.handle(ShowGainCard.class).decorate(this::showGainCard).size(3).register(registry);
      Handler.handle(ShowIntro.class).decorate(this::showIntro).register(registry);
      Handler.handle(ShowInfo.class).decorate(this::showInfo).register(registry);
      Handler.handle(ShowBuybackEffect.class).decorate(this::showBuybackEffect).register(registry);
      Handler.handle(ShowSpecialEffect.class).decorate(this::showSpecialEffect).register(registry);
      Handler.handle(ShowMakerEffect.class).decorate(this::showMakerEffect).register(registry);
      Handler.handle(ShowOwnRecovery.class).decorate(this::showOwnRecovery).register(registry);
      Handler.handle(ShowWheelsLeft.class).decorate(this::showWheelsLeft).register(registry);
   }

   /**
    * Gets a packet telling the client to show an item gain.
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
    * 0 = Level up 6 = Exp did not drop (Safety Charms) 7 = Enter portal sound
    * 8 = Job change 9 = Quest complete 10 = Recovery 11 = Buff effect
    * 14 = Monster book pickup 15 = Equipment level up 16 = Maker Skill Success
    * 17 = Buff effect w/ sfx 19 = Exp card [500, 200, 50] 21 = Wheel of destiny
    * 26 = Spirit Stone
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