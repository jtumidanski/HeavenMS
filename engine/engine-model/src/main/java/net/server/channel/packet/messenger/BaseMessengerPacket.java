package net.server.channel.packet.messenger;

import net.server.MaplePacket;

public class BaseMessengerPacket implements MaplePacket {
   private final Byte mode;

   public BaseMessengerPacket(Byte mode) {
      this.mode = mode;
   }

   public Byte mode() {
      return mode;
   }
}
