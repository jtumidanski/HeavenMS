package tools.packet.factory;

import client.BuddyListEntry;
import tools.StringUtil;
import tools.data.output.MaplePacketLittleEndianWriter;
import tools.packet.buddy.BuddyListMessage;
import tools.packet.buddy.RequestAddBuddy;
import tools.packet.buddy.UpdateBuddyCapacity;
import tools.packet.buddy.UpdateBuddyChannel;
import tools.packet.buddy.UpdateBuddyList;

public class BuddyPacketFactory extends AbstractPacketFactory {
   private static BuddyPacketFactory instance;

   public static BuddyPacketFactory getInstance() {
      if (instance == null) {
         instance = new BuddyPacketFactory();
      }
      return instance;
   }

   private BuddyPacketFactory() {
      Handler.handle(UpdateBuddyList.class).decorate(this::updateBuddyList).register(registry);
      Handler.handle(BuddyListMessage.class).decorate(this::buddyListMessage).register(registry);
      Handler.handle(RequestAddBuddy.class).decorate(this::requestBuddyListAdd).register(registry);
      Handler.handle(UpdateBuddyChannel.class).decorate(this::updateBuddyChannel).register(registry);
      Handler.handle(UpdateBuddyCapacity.class).decorate(this::updateBuddyCapacity).register(registry);
   }

   protected void updateBuddyList(MaplePacketLittleEndianWriter writer, UpdateBuddyList packet) {
      writer.write(7);
      writer.write(packet.buddies().size());
      for (BuddyListEntry buddy : packet.buddies()) {
         if (buddy.visible()) {
            writer.writeInt(buddy.characterId()); // cid
            writer.writeAsciiString(StringUtil.getRightPaddedStr(buddy.name(), '\0', 13));
            writer.write(0); // opposite status
            writer.writeInt(buddy.channel() - 1);
            writer.writeAsciiString(StringUtil.getRightPaddedStr(buddy.group(), '\0', 13));
            writer.writeInt(0);//map id?
         }
      }
      for (int x = 0; x < packet.buddies().size(); x++) {
         writer.writeInt(0);//map id?
      }
   }

   protected void buddyListMessage(MaplePacketLittleEndianWriter writer, BuddyListMessage packet) {
      writer.write(packet.message());
   }

   protected void requestBuddyListAdd(MaplePacketLittleEndianWriter writer, RequestAddBuddy packet) {
      writer.write(9);
      writer.writeInt(packet.characterIdFrom());
      writer.writeMapleAsciiString(packet.characterNameFrom());
      writer.writeInt(packet.characterIdFrom());
      writer.writeAsciiString(StringUtil.getRightPaddedStr(packet.characterNameFrom(), '\0', 11));
      writer.write(0x09);
      writer.write(0xf0);
      writer.write(0x01);
      writer.writeInt(0x0f);
      writer.writeNullTerminatedAsciiString("Default Group");
      writer.writeInt(packet.characterIdTo());
   }

   protected void updateBuddyChannel(MaplePacketLittleEndianWriter writer, UpdateBuddyChannel packet) {
      writer.write(0x14);
      writer.writeInt(packet.characterId());
      writer.write(0);
      writer.writeInt(packet.channel());
   }

   protected void updateBuddyCapacity(MaplePacketLittleEndianWriter writer, UpdateBuddyCapacity packet) {
      writer.write(0x15);
      writer.write(packet.capacity());
   }
}