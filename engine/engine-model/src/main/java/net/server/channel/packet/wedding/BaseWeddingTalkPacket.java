package net.server.channel.packet.wedding;

import net.server.MaplePacket;

public class BaseWeddingTalkPacket implements MaplePacket {
   private final Byte action;

   public BaseWeddingTalkPacket(Byte action) {
      this.action = action;
   }

   public Byte action() {
      return action;
   }
}
