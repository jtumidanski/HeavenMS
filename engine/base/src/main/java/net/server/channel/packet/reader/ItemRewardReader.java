package net.server.channel.packet.reader;

import net.server.PacketReader;
import net.server.channel.packet.ItemRewardPacket;
import tools.data.input.SeekableLittleEndianAccessor;

public class ItemRewardReader implements PacketReader<ItemRewardPacket> {
   @Override
   public ItemRewardPacket read(SeekableLittleEndianAccessor accessor) {
      byte slot = (byte) accessor.readShort();
      int itemId = accessor.readInt(); // will load from xml I don't care.
      return new ItemRewardPacket(slot, itemId);
   }
}
