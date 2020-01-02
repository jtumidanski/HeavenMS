package net.server.channel.packet.reader;

import net.server.PacketReader;
import net.server.channel.packet.wedding.AddRegistryItemPacket;
import net.server.channel.packet.wedding.BaseWeddingPacket;
import net.server.channel.packet.wedding.OutOfRegistryPacket;
import net.server.channel.packet.wedding.TakeRegistryItemsPacket;
import tools.data.input.SeekableLittleEndianAccessor;

public class WeddingReader implements PacketReader<BaseWeddingPacket> {
   @Override
   public BaseWeddingPacket read(SeekableLittleEndianAccessor accessor) {
      final byte mode = accessor.readByte();
      if (mode == 6) {
         return readAddItem(accessor, mode);
      } else if (mode == 7) {
         return readTakeRegistryItems(accessor, mode);
      } else if (mode == 8) {
         return readOutOfRegistry(mode);
      }
      return new BaseWeddingPacket(mode);
   }

   private BaseWeddingPacket readOutOfRegistry(byte mode) {
      return new OutOfRegistryPacket(mode);
   }

   private BaseWeddingPacket readTakeRegistryItems(SeekableLittleEndianAccessor accessor, byte mode) {
      accessor.readByte();    // invType
      int itemPos = accessor.readByte();
      return new TakeRegistryItemsPacket(mode, itemPos);
   }

   private BaseWeddingPacket readAddItem(SeekableLittleEndianAccessor accessor, byte mode) {
      short slot = accessor.readShort();
      int itemId = accessor.readInt();
      short quantity = accessor.readShort();
      return new AddRegistryItemPacket(mode, slot, itemId, quantity);
   }
}
