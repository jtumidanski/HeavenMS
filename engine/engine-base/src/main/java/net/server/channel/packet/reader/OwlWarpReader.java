package net.server.channel.packet.reader;

import net.server.PacketReader;
import net.server.channel.packet.OwlWarpPacket;
import tools.data.input.SeekableLittleEndianAccessor;

public class OwlWarpReader implements PacketReader<OwlWarpPacket> {
   @Override
   public OwlWarpPacket read(SeekableLittleEndianAccessor accessor) {
      int ownerid = accessor.readInt();
      int mapid = accessor.readInt();
      return new OwlWarpPacket(ownerid, mapid);
   }
}
