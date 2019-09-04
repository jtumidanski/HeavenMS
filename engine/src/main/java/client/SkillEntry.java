package client;

public class SkillEntry {

   private int masterLevel;
   private byte skillLevel;
   private long expiration;

   public SkillEntry(byte skillLevel, int masterLevel, long expiration) {
      this.skillLevel = skillLevel;
      this.masterLevel = masterLevel;
      this.expiration = expiration;
   }

   public int getMasterLevel() {
      return masterLevel;
   }

   public byte getSkillLevel() {
      return skillLevel;
   }

   public long getExpiration() {
      return expiration;
   }

   @Override
   public String toString() {
      return skillLevel + ":" + masterLevel;
   }
}
