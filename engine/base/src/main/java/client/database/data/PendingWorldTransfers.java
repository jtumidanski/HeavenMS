package client.database.data;

public class PendingWorldTransfers {
   private int id;

   private int characterId;

   private int from;

   private int to;

   public PendingWorldTransfers(int id, int characterId, int from, int to) {
      this.id = id;
      this.characterId = characterId;
      this.from = from;
      this.to = to;
   }

   public int getId() {
      return id;
   }

   public int getCharacterId() {
      return characterId;
   }

   public int getFrom() {
      return from;
   }

   public int getTo() {
      return to;
   }
}
