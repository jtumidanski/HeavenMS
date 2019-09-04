package net.server.packet.reader;

import net.server.PacketReader;
import net.server.packet.NoOpPacket;
import tools.data.input.SeekableLittleEndianAccessor;

public class NoOpReader implements PacketReader<NoOpPacket> {
   @Override
   public NoOpPacket read(SeekableLittleEndianAccessor accessor) {
      return new NoOpPacket();
   }
}
