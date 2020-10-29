package rest;

public class CharacterItemAttributes implements AttributeResult {
   private Integer itemId;

   private Short quantity;

   private String owner;

   private Integer petId;

   private Long expiration;

   public Integer getItemId() {
      return itemId;
   }

   public void setItemId(Integer itemId) {
      this.itemId = itemId;
   }

   public Short getQuantity() {
      return quantity;
   }

   public void setQuantity(Short quantity) {
      this.quantity = quantity;
   }

   public String getOwner() {
      return owner;
   }

   public void setOwner(String owner) {
      this.owner = owner;
   }

   public Integer getPetId() {
      return petId;
   }

   public void setPetId(Integer petId) {
      this.petId = petId;
   }

   public Long getExpiration() {
      return expiration;
   }

   public void setExpiration(Long expiration) {
      this.expiration = expiration;
   }
}
