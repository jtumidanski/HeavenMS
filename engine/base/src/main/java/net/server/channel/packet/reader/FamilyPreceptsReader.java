package net.server.channel.packet.reader;

import net.server.PacketReader;
import net.server.channel.packet.family.FamilyPreceptsPacket;
import tools.data.input.SeekableLittleEndianAccessor;

public class FamilyPreceptsReader implements PacketReader<FamilyPreceptsPacket> {
   @Override
   public FamilyPreceptsPacket read(SeekableLittleEndianAccessor accessor) {
      String newPrecepts = accessor.readMapleAsciiString();
      return new FamilyPreceptsPacket(newPrecepts);
   }
}
