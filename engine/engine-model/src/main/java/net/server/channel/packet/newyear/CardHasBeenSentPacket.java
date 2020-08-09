package net.server.channel.packet.newyear;

public class CardHasBeenSentPacket extends BaseNewYearCardPacket {
   private final Short slot;

   private final Integer itemId;

   private final String receiver;

   private final String message;

   public CardHasBeenSentPacket(Byte reqMode, Short slot, Integer itemId, String receiver, String message) {
      super(reqMode);
      this.slot = slot;
      this.itemId = itemId;
      this.receiver = receiver;
      this.message = message;
   }

   public Short slot() {
      return slot;
   }

   public Integer itemId() {
      return itemId;
   }

   public String receiver() {
      return receiver;
   }

   public String message() {
      return message;
   }
}
