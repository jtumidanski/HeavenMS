package net.server.channel.packet.reader;

import net.server.PacketReader;
import net.server.channel.packet.InventoryMergePacket;
import tools.data.input.SeekableLittleEndianAccessor;

public class InventoryMergeReader implements PacketReader<InventoryMergePacket> {
   @Override
   public InventoryMergePacket read(SeekableLittleEndianAccessor accessor) {
      accessor.readInt();
      byte invType = accessor.readByte();
      return new InventoryMergePacket(invType);
   }
}
