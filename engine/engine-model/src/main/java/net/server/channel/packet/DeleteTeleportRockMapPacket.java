package net.server.channel.packet;

import net.server.MaplePacket;

public class DeleteTeleportRockMapPacket extends BaseTeleportRockMapPacket implements MaplePacket {
   private final Integer mapId;

   public DeleteTeleportRockMapPacket(Byte theType, Boolean vip, Integer mapId) {
      super(theType, vip);
      this.mapId = mapId;
   }

   public Integer mapId() {
      return mapId;
   }
}
