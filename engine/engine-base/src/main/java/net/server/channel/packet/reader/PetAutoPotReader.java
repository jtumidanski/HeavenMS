package net.server.channel.packet.reader;

import net.server.PacketReader;
import net.server.channel.packet.pet.PetAutoPotPacket;
import tools.data.input.SeekableLittleEndianAccessor;

public class PetAutoPotReader implements PacketReader<PetAutoPotPacket> {
   @Override
   public PetAutoPotPacket read(SeekableLittleEndianAccessor accessor) {
      accessor.readByte();
      accessor.readLong();
      accessor.readInt();
      short slot = accessor.readShort();
      int itemId = accessor.readInt();
      return new PetAutoPotPacket(slot, itemId);
   }
}
