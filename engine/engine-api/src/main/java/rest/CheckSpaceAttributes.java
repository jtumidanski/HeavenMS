package rest;

public class CheckSpaceAttributes implements AttributeResult {
   private Integer itemId;

   private Integer quantity;

   private String owner;

   private Integer usedSlots;

   private Boolean useProofInventory;

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

   public String getOwner() {
      return owner;
   }

   public void setOwner(String owner) {
      this.owner = owner;
   }

   public Integer getUsedSlots() {
      return usedSlots;
   }

   public void setUsedSlots(Integer usedSlots) {
      this.usedSlots = usedSlots;
   }

   public Boolean getUseProofInventory() {
      return useProofInventory;
   }

   public void setUseProofInventory(Boolean useProofInventory) {
      this.useProofInventory = useProofInventory;
   }
}
