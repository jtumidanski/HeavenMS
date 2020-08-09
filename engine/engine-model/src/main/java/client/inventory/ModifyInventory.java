package client.inventory;

public record ModifyInventory(int mode, Item item, short oldPos) {
   public ModifyInventory(int mode, Item item) {
      this(mode, item, (short) 0);
   }

   public ModifyInventory clear() {
      return new ModifyInventory(mode, null, oldPos);
   }

   public short position() {
      return item.position();
   }

   public int inventoryType() {
      return item.inventoryType().getType();
   }

   public short quantity() {
      return item.quantity();
   }
}
