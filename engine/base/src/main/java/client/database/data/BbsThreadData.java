package client.database.data;

import java.util.ArrayList;
import java.util.List;

public class BbsThreadData {
   private int posterCharacterId;

   private long timestamp;

   private String name;

   private String startPost;

   private int icon;

   private int replyCount;

   private int threadId;

   private List<BbsThreadReplyData> replyDataList;

   public BbsThreadData(int posterCharacterId, long timestamp, String name, String startPost, int icon, int replyCount, int threadId) {
      this.posterCharacterId = posterCharacterId;
      this.timestamp = timestamp;
      this.name = name;
      this.startPost = startPost;
      this.icon = icon;
      this.replyCount = replyCount;
      this.threadId = threadId;
   }

   public void addReply(BbsThreadReplyData replyData) {
      if (replyDataList == null) {
         replyDataList = new ArrayList<>();
      }
      replyDataList.add(replyData);
   }

   public List<BbsThreadReplyData> getReplyData() {
      return replyDataList;
   }

   public int getPosterCharacterId() {
      return posterCharacterId;
   }

   public long getTimestamp() {
      return timestamp;
   }

   public String getName() {
      return name;
   }

   public String getStartPost() {
      return startPost;
   }

   public int getIcon() {
      return icon;
   }

   public int getReplyCount() {
      return replyCount;
   }

   public int getThreadId() {
      return threadId;
   }
}
