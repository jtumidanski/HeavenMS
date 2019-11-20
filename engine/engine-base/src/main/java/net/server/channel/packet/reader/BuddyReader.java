package net.server.channel.packet.reader;

import net.server.PacketReader;
import net.server.channel.packet.buddy.AcceptBuddyPacket;
import net.server.channel.packet.buddy.AddBuddyPacket;
import net.server.channel.packet.buddy.BaseBuddyPacket;
import net.server.channel.packet.buddy.DeleteBuddyPacket;
import tools.data.input.SeekableLittleEndianAccessor;

public class BuddyReader implements PacketReader<BaseBuddyPacket> {
   @Override
   public BaseBuddyPacket read(SeekableLittleEndianAccessor accessor) {
      int mode = accessor.readByte();
      if (mode == 1) {
         String addName = accessor.readMapleAsciiString();
         String group = accessor.readMapleAsciiString();
         return new AddBuddyPacket(mode, addName, group);
      } else if (mode == 2) {
         int otherCid = accessor.readInt();
         return new AcceptBuddyPacket(mode, otherCid);
      } else if (mode == 3) {
         int otherCid = accessor.readInt();
         return new DeleteBuddyPacket(mode, otherCid);
      }
      return new BaseBuddyPacket(mode);
   }
}
