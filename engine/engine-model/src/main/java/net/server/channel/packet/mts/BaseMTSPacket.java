package net.server.channel.packet.mts;

import net.server.MaplePacket;

public class BaseMTSPacket implements MaplePacket {
   private final Boolean available;

   private final Byte operation;

   public BaseMTSPacket(Boolean available, Byte operation) {
      this.available = available;
      this.operation = operation;
   }

   public Boolean available() {
      return available;
   }

   public Byte operation() {
      return operation;
   }
}
