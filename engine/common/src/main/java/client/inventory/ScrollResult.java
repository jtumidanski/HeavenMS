package client.inventory;

public enum ScrollResult {
   FAIL(0), SUCCESS(1), CURSE(2);

   private int value = -1;

   ScrollResult(int value) {
      this.value = value;
   }

   public int getValue() {
      return value;
   }
}
