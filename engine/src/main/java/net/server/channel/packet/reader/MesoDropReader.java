package net.server.channel.packet.reader;

import net.server.PacketReader;
import net.server.channel.packet.MesoDropPacket;
import tools.data.input.SeekableLittleEndianAccessor;

public class MesoDropReader implements PacketReader<MesoDropPacket> {
   @Override
   public MesoDropPacket read(SeekableLittleEndianAccessor accessor) {
      accessor.skip(4);
      int meso = accessor.readInt();
      return new MesoDropPacket(meso);
   }
}
