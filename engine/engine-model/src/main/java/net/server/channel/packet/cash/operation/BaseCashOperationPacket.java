package net.server.channel.packet.cash.operation;

import net.server.MaplePacket;

public class BaseCashOperationPacket implements MaplePacket {
   private final Integer action;

   public BaseCashOperationPacket(Integer action) {
      this.action = action;
   }

   public Integer action() {
      return action;
   }
}
