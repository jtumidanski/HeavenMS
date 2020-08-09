package net.server.channel.packet.rps;

import net.server.MaplePacket;

public class BaseRPSActionPacket implements MaplePacket {
   private final Boolean available;

   private final Byte mode;

   public BaseRPSActionPacket(Boolean available, Byte mode) {
      this.available = available;
      this.mode = mode;
   }

   public Boolean available() {
      return available;
   }

   public Byte mode() {
      return mode;
   }
}
