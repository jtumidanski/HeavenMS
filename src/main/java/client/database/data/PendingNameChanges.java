package client.database.data;

public class PendingNameChanges {
   private int id;

   private int characterId;

   private String oldName;

   private String newName;

   public PendingNameChanges(int id, int characterId, String oldName, String newName) {
      this.id = id;
      this.characterId = characterId;
      this.oldName = oldName;
      this.newName = newName;
   }

   public int getId() {
      return id;
   }

   public int getCharacterId() {
      return characterId;
   }

   public String getOldName() {
      return oldName;
   }

   public String getNewName() {
      return newName;
   }
}
