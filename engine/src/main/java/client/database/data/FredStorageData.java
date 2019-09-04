package client.database.data;

import java.sql.Timestamp;

public class FredStorageData {
   private int characterId;

   private String name;

   private int worldId;

   private Timestamp timestamp;

   private int dayNotes;

   private Timestamp lastLogoutTime;

   public FredStorageData(int characterId, String name, int worldId, Timestamp timestamp, int dayNotes, Timestamp lastLogoutTime) {
      this.characterId = characterId;
      this.name = name;
      this.worldId = worldId;
      this.timestamp = timestamp;
      this.dayNotes = dayNotes;
      this.lastLogoutTime = lastLogoutTime;
   }

   public int getCharacterId() {
      return characterId;
   }

   public String getName() {
      return name;
   }

   public int getWorldId() {
      return worldId;
   }

   public Timestamp getTimestamp() {
      return timestamp;
   }

   public int getDayNotes() {
      return dayNotes;
   }

   public Timestamp getLastLogoutTime() {
      return lastLogoutTime;
   }
}
