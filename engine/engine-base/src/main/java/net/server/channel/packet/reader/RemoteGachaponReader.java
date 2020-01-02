package net.server.channel.packet.reader;

import net.server.PacketReader;
import net.server.channel.packet.RemoteGachaponPacket;
import tools.data.input.SeekableLittleEndianAccessor;

public class RemoteGachaponReader implements PacketReader<RemoteGachaponPacket> {
   @Override
   public RemoteGachaponPacket read(SeekableLittleEndianAccessor accessor) {
      int ticket = accessor.readInt();
      int gachapon = accessor.readInt();
      return new RemoteGachaponPacket(ticket, gachapon);
   }
}
