package net.server.channel.packet.reader;

import net.server.PacketReader;
import net.server.channel.packet.DistributeSPPacket;
import tools.data.input.SeekableLittleEndianAccessor;

public class DistributeSPReader implements PacketReader<DistributeSPPacket> {
   @Override
   public DistributeSPPacket read(SeekableLittleEndianAccessor accessor) {
      accessor.readInt();
      int skillId = accessor.readInt();
      return new DistributeSPPacket(skillId);
   }
}
