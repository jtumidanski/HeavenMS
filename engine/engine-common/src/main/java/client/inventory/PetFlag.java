package client.inventory;

public enum PetFlag {
   OWNER_SPEED(0x01);

   private int i;

   PetFlag(int i) {
      this.i = i;
   }

   public int getValue() {
      return i;
   }
}
