package server.processor.maps;

import server.maps.MapleMapObjectType;

public class MapleMapObjectTypeProcessor {
   private static MapleMapObjectTypeProcessor ourInstance = new MapleMapObjectTypeProcessor();

   public static MapleMapObjectTypeProcessor getInstance() {
      return ourInstance;
   }

   private MapleMapObjectTypeProcessor() {
   }

   public boolean isNonRangedType(MapleMapObjectType type) {
      switch (type) {
         case NPC:
         case PLAYER:
         case HIRED_MERCHANT:
         case PLAYER_NPC:
         case DRAGON:
         case MIST:
         case KITE:
            return true;
         default:
            return false;
      }
   }
}
