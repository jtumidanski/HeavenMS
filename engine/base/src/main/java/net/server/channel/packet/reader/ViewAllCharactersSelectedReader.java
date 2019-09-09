package net.server.channel.packet.reader;

import net.server.PacketReader;
import net.server.login.packet.ViewAllCharactersSelectedPacket;
import tools.data.input.SeekableLittleEndianAccessor;

public class ViewAllCharactersSelectedReader implements PacketReader<ViewAllCharactersSelectedPacket> {
   @Override
   public ViewAllCharactersSelectedPacket read(SeekableLittleEndianAccessor accessor) {
      int charId = accessor.readInt();
      accessor.readInt(); // please don't let the client choose which world they should login

      String macs = accessor.readMapleAsciiString();
      String hwid = accessor.readMapleAsciiString();

      return new ViewAllCharactersSelectedPacket(charId, macs, hwid);
   }
}
