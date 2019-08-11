package client.database.data;

public class PetData {
   private String name;

   private byte level;

   private int closeness;

   private int fullness;

   private boolean summoned;

   private int flag;

   public PetData(String name, byte level, int closeness, int fullness, boolean summoned, int flag) {
      this.name = name;
      this.level = level;
      this.closeness = closeness;
      this.fullness = fullness;
      this.summoned = summoned;
      this.flag = flag;
   }

   public String getName() {
      return name;
   }

   public byte getLevel() {
      return level;
   }

   public int getCloseness() {
      return closeness;
   }

   public int getFullness() {
      return fullness;
   }

   public boolean isSummoned() {
      return summoned;
   }

   public int getFlag() {
      return flag;
   }
}
