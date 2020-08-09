package net.server.channel.packet.cash.use;

import net.server.MaplePacket;

public abstract class AbstractUseCashItemPacket implements MaplePacket {
   private final Short position;

   private final Integer itemId;

   public AbstractUseCashItemPacket(Short position, Integer itemId) {
      this.position = position;
      this.itemId = itemId;
   }

   public Short position() {
      return position;
   }

   public Integer itemId() {
      return itemId;
   }
}
