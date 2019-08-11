package client.database.data;

public class CoolDownData {
   private int skillId;

   private long length;

   private long startTime;

   public CoolDownData(int skillId, long length, long startTime) {
      this.skillId = skillId;
      this.length = length;
      this.startTime = startTime;
   }

   public int getSkillId() {
      return skillId;
   }

   public long getLength() {
      return length;
   }

   public long getStartTime() {
      return startTime;
   }
}
