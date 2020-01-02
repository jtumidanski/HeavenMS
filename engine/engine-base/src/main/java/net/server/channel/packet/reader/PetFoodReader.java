package net.server.channel.packet.reader;

import net.server.PacketReader;
import net.server.channel.packet.pet.PetFoodPacket;
import tools.data.input.SeekableLittleEndianAccessor;

public class PetFoodReader implements PacketReader<PetFoodPacket> {
   @Override
   public PetFoodPacket read(SeekableLittleEndianAccessor accessor) {
      int timestamp = accessor.readInt();
      short pos = accessor.readShort();
      int itemId = accessor.readInt();
      return new PetFoodPacket(timestamp, pos, itemId);
   }
}
