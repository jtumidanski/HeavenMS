package tools.packet.cashshop;

public enum CashShopGachaponSubOp {
   FAILURE(0xE4),
   SUCCESS(0xE5);

   private final byte value;

   CashShopGachaponSubOp(int value) {
      this.value = (byte) value;
   }

   public byte getValue() {
      return value;
   }

   public static CashShopGachaponSubOp fromValue(byte value) {
      for (CashShopGachaponSubOp op : CashShopGachaponSubOp.values()) {
         if (op.getValue() == value) {
            return op;
         }
      }
      return null;
   }
}
