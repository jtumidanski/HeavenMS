package tools.packet.cashshop;

public enum CashShopMessage {
   UNKNOWN_ERROR(0x00), //Due to an unknown error, failed
   REQUEST_TIMED_OUT(0xA3), //Request timed out. Please try again.
   UNKNOWN_ERROR_AND_WARP(0xA4), //Due to an unknown error, failed + warpout
   NOT_ENOUGH_CASH(0xA5), //You don't have enough cash.
   LONG_MESSAGE(0xA6), //long as shet msg
   EXCEEDED_ALLOTTED_GIFT_PRICE_LIMIT(0xA7), //You have exceeded the allotted limit of price for gifts.
   CANNOT_GIFT_TO_OWN_CHARACTER(0xA8), //You cannot send a gift to your own account. Log in on the char and purchase
   INCORRECT_CHARACTER_NAME(0xA9), // Please confirm whether the character's name is correct.
   GENDER_RESTRICTION(0xAA), // Gender restriction!
   RECIPIENT_INVENTORY_FULL(0xAB), // gift cannot be sent because recipient inv is full
   MAX_CASH_ITEM_LIMIT(0xAC), // exceeded the number of cash items you can have
   INCORRECT_CHARACTER_NAME_OR_GENDER_RESTRICTION(0xAD), // check and see if the character name is wrong or there is gender restrictions
   WRONG_COUPON_CODE(0xB0), // Wrong Coupon Code
   WRONG_COUPON_CODE_X3(0xB1), // Disconnect from CS because of 3 wrong coupon codes < lol
   EXPIRED_COUPON(0xB2), // Expired Coupon
   COUPON_ALREADY_USED(0xB3), // Coupon has been used already
   INTERNET_CAFE(0xB4), // Nexon internet cafes? lolfk
   CANNOT_USE_COUPON_DUE_TO_GENDER(0xB8), // Due to gender restrictions, the coupon cannot be used.
   INVENTORY_FULL(0xBB), // inv full
   LONG_MESSAGE_2(0xBC), // long as shet "(not?) available to purchase by a use at the premium" msg
   INVALID_GIFT_RECIPIENT(0xBD), // invalid gift recipient
   INVALID_RECEIVER_NAME(0xBE), // invalid receiver name
   UNAVAILABLE_TO_PURCHASE_AT_THIS_HOUR(0xBF), // item unavailable to purchase at this hour
   NOT_ENOUGH_ITEMS_IN_STOCK(0xC0), // not enough items in stock, therefore not available
   EXCEEDED_NX_SPENDING_LIMIT(0xC1), // you have exceeded spending limit of NX
   NOT_ENOUGH_MESOS(0xC2), // not enough mesos? Lol not even 1 mesos xD
   CASH_SHOP_NOT_AVAILABLE_IN_BETA(0xC3), // cash shop unavailable during beta phase
   CHECK_BIRTHDAY_CODE(0xC4), // check birthday code
   LONG_MESSAGE_3(0xC7), // only available to users buying cash item, whatever msg too long
   ALREADY_APPLIED_FOR_THIS(0xC8), // already applied for this
   DAILY_PURCHASE_LIMIT(0xCD), // You have reached the daily purchase limit for the cash shop.
   COUPON_ACCOUNT_LIMIT(0xD0), // coupon account limit reached
   COUPON_SYSTEM_NOT_AVAILABLE(0xD2), // coupon system currently unavailable
   ITEM_CAN_ONLY_BE_USED_AFTER_15_DAYS(0xD3), // item can only be used 15 days after registration
   NOT_ENOUGH_GIFT_TOKENS(0xD4), // not enough gift tokens
   NEW_PEOPLE_CANNOT_GIFT(0xD6), // fresh people cannot gift items lul
   BAD_PEOPLE_CANNOT_GIFT(0xD7), // bad people cannot gift items >:(
   CANNNOT_GIFT(0xD8), // cannot gift due to limitations
   REACHED_GIFT_LIMIT(0xD9), // cannot gift due to amount of gifted times
   CANNOT_GIFT_DUE_TO_TECHNICAL_DIFFICULTIES(0xDA), // cannot be gifted due to technical difficulties
   CANNOT_TRANSFER_TO_BELOW_LEVEL_20(0xDB), // cannot transfer to char below level 20
   CANNOT_TRANSFER_TO_SAME_WORLD(0xDC), // cannot transfer char to same world
   CANNOT_TRANSFER_TO_NEW_WORLD(0xDD), // cannot transfer char to new server world
   CANNOT_TRANSFER_OUT_OF_THIS_WORLD(0xDE), // cannot transfer char out of this world
   CANNOT_TRANSFER_NO_EMPTY_SLOTS(0xDF), // cannot transfer char due to no empty char slots
   EVENT_ENDED(0xE0), // event or free test time ended
   CANNOT_BE_PURCHASED_WITH_MAPLE_POINTS(0xE6), // item cannot be purchased with MaplePoints
   SORRY_FOR_INCONVENIENCE(0xE7), // lol sorry for the inconvenience, eh?
   MUST_BE_OLDER_THAN_7(0xE8); // cannot be purchased by anyone under 7

   private final byte value;

   CashShopMessage(int value) {
      this.value = (byte) value;
   }

   public byte getValue() {
      return value;
   }

   public static CashShopMessage fromValue(int value) {
      for (CashShopMessage op : CashShopMessage.values()) {
         if (op.getValue() == (byte) value) {
            return op;
         }
      }
      return null;
   }
}
