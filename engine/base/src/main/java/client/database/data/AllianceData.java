package client.database.data;

public class AllianceData {
   private int capacity;

   private String name;

   private String notice;

   private String rank1;

   private String rank2;

   private String rank3;

   private String rank4;

   private String rank5;

   public AllianceData(int capacity, String name, String notice, String rank1, String rank2, String rank3, String rank4, String rank5) {
      this.capacity = capacity;
      this.name = name;
      this.notice = notice;
      this.rank1 = rank1;
      this.rank2 = rank2;
      this.rank3 = rank3;
      this.rank4 = rank4;
      this.rank5 = rank5;
   }

   public int getCapacity() {
      return capacity;
   }

   public String getName() {
      return name;
   }

   public String getNotice() {
      return notice;
   }

   public String getRank1() {
      return rank1;
   }

   public String getRank2() {
      return rank2;
   }

   public String getRank3() {
      return rank3;
   }

   public String getRank4() {
      return rank4;
   }

   public String getRank5() {
      return rank5;
   }
}
