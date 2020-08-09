package net.server.channel.packet.cash.use;

public class UseNotePacket extends AbstractUseCashItemPacket {
   private final String to;

   private final String message;

   public UseNotePacket(Short position, Integer itemId, String to, String message) {
      super(position, itemId);
      this.to = to;
      this.message = message;
   }

   public String to() {
      return to;
   }

   public String message() {
      return message;
   }
}
