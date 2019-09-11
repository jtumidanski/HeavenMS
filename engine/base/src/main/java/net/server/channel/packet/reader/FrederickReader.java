package net.server.channel.packet.reader;

import net.server.PacketReader;
import net.server.channel.packet.fredrick.BaseFrederickPacket;
import tools.data.input.SeekableLittleEndianAccessor;

public class FrederickReader implements PacketReader<BaseFrederickPacket> {
   @Override
   public BaseFrederickPacket read(SeekableLittleEndianAccessor accessor) {
      byte operation = accessor.readByte();
      return new BaseFrederickPacket(operation);
   }
}
