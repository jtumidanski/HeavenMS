package net.server.channel.packet.reader;

import net.server.PacketReader;
import net.server.channel.packet.guild.DenyGuildRequestPacket;
import tools.data.input.SeekableLittleEndianAccessor;

public class DenyGuildRequestReader implements PacketReader<DenyGuildRequestPacket> {
   @Override
   public DenyGuildRequestPacket read(SeekableLittleEndianAccessor accessor) {
      accessor.readByte();
      String characterName = accessor.readMapleAsciiString();
      return new DenyGuildRequestPacket(characterName);
   }
}
