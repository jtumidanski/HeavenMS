package client.database.data;

public class MakerCreateData {
   private int requiredLevel;

   private int requiredMakerLevel;

   private int requiredMeso;

   private int quantity;

   public MakerCreateData(int requiredLevel, int requiredMakerLevel, int requiredMeso, int quantity) {
      this.requiredLevel = requiredLevel;
      this.requiredMakerLevel = requiredMakerLevel;
      this.requiredMeso = requiredMeso;
      this.quantity = quantity;
   }

   public int getRequiredLevel() {
      return requiredLevel;
   }

   public int getRequiredMakerLevel() {
      return requiredMakerLevel;
   }

   public int getRequiredMeso() {
      return requiredMeso;
   }

   public int getQuantity() {
      return quantity;
   }
}
