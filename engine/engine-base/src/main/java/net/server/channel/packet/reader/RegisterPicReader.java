package net.server.channel.packet.reader;

import net.server.PacketReader;
import net.server.login.packet.RegisterPicPacket;
import tools.data.input.SeekableLittleEndianAccessor;

public class RegisterPicReader implements PacketReader<RegisterPicPacket> {
   @Override
   public RegisterPicPacket read(SeekableLittleEndianAccessor accessor) {
      accessor.readByte();
      int charId = accessor.readInt();
      String macs = accessor.readMapleAsciiString();
      String hwid = accessor.readMapleAsciiString();
      String pic = accessor.readMapleAsciiString();
      return new RegisterPicPacket(charId, macs, hwid, pic);
   }
}
