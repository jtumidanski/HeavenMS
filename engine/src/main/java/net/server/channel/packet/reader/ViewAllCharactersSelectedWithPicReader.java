package net.server.channel.packet.reader;

import net.server.PacketReader;
import net.server.login.packet.ViewAllCharactersSelectedWithPicPacket;
import tools.data.input.SeekableLittleEndianAccessor;

public class ViewAllCharactersSelectedWithPicReader implements PacketReader<ViewAllCharactersSelectedWithPicPacket> {
   @Override
   public ViewAllCharactersSelectedWithPicPacket read(SeekableLittleEndianAccessor accessor) {
      String pic = accessor.readMapleAsciiString();
      int charId = accessor.readInt();
      accessor.readInt(); // please don't let the client choose which world they should login
      String macs = accessor.readMapleAsciiString();
      String hwid = accessor.readMapleAsciiString();
      return new ViewAllCharactersSelectedWithPicPacket(pic, charId, macs, hwid);
   }
}
