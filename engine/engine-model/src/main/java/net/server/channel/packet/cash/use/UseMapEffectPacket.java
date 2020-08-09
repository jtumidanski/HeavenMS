package net.server.channel.packet.cash.use;

public class UseMapEffectPacket extends AbstractUseCashItemPacket {
   private final String message;

   public UseMapEffectPacket(Short position, Integer itemId, String message) {
      super(position, itemId);
      this.message = message;
   }

   public String message() {
      return message;
   }
}
