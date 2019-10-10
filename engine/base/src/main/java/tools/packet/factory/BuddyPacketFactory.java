package tools.packet.factory;

import client.BuddyListEntry;
import net.opcodes.SendOpcode;
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
      registry.setHandler(UpdateBuddyList.class, packet -> create(SendOpcode.BUDDYLIST, this::updateBuddylist, packet));
      registry.setHandler(BuddyListMessage.class, packet -> create(SendOpcode.BUDDYLIST, this::buddylistMessage, packet));
      registry.setHandler(RequestAddBuddy.class, packet -> create(SendOpcode.BUDDYLIST, this::requestBuddylistAdd, packet));
      registry.setHandler(UpdateBuddyChannel.class, packet -> create(SendOpcode.BUDDYLIST, this::updateBuddyChannel, packet));
      registry.setHandler(UpdateBuddyCapacity.class, packet -> create(SendOpcode.BUDDYLIST, this::updateBuddyCapacity, packet));
   }

   protected void updateBuddylist(MaplePacketLittleEndianWriter writer, UpdateBuddyList packet) {
      writer.write(7);
      writer.write(packet.buddies().size());
      for (BuddyListEntry buddy : packet.buddies()) {
         if (buddy.visible()) {
            writer.writeInt(buddy.characterId()); // cid
            writer.writeAsciiString(StringUtil.getRightPaddedStr(buddy.name(), '\0', 13));
            writer.write(0); // opposite status
            writer.writeInt(buddy.channel() - 1);
            writer.writeAsciiString(StringUtil.getRightPaddedStr(buddy.group(), '\0', 13));
            writer.writeInt(0);//mapid?
         }
      }
      for (int x = 0; x < packet.buddies().size(); x++) {
         writer.writeInt(0);//mapid?
      }
   }

   protected void buddylistMessage(MaplePacketLittleEndianWriter writer, BuddyListMessage packet) {
      writer.write(packet.message());
   }

   protected void requestBuddylistAdd(MaplePacketLittleEndianWriter writer, RequestAddBuddy packet) {
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