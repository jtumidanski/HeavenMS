package net.server.channel.packet.reader;

import net.server.PacketReader;
import net.server.channel.packet.alliance.DenyAllianceRequestPacket;
import tools.data.input.SeekableLittleEndianAccessor;

public class DenyAllianceRequestReader implements PacketReader<DenyAllianceRequestPacket> {
   @Override
   public DenyAllianceRequestPacket read(SeekableLittleEndianAccessor accessor) {
      accessor.readByte();
      String inviterName = accessor.readMapleAsciiString();
      String guildName = accessor.readMapleAsciiString();
      return new DenyAllianceRequestPacket(inviterName, guildName);
   }
}
