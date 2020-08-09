package net.server.channel.packet.cash.use;

public class UseSuperMegaphonePacket extends AbstractUseCashItemPacket {
   private final String message;

   private final Boolean ear;

   public UseSuperMegaphonePacket(Short position, Integer itemId, String message, Boolean ear) {
      super(position, itemId);
      this.message = message;
      this.ear = ear;
   }

   public String message() {
      return message;
   }

   public Boolean ear() {
      return ear;
   }
}
