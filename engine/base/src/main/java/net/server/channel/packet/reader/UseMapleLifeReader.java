package net.server.channel.packet.reader;

import net.server.PacketReader;
import net.server.channel.packet.UseMapleLifePacket;
import tools.data.input.SeekableLittleEndianAccessor;

public class UseMapleLifeReader implements PacketReader<UseMapleLifePacket> {
   @Override
   public UseMapleLifePacket read(SeekableLittleEndianAccessor accessor) {
      String name = accessor.readMapleAsciiString();
      return new UseMapleLifePacket(name);
   }
}
