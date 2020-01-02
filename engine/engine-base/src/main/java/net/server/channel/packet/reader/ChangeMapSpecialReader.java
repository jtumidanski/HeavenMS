package net.server.channel.packet.reader;

import net.server.PacketReader;
import net.server.channel.packet.ChangeMapSpecialPacket;
import tools.data.input.SeekableLittleEndianAccessor;

public class ChangeMapSpecialReader implements PacketReader<ChangeMapSpecialPacket> {
   @Override
   public ChangeMapSpecialPacket read(SeekableLittleEndianAccessor accessor) {
      accessor.readByte();
      String startWarp = accessor.readMapleAsciiString();
      accessor.readShort();
      return new ChangeMapSpecialPacket(startWarp);
   }
}
