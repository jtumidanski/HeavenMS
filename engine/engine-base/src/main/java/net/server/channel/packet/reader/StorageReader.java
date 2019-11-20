package net.server.channel.packet.reader;

import net.server.PacketReader;
import net.server.channel.packet.storage.ArrangeItemsPacket;
import net.server.channel.packet.storage.BaseStoragePacket;
import net.server.channel.packet.storage.ClosePacket;
import net.server.channel.packet.storage.MesoPacket;
import net.server.channel.packet.storage.StorePacket;
import net.server.channel.packet.storage.TakeoutPacket;
import tools.data.input.SeekableLittleEndianAccessor;

public class StorageReader implements PacketReader<BaseStoragePacket> {
   @Override
   public BaseStoragePacket read(SeekableLittleEndianAccessor accessor) {
      byte mode = accessor.readByte();
      if (mode == 4) {
         byte type = accessor.readByte();
         byte slot = accessor.readByte();
         return new TakeoutPacket(mode, type, slot);
      } else if (mode == 5) {
         short slot = accessor.readShort();
         int itemId = accessor.readInt();
         short quantity = accessor.readShort();
         return new StorePacket(mode, slot, itemId, quantity);
      } else if (mode == 6) {
         return new ArrangeItemsPacket(mode);
      } else if (mode == 7) {
         int meso = accessor.readInt();
         return new MesoPacket(mode, meso);
      } else if (mode == 8) {
         return new ClosePacket(mode);
      }
      return new BaseStoragePacket(mode);
   }
}
