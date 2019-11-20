package net.server.channel.packet.reader;

import net.server.PacketReader;
import net.server.channel.packet.party.PartySearchStartPacket;
import tools.data.input.SeekableLittleEndianAccessor;

public class PartySearchStartReader implements PacketReader<PartySearchStartPacket> {
   @Override
   public PartySearchStartPacket read(SeekableLittleEndianAccessor accessor) {
      int min = accessor.readInt();
      int max = accessor.readInt();
      int members = accessor.readInt(); // members
      int jobs = accessor.readInt();
      return new PartySearchStartPacket(min, max, members, jobs);
   }
}
