package net.server.channel.packet.reader;

import net.server.PacketReader;
import net.server.channel.packet.CouponCodePacket;
import tools.data.input.SeekableLittleEndianAccessor;

public class CouponCodeReader implements PacketReader<CouponCodePacket> {
   @Override
   public CouponCodePacket read(SeekableLittleEndianAccessor accessor) {
      accessor.skip(2);
      String code = accessor.readMapleAsciiString();
      return new CouponCodePacket(code);
   }
}
