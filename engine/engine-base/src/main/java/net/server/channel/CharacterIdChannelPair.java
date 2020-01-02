package net.server.channel;

public class CharacterIdChannelPair {
   private int characterId;
   private int channel;

   public CharacterIdChannelPair() {
   }

   public CharacterIdChannelPair(int characterId, int channel) {
      this.characterId = characterId;
      this.channel = channel;
   }

   public int getCharacterId() {
      return characterId;
   }

   public int getChannel() {
      return channel;
   }
}
