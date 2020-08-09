package net.server.channel.packet.duey;

import net.server.MaplePacket;

public class BaseDueyPacket implements MaplePacket {
   private final Byte operation;

   public BaseDueyPacket(Byte operation) {
      this.operation = operation;
   }

   public Byte operation() {
      return operation;
   }
}
