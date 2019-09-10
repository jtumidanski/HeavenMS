package net.server.channel.packet.reader;

import net.server.PacketReader;
import net.server.channel.packet.CancelBuffPacket;
import tools.data.input.SeekableLittleEndianAccessor;

public class CancelBuffReader implements PacketReader<CancelBuffPacket> {
   @Override
   public CancelBuffPacket read(SeekableLittleEndianAccessor accessor) {
      int sourceId = accessor.readInt();
      return new CancelBuffPacket(sourceId);
   }
}
