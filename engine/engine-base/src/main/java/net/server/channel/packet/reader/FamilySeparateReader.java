package net.server.channel.packet.reader;

import net.server.PacketReader;
import net.server.channel.packet.family.FamilySeparatePacket;
import tools.data.input.SeekableLittleEndianAccessor;

public class FamilySeparateReader implements PacketReader<FamilySeparatePacket> {
   @Override
   public FamilySeparatePacket read(SeekableLittleEndianAccessor accessor) {
      boolean available = accessor.available() > 0;
      int characterId = -1;
      if (available) {
         characterId = accessor.readInt();
      }

      return new FamilySeparatePacket(available, characterId);
   }
}
