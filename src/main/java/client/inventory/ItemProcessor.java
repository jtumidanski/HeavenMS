package client.inventory;

import constants.ItemConstants;

public class ItemProcessor {
   private static ItemProcessor ourInstance = new ItemProcessor();

   public static ItemProcessor getInstance() {
      return ourInstance;
   }

   private ItemProcessor() {
   }

   public boolean hasMergeFlag(Item item) {
      return (item.getFlag() & ItemConstants.MERGE_UNTRADEABLE) == ItemConstants.MERGE_UNTRADEABLE;
   }

   public void setMergeFlag(Item item) {
      short flag = item.getFlag();
      flag |= ItemConstants.MERGE_UNTRADEABLE;
      flag |= ItemConstants.UNTRADEABLE;
      item.setFlag(flag);
   }
}
