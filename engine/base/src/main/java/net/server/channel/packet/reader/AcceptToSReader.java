package net.server.channel.packet.reader;

import net.server.PacketReader;
import net.server.login.packet.AcceptToSPacket;
import tools.data.input.SeekableLittleEndianAccessor;

public class AcceptToSReader implements PacketReader<AcceptToSPacket> {
   @Override
   public AcceptToSPacket read(SeekableLittleEndianAccessor accessor) {
      if (accessor.available() > 0) {
         return new AcceptToSPacket(accessor.read((int) accessor.available()));
      }
      return new AcceptToSPacket(new byte[0]);
   }
}
