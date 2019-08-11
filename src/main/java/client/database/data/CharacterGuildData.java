package client.database.data;

public class CharacterGuildData {
   private int id;

   private int guildId;

   private int guildRank;

   private String name;

   private int allianceRank;

   private int level;

   private int job;

   public CharacterGuildData(int id, int guildId, int guildRank, String name, int allianceRank, int level, int job) {
      this.id = id;
      this.guildId = guildId;
      this.guildRank = guildRank;
      this.name = name;
      this.allianceRank = allianceRank;
      this.level = level;
      this.job = job;
   }

   public int getId() {
      return id;
   }

   public int getGuildId() {
      return guildId;
   }

   public int getGuildRank() {
      return guildRank;
   }

   public String getName() {
      return name;
   }

   public int getAllianceRank() {
      return allianceRank;
   }

   public int getLevel() {
      return level;
   }

   public int getJob() {
      return job;
   }
}
