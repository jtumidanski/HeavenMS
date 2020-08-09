package net.server.channel.packet.cash.use;

public class UseSpResetPacket extends AbstractUseCashItemPacket {
   private final Integer to;

   private final Integer from;

   public UseSpResetPacket(Short position, Integer itemId, Integer to, Integer from) {
      super(position, itemId);
      this.to = to;
      this.from = from;
   }

   public Integer to() {
      return to;
   }

   public Integer from() {
      return from;
   }
}
