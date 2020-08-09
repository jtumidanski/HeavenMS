package net.server.channel.packet.cash.use;

public class UseTripleMegaphonePacket extends AbstractUseCashItemPacket {
   private final Integer lines;

   private final String[] message;

   private final Boolean whisper;

   public UseTripleMegaphonePacket(Short position, Integer itemId, Integer lines, String[] message, Boolean whisper) {
      super(position, itemId);
      this.lines = lines;
      this.message = message;
      this.whisper = whisper;
   }

   public Integer lines() {
      return lines;
   }

   public String[] message() {
      return message;
   }

   public Boolean whisper() {
      return whisper;
   }
}
