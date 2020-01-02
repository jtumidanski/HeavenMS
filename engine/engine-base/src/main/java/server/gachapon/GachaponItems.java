package server.gachapon;

public abstract class GachaponItems {
   private final int[] commonItems;
   private final int[] uncommonItems;
   private final int[] rareItems;

   public GachaponItems() {
      this.commonItems = getCommonItems();
      this.uncommonItems = getUncommonItems();
      this.rareItems = getRareItems();
   }

   public abstract int[] getCommonItems();

   public abstract int[] getUncommonItems();

   public abstract int[] getRareItems();

   public final int[] getItems(int tier) {
      if (tier == 0) {
         return commonItems;
      } else if (tier == 1) {
         return uncommonItems;
      } else if (tier == 2) {
         return rareItems;
      }
      return null;
   }
}

