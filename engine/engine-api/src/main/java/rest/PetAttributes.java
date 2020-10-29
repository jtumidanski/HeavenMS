package rest;

public class PetAttributes implements AttributeResult {
   private Short slot;

   private Short flag;

   private Integer petFlag;

   private Integer closeness;

   private Integer fullness;

   public Short getSlot() {
      return slot;
   }

   public void setSlot(Short slot) {
      this.slot = slot;
   }

   public Short getFlag() {
      return flag;
   }

   public void setFlag(Short flag) {
      this.flag = flag;
   }

   public Integer getPetFlag() {
      return petFlag;
   }

   public void setPetFlag(Integer petFlag) {
      this.petFlag = petFlag;
   }

   public Integer getCloseness() {
      return closeness;
   }

   public void setCloseness(Integer closeness) {
      this.closeness = closeness;
   }

   public Integer getFullness() {
      return fullness;
   }

   public void setFullness(Integer fullness) {
      this.fullness = fullness;
   }
}
