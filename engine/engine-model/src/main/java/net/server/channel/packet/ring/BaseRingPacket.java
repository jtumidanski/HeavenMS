package net.server.channel.packet.ring;

import net.server.MaplePacket;

public class BaseRingPacket implements MaplePacket {
   private final Byte mode;

   public BaseRingPacket(Byte mode) {
      this.mode = mode;
   }

   public Byte mode() {
      return mode;
   }
}
