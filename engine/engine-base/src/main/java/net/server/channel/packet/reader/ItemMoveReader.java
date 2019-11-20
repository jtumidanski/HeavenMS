package net.server.channel.packet.reader;

import net.server.PacketReader;
import net.server.channel.packet.ItemMovePacket;
import tools.data.input.SeekableLittleEndianAccessor;

public class ItemMoveReader implements PacketReader<ItemMovePacket> {
   @Override
   public ItemMovePacket read(SeekableLittleEndianAccessor accessor) {
      accessor.skip(4);
      byte type = accessor.readByte();
      short src = accessor.readShort();     //is there any reason to use byte instead of short in src and action?
      short action = accessor.readShort();
      short quantity = accessor.readShort();

      return new ItemMovePacket(type, src, action, quantity);
   }
}
