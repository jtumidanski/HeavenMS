package net.server.channel.packet.reader;

import net.server.PacketReader;
import net.server.channel.packet.SpouseChatPacket;
import tools.data.input.SeekableLittleEndianAccessor;

public class SpouseChatReader implements PacketReader<SpouseChatPacket> {
   @Override
   public SpouseChatPacket read(SeekableLittleEndianAccessor accessor) {
      String recipient = accessor.readMapleAsciiString();
      String msg = accessor.readMapleAsciiString();
      return new SpouseChatPacket(recipient, msg);
   }
}
