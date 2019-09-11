package net.server.channel.packet.reader;

import net.server.PacketReader;
import net.server.channel.packet.UseItemUIPacket;
import tools.data.input.SeekableLittleEndianAccessor;

public class UseItemUIReader implements PacketReader<UseItemUIPacket> {
   @Override
   public UseItemUIPacket read(SeekableLittleEndianAccessor accessor) {
      byte inventorytype = accessor.readByte();//nItemIT
      short slot = accessor.readShort();//nSlotPosition
      int itemid = accessor.readInt();//nItemID
      return new UseItemUIPacket(inventorytype, slot, itemid);
   }
}
