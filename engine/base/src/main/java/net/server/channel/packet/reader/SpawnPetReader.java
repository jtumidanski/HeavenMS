package net.server.channel.packet.reader;

import net.server.PacketReader;
import net.server.channel.packet.SpawnPetPacket;
import tools.data.input.SeekableLittleEndianAccessor;

public class SpawnPetReader implements PacketReader<SpawnPetPacket> {
   @Override
   public SpawnPetPacket read(SeekableLittleEndianAccessor accessor) {
      accessor.readInt();
      byte slot = accessor.readByte();
      accessor.readByte();
      boolean lead = accessor.readByte() == 1;

      return new SpawnPetPacket(slot, lead);
   }
}
