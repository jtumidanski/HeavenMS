package net.server.channel.packet.reader;

import java.awt.Point;

import net.server.PacketReader;
import net.server.channel.packet.ItemPickupPacket;
import tools.data.input.SeekableLittleEndianAccessor;

public class ItemPickupReader implements PacketReader<ItemPickupPacket> {
   @Override
   public ItemPickupPacket read(SeekableLittleEndianAccessor accessor) {
      int timestamp = accessor.readInt();
      accessor.readByte();
      Point characterPosition = accessor.readPos();
      int oid = accessor.readInt();
      return new ItemPickupPacket(timestamp, characterPosition, oid);
   }
}
