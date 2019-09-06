package net.server.channel.packet.reader;

import net.server.PacketReader;
import net.server.channel.packet.WhisperPacket;
import tools.data.input.SeekableLittleEndianAccessor;

public class WhisperReader implements PacketReader<WhisperPacket> {
   @Override
   public WhisperPacket read(SeekableLittleEndianAccessor accessor) {
      byte mode = accessor.readByte();
      String recipient = "";
      String message = "";

      if (mode == 6) { // whisper
         recipient = accessor.readMapleAsciiString();
      } else if (mode == 5) { // - /find
         recipient = accessor.readMapleAsciiString();
      } else if (mode == 0x44) {
         recipient = accessor.readMapleAsciiString();
         message = accessor.readMapleAsciiString();
      }

      return new WhisperPacket(mode, recipient, message);
   }
}
