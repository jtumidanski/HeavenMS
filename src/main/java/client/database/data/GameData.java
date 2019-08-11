package client.database.data;

public class GameData {
   public enum Type {
      OMOK,
      MATCH
   }

   private Type type;

   private int wins;

   private int losses;

   private int ties;

   public GameData(Type type, int wins, int losses, int ties) {
      this.type = type;
      this.wins = wins;
      this.losses = losses;
      this.ties = ties;
   }

   public Type getType() {
      return type;
   }

   public int getWins() {
      return wins;
   }

   public int getLosses() {
      return losses;
   }

   public int getTies() {
      return ties;
   }
}
