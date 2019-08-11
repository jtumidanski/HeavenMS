package client.database.data;

public class CharacterWorldData {
   private int characterId;

   private int worldId;

   public CharacterWorldData(int characterId, int worldId) {
      this.characterId = characterId;
      this.worldId = worldId;
   }

   public int getCharacterId() {
      return characterId;
   }

   public int getWorldId() {
      return worldId;
   }
}
