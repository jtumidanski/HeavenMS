package client.database.data;

public class MarriageData {
   private int id;

   private int spouse1;

   private int spouse2;

   public MarriageData(int id, int spouse1, int spouse2) {
      this.id = id;
      this.spouse1 = spouse1;
      this.spouse2 = spouse2;
   }

   public int getId() {
      return id;
   }

   public int getSpouse1() {
      return spouse1;
   }

   public int getSpouse2() {
      return spouse2;
   }
}
