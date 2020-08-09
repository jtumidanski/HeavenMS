package net.server.channel.packet.newyear;

import net.server.MaplePacket;

public class BaseNewYearCardPacket implements MaplePacket {
   private final Byte reqMode;

   public BaseNewYearCardPacket(Byte reqMode) {
      this.reqMode = reqMode;
   }

   public Byte reqMode() {
      return reqMode;
   }
}
