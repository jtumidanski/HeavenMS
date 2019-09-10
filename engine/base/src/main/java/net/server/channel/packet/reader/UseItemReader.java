package net.server.channel.packet.reader;

import net.server.PacketReader;
import net.server.channel.packet.UseItemPacket;
import tools.data.input.SeekableLittleEndianAccessor;

public class UseItemReader implements PacketReader<UseItemPacket> {
   @Override
   public UseItemPacket read(SeekableLittleEndianAccessor accessor) {
      accessor.readInt();
      short slot = accessor.readShort();
      int itemId = accessor.readInt();
      return new UseItemPacket(slot, itemId);
   }
}
