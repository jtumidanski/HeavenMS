package net.server.channel.packet.reader;

import net.server.PacketReader;
import net.server.channel.packet.CancelChairPacket;
import tools.data.input.SeekableLittleEndianAccessor;

public class CancelChairReader implements PacketReader<CancelChairPacket> {
   @Override
   public CancelChairPacket read(SeekableLittleEndianAccessor accessor) {
      int id = accessor.readShort();
      return new CancelChairPacket(id);
   }
}
