package net.server.channel.packet.command.admin;

import net.server.MaplePacket;

public class BaseAdminCommandPacket implements MaplePacket {
   private final byte mode;

   public BaseAdminCommandPacket(byte mode) {
      this.mode = mode;
   }

   public byte mode() {
      return mode;
   }
}
