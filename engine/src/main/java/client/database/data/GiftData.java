package client.database.data;

public class GiftData {
   private int sn;

   private int ringId;

   private String message;

   private String from;

   public GiftData(int sn, int ringId, String message, String from) {
      this.sn = sn;
      this.ringId = ringId;
      this.message = message;
      this.from = from;
   }

   public int getSn() {
      return sn;
   }

   public int getRingId() {
      return ringId;
   }

   public String getMessage() {
      return message;
   }

   public String getFrom() {
      return from;
   }
}
