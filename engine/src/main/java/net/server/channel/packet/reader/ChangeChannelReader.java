package net.server.channel.packet.reader;

import net.server.PacketReader;
import net.server.channel.packet.ChangeChannelPacket;
import tools.data.input.SeekableLittleEndianAccessor;

public class ChangeChannelReader implements PacketReader<ChangeChannelPacket> {
   @Override
   public ChangeChannelPacket read(SeekableLittleEndianAccessor accessor) {
      int channel = accessor.readByte() + 1;
      accessor.readInt();
      return new ChangeChannelPacket(channel);
   }
}
