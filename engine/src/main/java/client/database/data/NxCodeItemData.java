package client.database.data;

public class NxCodeItemData {
   private int type;

   private int quantity;

   private int itemId;

   public NxCodeItemData(int type, int quantity, int itemId) {
      this.type = type;
      this.quantity = quantity;
      this.itemId = itemId;
   }

   public int getType() {
      return type;
   }

   public int getQuantity() {
      return quantity;
   }

   public int getItemId() {
      return itemId;
   }
}
