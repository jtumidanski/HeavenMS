package net.server.channel.packet.reader;

import net.server.PacketReader;
import net.server.channel.packet.family.FamilyAddPacket;
import tools.data.input.SeekableLittleEndianAccessor;

public class FamilyAddReader implements PacketReader<FamilyAddPacket> {
   @Override
   public FamilyAddPacket read(SeekableLittleEndianAccessor accessor) {
      String toAdd = accessor.readMapleAsciiString();
      return new FamilyAddPacket(toAdd);
   }
}
