package net.server.channel.packet.reader;

import net.server.PacketReader;
import net.server.channel.packet.family.AcceptFamilyPacket;
import tools.data.input.SeekableLittleEndianAccessor;

public class AcceptFamilyReader implements PacketReader<AcceptFamilyPacket> {
   @Override
   public AcceptFamilyPacket read(SeekableLittleEndianAccessor accessor) {
      int inviterId = accessor.readInt();
      accessor.readMapleAsciiString();
      boolean accept = accessor.readByte() != 0;
      return new AcceptFamilyPacket(inviterId, accept);
   }
}
