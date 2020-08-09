package net.server.channel.packet.wedding;

public class AddRegistryItemPacket extends BaseWeddingPacket {
   private final Short slot;

   private final Integer itemId;

   private final Short quantity;

   public AddRegistryItemPacket(Byte mode, Short slot, Integer itemId, Short quantity) {
      super(mode);
      this.slot = slot;
      this.itemId = itemId;
      this.quantity = quantity;
   }

   public Short slot() {
      return slot;
   }

   public Integer itemId() {
      return itemId;
   }

   public Short quantity() {
      return quantity;
   }
}
