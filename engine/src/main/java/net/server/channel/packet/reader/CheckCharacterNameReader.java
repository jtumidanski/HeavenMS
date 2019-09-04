package net.server.channel.packet.reader;

import net.server.PacketReader;
import net.server.login.packet.CheckCharacterNamePacket;
import tools.data.input.SeekableLittleEndianAccessor;

public class CheckCharacterNameReader implements PacketReader<CheckCharacterNamePacket> {
   @Override
   public CheckCharacterNamePacket read(SeekableLittleEndianAccessor accessor) {
      return new CheckCharacterNamePacket(accessor.readMapleAsciiString());
   }
}
