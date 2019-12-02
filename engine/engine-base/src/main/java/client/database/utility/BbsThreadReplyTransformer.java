package client.database.utility;

import client.database.data.BbsThreadReplyData;
import database.SqlTransformer;
import entity.bbs.BBSReply;

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
