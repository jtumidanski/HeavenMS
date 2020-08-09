package net.server.channel.packet.mts;

public class PlaceItemForSalePacket extends BaseMTSPacket {
   private final Integer itemId;

   private final Short quantity;

   private final Integer price;

   private final Short slot;

   public PlaceItemForSalePacket(Boolean available, Byte operation, Integer itemId, Short quantity, Integer price, Short slot) {
      super(available, operation);
      this.itemId = itemId;
      this.quantity = quantity;
      this.price = price;
      this.slot = slot;
   }

   public Integer itemId() {
      return itemId;
   }

   public Short quantity() {
      return quantity;
   }

   public Integer price() {
      return price;
   }

   public Short slot() {
      return slot;
   }
}
