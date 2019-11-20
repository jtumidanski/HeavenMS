package net.server.channel.packet.reader;

import net.server.PacketReader;
import net.server.channel.packet.pet.PetLootPacket;
import tools.data.input.SeekableLittleEndianAccessor;

public class PetLootReader implements PacketReader<PetLootPacket> {
   @Override
   public PetLootPacket read(SeekableLittleEndianAccessor accessor) {
      int petIndex = accessor.readInt();
      accessor.skip(13);
      int objectId = accessor.readInt();
      return new PetLootPacket(petIndex, objectId);
   }
}
