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
      return switch (type) {
         case NPC, PLAYER, HIRED_MERCHANT, PLAYER_NPC, DRAGON, MIST, KITE -> true;
         default -> false;
      };
   }
}
