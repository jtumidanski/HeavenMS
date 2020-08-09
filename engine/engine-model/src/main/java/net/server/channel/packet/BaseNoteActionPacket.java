package net.server.channel.packet;

import net.server.MaplePacket;

public class BaseNoteActionPacket implements MaplePacket {
   private final Integer action;

   public BaseNoteActionPacket(Integer action) {
      this.action = action;
   }

   public Integer action() {
      return action;
   }
}
