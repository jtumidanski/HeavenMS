package net.server.channel.packet.reader;

import net.server.PacketReader;
import net.server.channel.packet.RaiseUIStatePacket;
import tools.data.input.SeekableLittleEndianAccessor;

public class RaiseUIStateReader implements PacketReader<RaiseUIStatePacket> {
   @Override
   public RaiseUIStatePacket read(SeekableLittleEndianAccessor accessor) {
      int questid = accessor.readShort();
      return new RaiseUIStatePacket(questid);
   }
}
