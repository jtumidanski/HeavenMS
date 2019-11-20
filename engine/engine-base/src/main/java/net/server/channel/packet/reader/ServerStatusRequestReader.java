package net.server.channel.packet.reader;

import net.server.PacketReader;
import net.server.login.packet.ServerStatusRequestPacket;
import tools.data.input.SeekableLittleEndianAccessor;

public class ServerStatusRequestReader implements PacketReader<ServerStatusRequestPacket> {
   @Override
   public ServerStatusRequestPacket read(SeekableLittleEndianAccessor accessor) {
      return new ServerStatusRequestPacket((byte) accessor.readShort());
   }
}
