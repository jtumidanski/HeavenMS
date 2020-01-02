package net.server.channel.packet.reader;

import net.server.PacketReader;
import net.server.channel.packet.UseItemUIPacket;
import tools.data.input.SeekableLittleEndianAccessor;

public class UseItemUIReader implements PacketReader<UseItemUIPacket> {
   @Override
   public UseItemUIPacket read(SeekableLittleEndianAccessor accessor) {
      byte inventoryType = accessor.readByte();
      short slot = accessor.readShort();
      int itemId = accessor.readInt();
      return new UseItemUIPacket(inventoryType, slot, itemId);
   }
}
