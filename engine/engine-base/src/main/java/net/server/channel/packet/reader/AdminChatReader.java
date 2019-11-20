package net.server.channel.packet.reader;

import net.server.PacketReader;
import net.server.channel.packet.AdminChatPacket;
import tools.data.input.SeekableLittleEndianAccessor;

public class AdminChatReader implements PacketReader<AdminChatPacket> {
   @Override
   public AdminChatPacket read(SeekableLittleEndianAccessor accessor) {
      byte mode = accessor.readByte();
      String message = accessor.readMapleAsciiString();
      int noticeType = accessor.readByte();
      return new AdminChatPacket(mode, message, noticeType);
   }
}
