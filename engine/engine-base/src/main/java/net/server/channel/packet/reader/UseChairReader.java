package net.server.channel.packet.reader;

import net.server.PacketReader;
import net.server.channel.packet.UseChairPacket;
import tools.data.input.SeekableLittleEndianAccessor;

public class UseChairReader implements PacketReader<UseChairPacket> {
   @Override
   public UseChairPacket read(SeekableLittleEndianAccessor accessor) {
      int itemId = accessor.readInt();
      return new UseChairPacket(itemId);
   }
}
