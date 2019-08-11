package client.database.data;

public class MakerRecipeData {
   private int requiredItem;

   private int count;

   public MakerRecipeData(int requiredItem, int count) {
      this.requiredItem = requiredItem;
      this.count = count;
   }

   public int getRequiredItem() {
      return requiredItem;
   }

   public int getCount() {
      return count;
   }
}
