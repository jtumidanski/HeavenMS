package net.server.channel.packet.reader;

import net.server.PacketReader;
import net.server.channel.packet.UseMountFoodPacket;
import tools.data.input.SeekableLittleEndianAccessor;

public class UseMountFoodReader implements PacketReader<UseMountFoodPacket> {
   @Override
   public UseMountFoodPacket read(SeekableLittleEndianAccessor accessor) {
      accessor.skip(4);
      short pos = accessor.readShort();
      int itemId = accessor.readInt();
      return new UseMountFoodPacket(pos, itemId);
   }
}
