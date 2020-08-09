package net.server.channel.packet.maker;

import net.server.MaplePacket;

public class BaseMakerActionPacket implements MaplePacket {
   private final Integer theType;

   private final Integer toCreate;

   public BaseMakerActionPacket(Integer theType, Integer toCreate) {
      this.theType = theType;
      this.toCreate = toCreate;
   }

   public Integer theType() {
      return theType;
   }

   public Integer toCreate() {
      return toCreate;
   }
}
