package client.database.data;

public class PlayerDiseaseData {
   private int disease;

   private int mobSkillId;

   private int mobSkillLevel;

   private int length;

   public PlayerDiseaseData(int disease, int mobSkillId, int mobSkillLevel, int length) {
      this.disease = disease;
      this.mobSkillId = mobSkillId;
      this.mobSkillLevel = mobSkillLevel;
      this.length = length;
   }

   public int getDisease() {
      return disease;
   }

   public int getMobSkillId() {
      return mobSkillId;
   }

   public int getMobSkillLevel() {
      return mobSkillLevel;
   }

   public int getLength() {
      return length;
   }
}
