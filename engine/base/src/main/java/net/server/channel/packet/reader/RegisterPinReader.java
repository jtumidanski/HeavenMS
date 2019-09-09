package net.server.channel.packet.reader;

import net.server.PacketReader;
import net.server.login.packet.RegisterPinPacket;
import tools.data.input.SeekableLittleEndianAccessor;

public class RegisterPinReader implements PacketReader<RegisterPinPacket> {
   @Override
   public RegisterPinPacket read(SeekableLittleEndianAccessor accessor) {
      byte c2 = accessor.readByte();
      String pin = "";
      if (c2 == 0) {
         pin = accessor.readMapleAsciiString();
      }
      return new RegisterPinPacket(c2, pin);
   }
}
