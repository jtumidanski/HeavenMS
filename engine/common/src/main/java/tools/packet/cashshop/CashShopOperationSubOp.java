package tools.packet.cashshop;

public enum CashShopOperationSubOp {
   WORLD_TRANSFER_SUCCESS(0xA0),
   NAME_CHANGE_SUCCESS(0x9E),
   COUPON_REDEEMED_SUCCESS(0x59),
   BOUGHT_CASH_PACKAGE_SUCCESS(0x89),
   BOUGHT_QUEST_ITEM_SUCCESS(0x8D),
   SHOW_WISHLIST_UPDATE(0x55),
   SHOW_WISHLIST(0x4F),
   BOUGHT_CASH_ITEM_SUCCESS(0x57),
   BOUGHT_CASH_RING_SUCCESS(0x87),
   CASH_SHOP_MESSAGE(0x5C),
   CASH_INVENTORY(0x4B),
   GIFTS(0x4D),
   GIFT_SUCCEED(0x5E),
   BOUGHT_INVENTORY_SLOTS(0x60),
   BOUGHT_STORAGE_SLOTS(0x62),
   BOUGHT_CHARACTER_SLOTS(0x64),
   TAKE_FROM_CASH_INVENTORY(0x68),
   DELETE_CASH_ITEM(0x6C),
   REFUND_CASH_ITEM(0x85),
   PUT_INTO_CASH_INVENTORY(0x6A);



   private final byte value;

   CashShopOperationSubOp(int value) {
      this.value = (byte) value;
   }

   public byte getValue() {
      return value;
   }
}
