package net.server.channel.packet.reader;

import net.server.PacketReader;
import net.server.login.packet.AfterLoginPacket;
import tools.data.input.SeekableLittleEndianAccessor;

public class AfterLoginReader implements PacketReader<AfterLoginPacket> {
   @Override
   public AfterLoginPacket read(SeekableLittleEndianAccessor accessor) {
      byte c2 = accessor.readByte();
      byte c3 = 5;
      if (accessor.available() > 0) {
         c3 = accessor.readByte();
      }

      if (c2 == 1 && c3 == 1) {
         return new AfterLoginPacket(c2, c3);
      } else if (c2 == 1 && c3 == 0) {
         String pin = accessor.readMapleAsciiString();
         return new AfterLoginPacket(c2, c3, pin);
      } else if (c2 == 2 && c3 == 0) {
         String pin = accessor.readMapleAsciiString();
         return new AfterLoginPacket(c2, c3, pin);
      } else if (c2 == 0 && c3 == 5) {
         return new AfterLoginPacket(c2, c3);
      }
      return new AfterLoginPacket(c2);
   }
}
