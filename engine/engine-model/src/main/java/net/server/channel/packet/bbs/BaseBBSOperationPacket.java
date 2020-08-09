package net.server.channel.packet.bbs;

import net.server.MaplePacket;

public class BaseBBSOperationPacket implements MaplePacket {
   private final Byte mode;

   public BaseBBSOperationPacket(Byte mode) {
      this.mode = mode;
   }

   public Byte mode() {
      return mode;
   }
}
