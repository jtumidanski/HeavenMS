package net.server.channel.packet.reader;

import net.server.PacketReader;
import net.server.channel.packet.NPCShopPacket;
import tools.data.input.SeekableLittleEndianAccessor;

public class NPCShopReader implements PacketReader<NPCShopPacket> {
   @Override
   public NPCShopPacket read(SeekableLittleEndianAccessor accessor) {
      byte mode = accessor.readByte();
      short slot = -1;
      int itemId = -1;
      short quantity = -1;

      if (mode == 0) { // mode 0 = buy :)
         slot = accessor.readShort();// slot
         itemId = accessor.readInt();
         quantity = accessor.readShort();
      } else if (mode == 1) { // sell ;)
         slot = accessor.readShort();
         itemId = accessor.readInt();
         quantity = accessor.readShort();
      } else if (mode == 2) { // recharge ;)
         slot = (byte) accessor.readShort();
      }

      return new NPCShopPacket(mode, slot, itemId, quantity);
   }
}
