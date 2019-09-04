package client.database.data;

public class PlayerNpcFieldData {
   private int worldId;

   private int mapId;

   private int step;

   private int podium;

   public PlayerNpcFieldData(int worldId, int mapId, int step, int podium) {
      this.worldId = worldId;
      this.mapId = mapId;
      this.step = step;
      this.podium = podium;
   }

   public int getWorldId() {
      return worldId;
   }

   public int getMapId() {
      return mapId;
   }

   public int getStep() {
      return step;
   }

   public int getPodium() {
      return podium;
   }
}
