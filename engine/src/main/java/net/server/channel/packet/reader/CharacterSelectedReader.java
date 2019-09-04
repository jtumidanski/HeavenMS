package net.server.channel.packet.reader;

import net.server.PacketReader;
import net.server.login.packet.CharacterSelectedPacket;
import tools.data.input.SeekableLittleEndianAccessor;

public class CharacterSelectedReader implements PacketReader<CharacterSelectedPacket> {
   @Override
   public CharacterSelectedPacket read(SeekableLittleEndianAccessor slea) {
      int charId = slea.readInt();
      String macs = slea.readMapleAsciiString();
      String hwid = slea.readMapleAsciiString();
      return new CharacterSelectedPacket(charId, macs, hwid);
   }
}
