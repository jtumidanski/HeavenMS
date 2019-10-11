package tools.packet.factory;

import tools.data.output.MaplePacketLittleEndianWriter;
import tools.packet.statusinfo.CompleteQuest;
import tools.packet.statusinfo.GetDojoInfo;
import tools.packet.statusinfo.GetDojoInfoMessage;
import tools.packet.statusinfo.GetGuildPointMessage;
import tools.packet.statusinfo.GetItemMessage;
import tools.packet.statusinfo.ShowBunny;
import tools.packet.statusinfo.ShowEXPGain;
import tools.packet.statusinfo.ShowFameGain;
import tools.packet.statusinfo.ShowInfoText;
import tools.packet.statusinfo.ShowInventoryFull;
import tools.packet.statusinfo.ShowItemExpired;
import tools.packet.statusinfo.ShowItemGain;
import tools.packet.statusinfo.ShowItemUnavailable;
import tools.packet.statusinfo.ShowMesoGain;
import tools.packet.statusinfo.ShowQuestForfeit;
import tools.packet.statusinfo.UpdateAreaInfo;
import tools.packet.statusinfo.UpdateDojoStats;
import tools.packet.statusinfo.UpdateQuest;

public class StatusInfoPacketFactory extends AbstractPacketFactory {
   private static StatusInfoPacketFactory instance;

   public static StatusInfoPacketFactory getInstance() {
      if (instance == null) {
         instance = new StatusInfoPacketFactory();
      }
      return instance;
   }

   private StatusInfoPacketFactory() {
      Handler.handle(ShowEXPGain.class).decorate(this::getShowExpGain).register(registry);
      Handler.handle(ShowFameGain.class).decorate(this::getShowFameGain).register(registry);
      Handler.handle(ShowMesoGain.class).decorate(this::getShowMesoGain).register(registry);
      Handler.handle(ShowQuestForfeit.class).decorate(this::forfeitQuest).register(registry);
      Handler.handle(CompleteQuest.class).decorate(this::completeQuest).register(registry);
      Handler.handle(UpdateQuest.class).decorate(this::updateQuest).register(registry);
      Handler.handle(ShowInventoryFull.class).decorate(this::getShowInventoryFull).register(registry);
      Handler.handle(ShowItemUnavailable.class).decorate(this::showItemUnavailable).register(registry);
      Handler.handle(UpdateAreaInfo.class).decorate(this::updateAreaInfo).register(registry);
      Handler.handle(GetGuildPointMessage.class).decorate(this::getGPMessage).size(7).register(registry);
      Handler.handle(GetItemMessage.class).decorate(this::getItemMessage).size(7).register(registry);
      Handler.handle(ShowInfoText.class).decorate(this::showInfoText).register(registry);
      Handler.handle(GetDojoInfo.class).decorate(this::getDojoInfo).register(registry);
      Handler.handle(GetDojoInfoMessage.class).decorate(this::getDojoInfoMessage).register(registry);
      Handler.handle(UpdateDojoStats.class).decorate(this::updateDojoStats).register(registry);
      Handler.handle(ShowItemExpired.class).decorate(this::itemExpired).register(registry);
      Handler.handle(ShowBunny.class).decorate(this::bunnyPacket).register(registry);
      Handler.handle(ShowItemGain.class).decorate(this::getShowItemGain).register(registry);
   }

   /**
    * Gets a packet telling the client to show an EXP increase.
    *
    * @return The exp gained packet.
    */
   protected void getShowExpGain(MaplePacketLittleEndianWriter writer, ShowEXPGain packet) {
      writer.write(3); // 3 = exp, 4 = fame, 5 = mesos, 6 = guildpoints
      writer.writeBool(packet.white());
      writer.writeInt(packet.gain());
      writer.writeBool(packet.inChat());
      writer.writeInt(0); // bonus event exp
      writer.write(0); // third monster kill event
      writer.write(0); // RIP byte, this is always a 0
      writer.writeInt(0); //wedding bonus
      if (packet.inChat()) { // quest bonus rate stuff
         writer.write(0);
      }

      writer.write(0); //0 = party bonus, 100 = 1x Bonus EXP, 200 = 2x Bonus EXP
      writer.writeInt(packet.party()); // party bonus
      writer.writeInt(packet.equip()); //equip bonus
      writer.writeInt(0); //Internet Cafe Bonus
      writer.writeInt(0); //Rainbow Week Bonus
   }

