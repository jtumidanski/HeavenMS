package client.database.data;

import client.database.GameType;

public record GameData(GameType theType, Integer wins, Integer losses, Integer ties) {
   public GameData incrementWins() {
      return new GameData(theType, wins + 1, losses, ties);
   }

   public GameData incrementLosses() {
      return new GameData(theType, wins, losses + 1, ties);
   }

   public GameData incrementTies() {
      return new GameData(theType, wins, losses, ties + 1);
   }
}
