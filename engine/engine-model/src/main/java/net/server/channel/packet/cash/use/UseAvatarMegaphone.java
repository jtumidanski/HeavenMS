package net.server.channel.packet.cash.use;

public class UseAvatarMegaphone extends AbstractUseCashItemPacket {
   private final String[] messages;

   private final Boolean ear;

   public UseAvatarMegaphone(Short position, Integer itemId, String[] messages, Boolean ear) {
      super(position, itemId);
      this.messages = messages;
      this.ear = ear;
   }

   public String[] messages() {
      return messages;
   }

   public Boolean ear() {
      return ear;
   }
}
