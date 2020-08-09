package net.server.channel.packet.party;

import net.server.MaplePacket;

public class BasePartyOperationPacket implements MaplePacket {
   private final Integer operation;

   public BasePartyOperationPacket(Integer operation) {
      this.operation = operation;
   }

   public Integer operation() {
      return operation;
   }
}