   /**
    * Gets a packet telling the client to show a fame gain.
    *
    * @return The meso gain packet.
    */
   protected void getShowFameGain(MaplePacketLittleEndianWriter writer, ShowFameGain packet) {
      writer.write(4);
      writer.writeInt(packet.gain());
   }

   /**
    * Gets a packet telling the client to show a meso gain.
    *
    * @return The meso gain packet.
    */
   protected void getShowMesoGain(MaplePacketLittleEndianWriter writer, ShowMesoGain packet) {
      if (!packet.inChat()) {
         writer.write(0);
         writer.writeShort(1); //v83
      } else {
         writer.write(5);
      }
      writer.writeInt(packet.gain());
      writer.writeShort(0);
   }

   protected void forfeitQuest(MaplePacketLittleEndianWriter writer, ShowQuestForfeit packet) {
      writer.write(1);
      writer.writeShort(packet.questId());
      writer.write(0);
   }

   protected void completeQuest(MaplePacketLittleEndianWriter writer, CompleteQuest packet) {
      writer.write(1);
      writer.writeShort(packet.questId());
      writer.write(2);
      writer.writeLong(getTime(packet.time()));
   }

   protected void updateQuest(MaplePacketLittleEndianWriter writer, UpdateQuest packet) {
      writer.write(1);
      writer.writeShort(packet.infoUpdate() ? packet.infoNumber() : packet.questId());
      if (packet.infoUpdate()) {
         writer.write(1);
      } else {
         writer.write(packet.questStatusId());
      }

      writer.writeMapleAsciiString(packet.questData());
      writer.skip(5);
   }

   protected void getShowInventoryFull(MaplePacketLittleEndianWriter writer, ShowInventoryFull packet) {
      getShowInventoryStatus(writer, 0xff);
   }

   protected void showItemUnavailable(MaplePacketLittleEndianWriter writer, ShowItemUnavailable packet) {
      getShowInventoryStatus(writer, 0xfe);
   }

   protected void getShowInventoryStatus(MaplePacketLittleEndianWriter writer, int mode) {
      writer.write(0);
      writer.write(mode);
      writer.writeInt(0);
      writer.writeInt(0);
   }

   protected void updateAreaInfo(MaplePacketLittleEndianWriter writer, UpdateAreaInfo packet) {
      writer.write(0x0A); //0x0B in v95
      writer.writeShort(packet.areaId());//infoNumber
      writer.writeMapleAsciiString(packet.info());
   }

   protected void getGPMessage(MaplePacketLittleEndianWriter writer, GetGuildPointMessage packet) {
      writer.write(6);
      writer.writeInt(packet.change());
   }

   protected void getItemMessage(MaplePacketLittleEndianWriter writer, GetItemMessage packet) {
      writer.write(7);
      writer.writeInt(packet.itemId());
   }

   protected void showInfoText(MaplePacketLittleEndianWriter writer, ShowInfoText packet) {
      writer.write(9);
      writer.writeMapleAsciiString(packet.text());
   }

   protected void getDojoInfo(MaplePacketLittleEndianWriter writer, GetDojoInfo packet) {
      writer.write(10);
      writer.write(new byte[]{(byte) 0xB7, 4});//QUEST ID f5
      writer.writeMapleAsciiString(packet.info());
   }

   protected void getDojoInfoMessage(MaplePacketLittleEndianWriter writer, GetDojoInfoMessage packet) {
      writer.write(9);
      writer.writeMapleAsciiString(packet.message());
   }

   protected void updateDojoStats(MaplePacketLittleEndianWriter writer, UpdateDojoStats packet) {
      writer.write(10);
      writer.write(new byte[]{(byte) 0xB7, 4}); //?
      writer.writeMapleAsciiString("pt=" + packet.dojoPoints() + ";belt=" + packet.belt() + ";tuto=" + (packet.finishedDojoTutorial() ? "1" : "0"));
   }

   protected void itemExpired(MaplePacketLittleEndianWriter writer, ShowItemExpired packet) {
      writer.write(2);
      writer.writeInt(packet.itemId());
   }

   protected void bunnyPacket(MaplePacketLittleEndianWriter writer, ShowBunny packet) {
      writer.write(9);
      writer.writeAsciiString("Protect the Moon Bunny!!!");
   }

   /**
    * Gets a packet telling the client to show an item gain.
    *
    * @return The item gain packet.
    */
   protected void getShowItemGain(MaplePacketLittleEndianWriter writer, ShowItemGain packet) {
      writer.writeShort(0);
      writer.writeInt(packet.itemId());
      writer.writeInt(packet.quantity());
      writer.writeInt(0);
      writer.writeInt(0);
   }
}