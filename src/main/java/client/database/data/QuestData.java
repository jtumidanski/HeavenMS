package client.database.data;

public class QuestData {
   private short questId;

   private int status;

   private long time;

   private long expires;

   private int forfeited;

   private int completed;

   private int questStatusId;

   public QuestData(short questId, int status, long time, long expires, int forfeited, int completed, int questStatusId) {
      this.questId = questId;
      this.status = status;
      this.time = time;
      this.expires = expires;
      this.forfeited = forfeited;
      this.completed = completed;
      this.questStatusId = questStatusId;
   }

   public short getQuestId() {
      return questId;
   }

   public int getStatus() {
      return status;
   }

   public long getTime() {
      return time;
   }

   public long getExpires() {
      return expires;
   }

   public int getForfeited() {
      return forfeited;
   }

   public int getCompleted() {
      return completed;
   }

   public int getQuestStatusId() {
      return questStatusId;
   }
}
