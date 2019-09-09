package client;

public class MapleCoolDownValueHolder {

   public int skillId;
   public long startTime, length;

   public MapleCoolDownValueHolder(int skillId, long startTime, long length) {
      super();
      this.skillId = skillId;
      this.startTime = startTime;
      this.length = length;
   }
}
