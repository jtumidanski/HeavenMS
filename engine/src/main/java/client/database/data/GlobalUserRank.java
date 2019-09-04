package client.database.data;

public class GlobalUserRank {
   private String name;

   private int level;

   public GlobalUserRank(String name, int level) {
      this.name = name;
      this.level = level;
   }

   public String getName() {
      return name;
   }

   public int getLevel() {
      return level;
   }
}
