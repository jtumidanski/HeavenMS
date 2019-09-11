package net.server.channel.packet.reader;

import net.server.PacketReader;
import net.server.channel.packet.family.FamilySummonResponsePacket;
import tools.data.input.SeekableLittleEndianAccessor;

public class FamilySummonResponseReader implements PacketReader<FamilySummonResponsePacket> {
   @Override
   public FamilySummonResponsePacket read(SeekableLittleEndianAccessor accessor) {
      String familyName = accessor.readMapleAsciiString();
      boolean accept = accessor.readByte() != 0;
      return new FamilySummonResponsePacket(familyName, accept);
   }
}
