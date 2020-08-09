package net.server.channel.packet.buddy;

import net.server.MaplePacket;

public class BaseBuddyPacket implements MaplePacket {
   private final Integer mode;

   public BaseBuddyPacket(Integer mode) {
      this.mode = mode;
   }

   public Integer mode() {
      return mode;
   }
}
