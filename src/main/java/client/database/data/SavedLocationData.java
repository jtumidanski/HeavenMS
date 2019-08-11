package client.database.data;

public class SavedLocationData {
   private String locationType;

   private int mapId;

   private int portalId;

   public SavedLocationData(String locationType, int mapId, int portalId) {
      this.locationType = locationType;
      this.mapId = mapId;
      this.portalId = portalId;
   }

   public String getLocationType() {
      return locationType;
   }

   public int getMapId() {
      return mapId;
   }

   public int getPortalId() {
      return portalId;
   }
}
