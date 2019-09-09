package server.processor.maps;

import client.MapleCharacter;
import server.maps.MapleMapObject;
import server.maps.MapleMapObjectType;

public class MapleMapObjectProcessor {
   private static MapleMapObjectProcessor ourInstance = new MapleMapObjectProcessor();

   public static MapleMapObjectProcessor getInstance() {
      return ourInstance;
   }

   private MapleMapObjectProcessor() {
   }

   public void updateMapObjectVisibility(MapleCharacter chr, MapleMapObject mo) {
      double rangedDistance = MapleMapProcessor.getInstance().getRangedDistance();
      if (!chr.isMapObjectVisible(mo)) { // object entered view range
         if (mo.getType() == MapleMapObjectType.SUMMON || mo.getPosition().distanceSq(chr.getPosition()) <= rangedDistance) {
            chr.addVisibleMapObject(mo);
            mo.sendSpawnData(chr.getClient());
         }
      } else if (mo.getType() != MapleMapObjectType.SUMMON && mo.getPosition().distanceSq(chr.getPosition()) > rangedDistance) {
         chr.removeVisibleMapObject(mo);
         mo.sendDestroyData(chr.getClient());
      }
   }
}
