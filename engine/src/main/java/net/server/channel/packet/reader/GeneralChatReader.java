package net.server.channel.packet.reader;

import net.server.PacketReader;
import net.server.channel.packet.GeneralChatPacket;
import tools.data.input.SeekableLittleEndianAccessor;

public class GeneralChatReader implements PacketReader<GeneralChatPacket> {
   @Override
   public GeneralChatPacket read(SeekableLittleEndianAccessor accessor) {
      String message = accessor.readMapleAsciiString();
      int show = accessor.readByte();
      return new GeneralChatPacket(message, show);
   }
}
