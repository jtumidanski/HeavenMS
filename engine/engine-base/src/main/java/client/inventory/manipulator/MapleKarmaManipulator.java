package client.inventory.manipulator;

import client.inventory.Item;
import client.processor.ItemProcessor;
import constants.inventory.ItemConstants;

public class MapleKarmaManipulator {
   private static short getKarmaFlag(Item item) {
      return item.itemType() == 1 ? ItemConstants.KARMA_EQP : ItemConstants.KARMA_USE;
   }

   public static boolean hasKarmaFlag(Item item) {
      short karmaFlag = getKarmaFlag(item);
      return (item.flag() & karmaFlag) == karmaFlag;
   }

   public static Item toggleKarmaFlagToUntradeable(Item item) {
      short karmaFlag = getKarmaFlag(item);
      short flag = item.flag();

      if ((flag & karmaFlag) == karmaFlag) {
         flag ^= karmaFlag;
         flag |= ItemConstants.UNTRADEABLE;
         return Item.newBuilder(item).setFlag(ItemProcessor.getInstance().setFlag(item.id(), (byte) flag)).build();
      }
      return item;
   }

   public static Item setKarmaFlag(Item item) {
      short karmaFlag = getKarmaFlag(item);
      short flag = item.flag();

      flag |= karmaFlag;
      flag &= (~ItemConstants.UNTRADEABLE);

      return Item.newBuilder(item).setFlag(ItemProcessor.getInstance().setFlag(item.id(), (byte) flag)).build();
   }
}
