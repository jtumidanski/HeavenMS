package net.server.channel.packet.reader;

import net.server.PacketReader;
import net.server.channel.packet.pet.PetExcludeItemsPacket;
import tools.data.input.SeekableLittleEndianAccessor;

public class PetExcludeItemsReader implements PacketReader<PetExcludeItemsPacket> {
   @Override
   public PetExcludeItemsPacket read(SeekableLittleEndianAccessor accessor) {
      final int petId = accessor.readInt();
      accessor.skip(4);
      byte amount = accessor.readByte();
      int[] itemIds = new int[amount];
      for (int i = 0; i < amount; i++) {
         itemIds[i] = accessor.readInt();
      }
      return new PetExcludeItemsPacket(petId, amount, itemIds);
   }
}
