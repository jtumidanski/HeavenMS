package net.server.channel.packet.reader;

import net.server.PacketReader;
import net.server.channel.packet.InventorySortPacket;
import tools.data.input.SeekableLittleEndianAccessor;

public class InventorySortReader implements PacketReader<InventorySortPacket> {
   @Override
   public InventorySortPacket read(SeekableLittleEndianAccessor accessor) {
      accessor.readInt();
      byte invType = accessor.readByte();
      return new InventorySortPacket(invType);
   }
}
