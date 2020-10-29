package rest;

public class ItemAttributes implements AttributeResult {
   private String name;

   private Boolean questItem;

   private Boolean pickupRestricted;

   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public Boolean getQuestItem() {
      return questItem;
   }

   public void setQuestItem(Boolean questItem) {
      this.questItem = questItem;
   }

   public Boolean getPickupRestricted() {
      return pickupRestricted;
   }

   public void setPickupRestricted(Boolean pickupRestricted) {
      this.pickupRestricted = pickupRestricted;
   }
}
