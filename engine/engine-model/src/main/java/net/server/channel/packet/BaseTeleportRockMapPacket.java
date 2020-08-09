package net.server.channel.packet;

import net.server.MaplePacket;

public class BaseTeleportRockMapPacket implements MaplePacket {
   private final Byte theType;

   private final Boolean vip;

   public BaseTeleportRockMapPacket(Byte theType, Boolean vip) {
      this.theType = theType;
      this.vip = vip;
   }

   public Byte theType() {
      return theType;
   }

   public Boolean vip() {
      return vip;
   }
}
