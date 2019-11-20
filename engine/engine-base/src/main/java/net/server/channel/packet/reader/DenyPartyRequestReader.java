package net.server.channel.packet.reader;

import net.server.PacketReader;
import net.server.channel.packet.party.DenyPartyRequestPacket;
import tools.data.input.SeekableLittleEndianAccessor;

public class DenyPartyRequestReader implements PacketReader<DenyPartyRequestPacket> {
   @Override
   public DenyPartyRequestPacket read(SeekableLittleEndianAccessor accessor) {
      accessor.readByte();
      String message = accessor.readMapleAsciiString();
      return new DenyPartyRequestPacket(message);
   }
}
