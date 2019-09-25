package server;

public enum MapleTradeResult {
   NO_RESPONSE(1),
   PARTNER_CANCEL(2),
   SUCCESSFUL(7),
   UNSUCCESSFUL(8),
   UNSUCCESSFUL_UNIQUE_ITEM_LIMIT(9),
   UNSUCCESSFUL_ANOTHER_MAP(12),
   UNSUCCESSFUL_DAMAGED_FILES(13);

   private final int res;

   MapleTradeResult(int res) {
      this.res = res;
   }

   public byte getValue() {
      return (byte) res;
   }
}
