package client.database.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public record BbsThreadData(int posterCharacterId, long timestamp, String name, String startPost, int icon,
                            int replyCount, int threadId, List<BbsThreadReplyData> replyData) {
   public BbsThreadData addReply(BbsThreadReplyData data) {
      List<BbsThreadReplyData> newReplies = new ArrayList<>(replyData);
      newReplies.add(data);
      return new BbsThreadData(posterCharacterId, timestamp, name, startPost, icon, replyCount, threadId, Collections.unmodifiableList(newReplies));
   }
}
