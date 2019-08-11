package client.database.data;

public class BbsThreadReplyData {
   private int threadId;

   private int replyId;

   private int posterCharacterId;

   private long timestamp;

   private String content;

   public BbsThreadReplyData(int replyId, int posterCharacterId, long timestamp, String content, int threadId) {
      this.replyId = replyId;
      this.posterCharacterId = posterCharacterId;
      this.timestamp = timestamp;
      this.content = content;
      this.threadId = threadId;
   }

   public int getThreadId() {
      return threadId;
   }

   public int getReplyId() {
      return replyId;
   }

   public int getPosterCharacterId() {
      return posterCharacterId;
   }

   public long getTimestamp() {
      return timestamp;
   }

   public String getContent() {
      return content;
   }
}
