package net.server.channel.packet.reader;

import net.server.PacketReader;
import net.server.login.packet.CharacterSelectedWithPicPacket;
import tools.data.input.SeekableLittleEndianAccessor;

public class CharacterSelectedWithPicReader implements PacketReader<CharacterSelectedWithPicPacket> {
   @Override
   public CharacterSelectedWithPicPacket read(SeekableLittleEndianAccessor accessor) {
      return new CharacterSelectedWithPicPacket(accessor.readMapleAsciiString(), accessor.readInt(),
            accessor.readMapleAsciiString(), accessor.readMapleAsciiString());
   }
}
