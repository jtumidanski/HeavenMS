package net.server.channel.packet.reader;

import net.server.PacketReader;
import net.server.login.packet.ViewAllCharactersRegisterPicPacket;
import tools.data.input.SeekableLittleEndianAccessor;

public class ViewAllCharactersRegisterPicReader implements PacketReader<ViewAllCharactersRegisterPicPacket> {
   @Override
   public ViewAllCharactersRegisterPicPacket read(SeekableLittleEndianAccessor accessor) {
      accessor.readByte();
      int charId = accessor.readInt();
      accessor.readInt(); // please don't let the client choose which world they should login

      String mac = accessor.readMapleAsciiString();
      String hwid = accessor.readMapleAsciiString();
      String pic = accessor.readMapleAsciiString();

      return new ViewAllCharactersRegisterPicPacket(charId, mac, hwid, pic);
   }
}
