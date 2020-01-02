package server.events.gm;

public class MapleEvent {
   private int mapId;
   private int limit;

   public MapleEvent(int mapId, int limit) {
      this.mapId = mapId;
      this.limit = limit;
   }

   public int getMapId() {
      return mapId;
   }

   public int getLimit() {
      return limit;
   }

   public void minusLimit() {
      this.limit--;
   }

   public void addLimit() {
      this.limit++;
   }
}  