package net.server.channel.packet.interaction;

import net.server.MaplePacket;

public class BasePlayerInteractionPacket implements MaplePacket {
   private final Byte mode;

   public BasePlayerInteractionPacket(Byte mode) {
      this.mode = mode;
   }

   public Byte mode() {
      return mode;
   }
}
