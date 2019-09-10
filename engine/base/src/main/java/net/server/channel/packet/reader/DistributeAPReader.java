package net.server.channel.packet.reader;

import net.server.PacketReader;
import net.server.channel.packet.DistributeAPPacket;
import tools.data.input.SeekableLittleEndianAccessor;

public class DistributeAPReader implements PacketReader<DistributeAPPacket> {
   @Override
   public DistributeAPPacket read(SeekableLittleEndianAccessor accessor) {
      accessor.readInt();
      int num = accessor.readInt();
      return new DistributeAPPacket(num);
   }
}
