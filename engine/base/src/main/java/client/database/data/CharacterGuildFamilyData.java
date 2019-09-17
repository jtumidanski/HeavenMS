package client.database.data;

public class CharacterGuildFamilyData {
   private int world;

   private int guildId;

   private int guildRank;

   private int familyId;

   public CharacterGuildFamilyData(int world, int guildId, int guildRank, int familyId) {
      this.world = world;
      this.guildId = guildId;
      this.guildRank = guildRank;
      this.familyId = familyId;
   }

   public int getWorld() {
      return world;
   }

   public int getGuildId() {
      return guildId;
   }

   public int getGuildRank() {
      return guildRank;
   }

   public int getFamilyId() {
      return familyId;
   }
}
