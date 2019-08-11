package client.database.data;

public class NoteData {
   private int id;

   private String from;

   private String message;

   private long timestamp;

   private byte fame;

   public NoteData(int id, String from, String message, long timestamp, byte fame) {
      this.id = id;
      this.from = from;
      this.message = message;
      this.timestamp = timestamp;
      this.fame = fame;
   }

   public int getId() {
      return id;
   }

   public String getFrom() {
      return from;
   }

   public String getMessage() {
      return message;
   }

   public long getTimestamp() {
      return timestamp;
   }

   public byte getFame() {
      return fame;
   }
}
