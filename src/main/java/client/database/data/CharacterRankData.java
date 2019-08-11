package client.database.data;

public class CharacterRankData {
   private long lastLogin;

   private int loggedIn;

   private int move;

   private int rank;

   private int characterId;

   public CharacterRankData(long lastLogin, int loggedIn, int move, int rank, int characterId) {
      this.lastLogin = lastLogin;
      this.loggedIn = loggedIn;
      this.move = move;
      this.rank = rank;
      this.characterId = characterId;
   }

   public long getLastLogin() {
      return lastLogin;
   }

   public int getLoggedIn() {
      return loggedIn;
   }

   public int getMove() {
      return move;
   }

   public int getRank() {
      return rank;
   }

   public int getCharacterId() {
      return characterId;
   }
}
