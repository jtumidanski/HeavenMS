package net.server.channel.packet.reader;

import net.server.PacketReader;
import net.server.channel.packet.ScrollPacket;
import tools.data.input.SeekableLittleEndianAccessor;

public class ScrollReader implements PacketReader<ScrollPacket> {
   @Override
   public ScrollPacket read(SeekableLittleEndianAccessor accessor) {
      accessor.readInt(); // whatever...
      short slot = accessor.readShort();
      short dst = accessor.readShort();
      byte ws = (byte) accessor.readShort();
      return new ScrollPacket(slot, dst, ws);
   }
}
