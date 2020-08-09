package net.server.channel.packet.wedding;

import net.server.MaplePacket;

public class BaseWeddingPacket implements MaplePacket {
   private final Byte mode;

   public BaseWeddingPacket(Byte mode) {
      this.mode = mode;
   }

   public Byte mode() {
      return mode;
   }
}
