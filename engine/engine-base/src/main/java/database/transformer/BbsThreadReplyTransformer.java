package database.transformer;

import client.database.data.BbsThreadReplyData;
import entity.bbs.BBSReply;
import transformer.SqlTransformer;

public class BbsThreadReplyTransformer implements SqlTransformer<BbsThreadReplyData, BBSReply> {
   @Override
   public BbsThreadReplyData transform(BBSReply bbsReply) {
      return new BbsThreadReplyData(
            bbsReply.getThreadId(),
            bbsReply.getReplyId(),
            bbsReply.getPosterId(),
            bbsReply.getTimestamp(),
            bbsReply.getContent());
   }
}
