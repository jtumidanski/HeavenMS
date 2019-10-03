package tools.packet.factory;

import client.BuddyListEntry;
import net.opcodes.SendOpcode;
import net.server.channel.packet.buddy.AddBuddyPacket;
import tools.FilePrinter;
import tools.StringUtil;
import tools.data.output.MaplePacketLittleEndianWriter;
import tools.packet.PacketInput;
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
   }

   @Override
   public byte[] create(PacketInput packetInput) {
      if (packetInput instanceof UpdateBuddyList) {
         return create(this::updateBuddylist, packetInput);
      } else if (packetInput instanceof BuddyListMessage) {
         return create(this::buddylistMessage, packetInput);
      } else if (packetInput instanceof RequestAddBuddy) {
         return create(this::requestBuddylistAdd, packetInput);
      } else if (packetInput instanceof UpdateBuddyChannel) {
         return create(this::updateBuddyChannel, packetInput);
      } else if (packetInput instanceof UpdateBuddyCapacity) {
         return create(this::updateBuddyCapacity, packetInput);
      }
      FilePrinter.printError(FilePrinter.PACKET_LOGS + "generic.txt", "Trying to handle invalid input " + packetInput.toString());
      return new byte[0];
   }

   protected byte[] updateBuddylist(UpdateBuddyList packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.BUDDYLIST.getValue());
      mplew.write(7);
      mplew.write(packet.buddies().size());
      for (BuddyListEntry buddy : packet.buddies()) {
         if (buddy.visible()) {
            mplew.writeInt(buddy.characterId()); // cid
            mplew.writeAsciiString(StringUtil.getRightPaddedStr(buddy.name(), '\0', 13));
            mplew.write(0); // opposite status
            mplew.writeInt(buddy.channel() - 1);
            mplew.writeAsciiString(StringUtil.getRightPaddedStr(buddy.group(), '\0', 13));
            mplew.writeInt(0);//mapid?
         }
      }
      for (int x = 0; x < packet.buddies().size(); x++) {
         mplew.writeInt(0);//mapid?
      }
      return mplew.getPacket();
   }

   protected byte[] buddylistMessage(BuddyListMessage packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.BUDDYLIST.getValue());
      mplew.write(packet.message());
      return mplew.getPacket();
   }

   protected byte[] requestBuddylistAdd(RequestAddBuddy packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.BUDDYLIST.getValue());
      mplew.write(9);
      mplew.writeInt(packet.characterIdFrom());
      mplew.writeMapleAsciiString(packet.characterNameFrom());
      mplew.writeInt(packet.characterIdFrom());
      mplew.writeAsciiString(StringUtil.getRightPaddedStr(packet.characterNameFrom(), '\0', 11));
      mplew.write(0x09);
      mplew.write(0xf0);
      mplew.write(0x01);
      mplew.writeInt(0x0f);
      mplew.writeNullTerminatedAsciiString("Default Group");
      mplew.writeInt(packet.characterIdTo());
      return mplew.getPacket();
   }

   protected byte[] updateBuddyChannel(UpdateBuddyChannel packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.BUDDYLIST.getValue());
      mplew.write(0x14);
      mplew.writeInt(packet.characterId());
      mplew.write(0);
      mplew.writeInt(packet.channel());
      return mplew.getPacket();
   }

   protected byte[] updateBuddyCapacity(UpdateBuddyCapacity packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.BUDDYLIST.getValue());
      mplew.write(0x15);
      mplew.write(packet.capacity());
      return mplew.getPacket();
   }
}