package net.server.channel.packet.reader;

import net.server.PacketReader;
import net.server.channel.packet.TouchReactorPacket;
import tools.data.input.SeekableLittleEndianAccessor;

public class TouchReactorReader implements PacketReader<TouchReactorPacket> {
   @Override
   public TouchReactorPacket read(SeekableLittleEndianAccessor accessor) {
      int oid = accessor.readInt();
      boolean isTouching = accessor.readByte() != 0;
      return new TouchReactorPacket(oid, isTouching);
   }
}
