package net.server.channel.packet.reader;

import net.server.PacketReader;
import net.server.channel.packet.SnowballPacket;
import tools.data.input.SeekableLittleEndianAccessor;

public class SnowballReader implements PacketReader<SnowballPacket> {
   @Override
   public SnowballPacket read(SeekableLittleEndianAccessor accessor) {
      int what = accessor.readByte();
      return new SnowballPacket(what);
   }
}
