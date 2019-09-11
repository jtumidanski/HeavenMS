package net.server.channel.packet.reader;

import net.server.PacketReader;
import net.server.channel.packet.AutoAggroPacket;
import tools.data.input.SeekableLittleEndianAccessor;

public class AutoAggroReader implements PacketReader<AutoAggroPacket> {
   @Override
   public AutoAggroPacket read(SeekableLittleEndianAccessor accessor) {
      int oid = accessor.readInt();
      return new AutoAggroPacket(oid);
   }
}
