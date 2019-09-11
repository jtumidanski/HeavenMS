package net.server.channel.packet.reader;

import net.server.PacketReader;
import net.server.channel.packet.UseDeathItemPacket;
import tools.data.input.SeekableLittleEndianAccessor;

public class UseDeathItemReader implements PacketReader<UseDeathItemPacket> {
   @Override
   public UseDeathItemPacket read(SeekableLittleEndianAccessor accessor) {
      int itemId = accessor.readInt();
      return new UseDeathItemPacket(itemId);
   }
}
