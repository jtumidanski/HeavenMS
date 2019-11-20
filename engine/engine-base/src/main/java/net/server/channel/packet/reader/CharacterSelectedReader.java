package net.server.channel.packet.reader;

import net.server.PacketReader;
import net.server.login.packet.CharacterSelectedPacket;
import tools.data.input.SeekableLittleEndianAccessor;

public class CharacterSelectedReader implements PacketReader<CharacterSelectedPacket> {
   @Override
   public CharacterSelectedPacket read(SeekableLittleEndianAccessor accessor) {
      int charId = accessor.readInt();
      String macs = accessor.readMapleAsciiString();
      String hwid = accessor.readMapleAsciiString();
      return new CharacterSelectedPacket(charId, macs, hwid);
   }
}
