package client.inventory;

/**
 * @author kevin
 */
public class ModifyInventory {

   private int mode;
   private Item item;
   private short oldPos;

   public ModifyInventory(final int mode, final Item item) {
      this.mode = mode;
      this.item = item.copy();
   }

   public ModifyInventory(final int mode, final Item item, final short oldPos) {
      this.mode = mode;
      this.item = item.copy();
      this.oldPos = oldPos;
   }

   public final int getMode() {
      return mode;
   }

   public final int getInventoryType() {
      return item.inventoryType().type;
   }

   public final short getPosition() {
      return item.position();
   }

   public final short getOldPosition() {
      return oldPos;
   }

   public final short getQuantity() {
      return item.quantity();
   }

   public final Item getItem() {
      return item;
   }

   public final void clear() {
      this.item = null;
   }
}