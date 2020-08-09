package net.server.channel.packet.cash.use;

public class UseItemMegaphonePacket extends AbstractUseCashItemPacket {
   private final String message;

   private final Boolean whisper;

   private final Boolean selected;

   private final Byte inventoryType;

   private final Short slot;

   public UseItemMegaphonePacket(Short position, Integer itemId, String message, Boolean whisper, Boolean selected, Byte inventoryType, Short slot) {
      super(position, itemId);
      this.message = message;
      this.whisper = whisper;
      this.selected = selected;
      this.inventoryType = inventoryType;
      this.slot = slot;
   }

   public String message() {
      return message;
   }

   public Boolean whisper() {
      return whisper;
   }

   public Boolean selected() {
      return selected;
   }

   public Byte inventoryType() {
      return inventoryType;
   }

   public Short slot() {
      return slot;
   }
}
