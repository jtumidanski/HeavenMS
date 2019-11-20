package constants;

/* 00 = /
 * 01 = You don't have enough in stock
 * 02 = You do not have enough mesos
 * 03 = Please check if your inventory is full or not
 * 05 = You don't have enough in stock
 * 06 = Due to an error, the trade did not happen
 * 07 = Due to an error, the trade did not happen
 * 08 = /
 * 0D = You need more items
 * 0E = CRASH; LENGTH NEEDS TO BE LONGER :O
 */
public enum ShopTransactionOperation {
   DEFAULT((byte) 0x00),
   NOT_ENOUGH_IN_STOCK((byte) 0x01),
   NOT_ENOUGH_MESO((byte) 0x02),
   INVENTORY_FULL((byte) 0x03),
   NOT_ENOUGH_IN_STOCK_2((byte) 0x05),
   TRADE_ERROR((byte) 0x06),
   TRADE_ERROR_2((byte) 0x07),
   DEFAULT_2((byte) 0x08),
   NEED_MORE_ITEMS((byte) 0x0D),
   CRASH((byte) 0x0E);

   private final byte value;

   ShopTransactionOperation(byte value) {
      this.value = value;
   }

   public byte getValue() {
      return value;
   }
}
