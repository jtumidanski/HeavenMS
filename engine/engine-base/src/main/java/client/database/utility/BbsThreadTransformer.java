package client.database.utility;

import client.database.data.BbsThreadData;
import database.SqlTransformer;
import entity.bbs.BBSThread;

public class BbsThreadTransformer implements SqlTransformer<BbsThreadData, BBSThread> {
   @Override
   public BbsThreadData transform(BBSThread bbsThread) {
      return new BbsThreadData(bbsThread.getPosterId(), bbsThread.getTimestamp(),
            bbsThread.getName(), bbsThread.getStartPost(), bbsThread.getIcon(), bbsThread.getReplyCount(), bbsThread.getThreadId());
   }
}
