package client.database.data;

public class MonsterBookData {
   private int cardId;

   private int level;

   public MonsterBookData(int cardId, int level) {
      this.cardId = cardId;
      this.level = level;
   }

   public int getCardId() {
      return cardId;
   }

   public int getLevel() {
      return level;
   }
}
