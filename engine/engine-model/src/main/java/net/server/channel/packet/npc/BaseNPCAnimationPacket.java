package net.server.channel.packet.npc;

import net.server.MaplePacket;

public class BaseNPCAnimationPacket implements MaplePacket {
   private final Integer available;

   public BaseNPCAnimationPacket(Integer available) {
      this.available = available;
   }

   public Integer available() {
      return available;
   }
}
