package client.database.data;

public class SkillData {
   private int skillId;

   private byte skillLevel;

   private int masterLevel;

   private long expiration;

   public SkillData(int skillId, byte skillLevel, int masterLevel, long expiration) {
      this.skillId = skillId;
      this.skillLevel = skillLevel;
      this.masterLevel = masterLevel;
      this.expiration = expiration;
   }

   public int getSkillId() {
      return skillId;
   }

   public byte getSkillLevel() {
      return skillLevel;
   }

   public int getMasterLevel() {
      return masterLevel;
   }

   public long getExpiration() {
      return expiration;
   }
}
