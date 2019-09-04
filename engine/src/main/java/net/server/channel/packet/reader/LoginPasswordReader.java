package net.server.channel.packet.reader;

import net.server.PacketReader;
import net.server.login.packet.LoginPasswordPacket;
import tools.data.input.SeekableLittleEndianAccessor;

public class LoginPasswordReader implements PacketReader<LoginPasswordPacket> {
   @Override
   public LoginPasswordPacket read(SeekableLittleEndianAccessor accessor) {
      String login = accessor.readMapleAsciiString();
      String pwd = accessor.readMapleAsciiString();
      accessor.skip(6);   // localhost masked the initial part with zeroes...
      byte[] hwidNibbles = accessor.read(4);
      return new LoginPasswordPacket(login, pwd, hwidNibbles);
   }
}
