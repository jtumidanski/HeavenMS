package net.server.channel.packet.keymap;

import net.server.MaplePacket;

public class BaseKeymapChangePacket implements MaplePacket {
   private final Boolean available;

   private final Integer mode;

   public BaseKeymapChangePacket(Boolean available, Integer mode) {
      this.available = available;
      this.mode = mode;
   }

   public Boolean available() {
      return available;
   }

   public Integer mode() {
      return mode;
   }
}
