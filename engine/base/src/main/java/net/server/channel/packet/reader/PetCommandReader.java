package net.server.channel.packet.reader;

import net.server.PacketReader;
import net.server.channel.packet.pet.PetCommandPacket;
import tools.data.input.SeekableLittleEndianAccessor;

public class PetCommandReader implements PacketReader<PetCommandPacket> {
   @Override
   public PetCommandPacket read(SeekableLittleEndianAccessor accessor) {
      int petId = accessor.readInt();
      accessor.readInt();
      accessor.readByte();
      byte command = accessor.readByte();
      return new PetCommandPacket(petId, command);
   }
}
