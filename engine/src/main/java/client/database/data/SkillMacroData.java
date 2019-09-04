package client.database.data;

public class SkillMacroData {
   private int position;

   private int skill1Id;

   private int skill2Id;

   private int skill3Id;

   private String name;

   private int shout;

   public SkillMacroData(int position, int skill1Id, int skill2Id, int skill3Id, String name, int shout) {
      this.position = position;
      this.skill1Id = skill1Id;
      this.skill2Id = skill2Id;
      this.skill3Id = skill3Id;
      this.name = name;
      this.shout = shout;
   }

   public int getPosition() {
      return position;
   }

   public int getSkill1Id() {
      return skill1Id;
   }

   public int getSkill2Id() {
      return skill2Id;
   }

   public int getSkill3Id() {
      return skill3Id;
   }

   public String getName() {
      return name;
   }

   public int getShout() {
      return shout;
   }
}
