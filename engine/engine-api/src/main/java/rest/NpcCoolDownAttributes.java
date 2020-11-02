package rest;

public class NpcCoolDownAttributes implements AttributeResult {
   private Long coolDown;

   public Long getCoolDown() {
      return coolDown;
   }

   public void setCoolDown(Long coolDown) {
      this.coolDown = coolDown;
   }
}
