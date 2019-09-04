package client.database.data;

public class CharacterNameNote {
   private int characterId;

   private String characterName;

   private int note;

   public CharacterNameNote(int characterId, String characterName, int note) {
      this.characterId = characterId;
      this.characterName = characterName;
      this.note = note;
   }

   public int getCharacterId() {
      return characterId;
   }

   public String getCharacterName() {
      return characterName;
   }

   public int getNote() {
      return note;
   }
}
