package client.database.data;

public class MonsterCardData {
   private int cardId;

   private int mobId;

   public MonsterCardData(int cardId, int mobId) {
      this.cardId = cardId;
      this.mobId = mobId;
   }

   public int getCardId() {
      return cardId;
   }

   public int getMobId() {
      return mobId;
   }
}
