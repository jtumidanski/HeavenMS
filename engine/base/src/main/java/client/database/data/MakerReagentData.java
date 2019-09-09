package client.database.data;

public class MakerReagentData {
   private String stat;

   private int value;

   public MakerReagentData(String stat, int value) {
      this.stat = stat;
      this.value = value;
   }

   public String getStat() {
      return stat;
   }

   public int getValue() {
      return value;
   }
}
