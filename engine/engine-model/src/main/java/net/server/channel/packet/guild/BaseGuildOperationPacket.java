package net.server.channel.packet.guild;

import net.server.MaplePacket;

public class BaseGuildOperationPacket implements MaplePacket {
   private final Byte theType;

   public BaseGuildOperationPacket(Byte theType) {
      this.theType = theType;
   }

   public Byte theType() {
      return theType;
   }
}
