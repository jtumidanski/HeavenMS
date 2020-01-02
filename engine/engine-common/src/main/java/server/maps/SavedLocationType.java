package server.maps;

public enum SavedLocationType {
   FREE_MARKET,
   WORLD_TOUR,
   FLORINA,
   INTRO,
   SUNDAY_MARKET,
   MIRROR,
   EVENT,
   BOSS_PQ,
   HAPPYVILLE,
   MONSTER_CARNIVAL,
   DEVELOPER;

   public static SavedLocationType fromString(String Str) {
      return valueOf(Str);
   }
}