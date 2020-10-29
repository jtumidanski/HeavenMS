package rest;

public class CanHoldItem {
   private Integer itemId;

   private Integer quantity;

   public CanHoldItem() {
   }

   public CanHoldItem(Integer itemId, Integer quantity) {
      this.itemId = itemId;
      this.quantity = quantity;
   }

   public Integer getItemId() {
      return itemId;
   }

   public void setItemId(Integer itemId) {
      this.itemId = itemId;
   }

   public Integer getQuantity() {
      return quantity;
   }

   public void setQuantity(Integer quantity) {
      this.quantity = quantity;
   }
}
