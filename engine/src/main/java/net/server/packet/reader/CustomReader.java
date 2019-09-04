package net.server.packet.reader;

import net.server.PacketReader;
import net.server.packet.CustomPacket;
import tools.data.input.SeekableLittleEndianAccessor;

public class CustomReader implements PacketReader<CustomPacket> {
   @Override
   public CustomPacket read(SeekableLittleEndianAccessor accessor) {
      if (accessor.available() > 0) {
         return new CustomPacket(accessor.read((int) accessor.available()));
      }
      return new CustomPacket(new byte[0]);
   }
}
