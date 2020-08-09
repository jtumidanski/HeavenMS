package client.database.utility;

import java.util.Collections;

import client.database.data.BbsThreadData;
import entity.bbs.BBSThread;
import transformer.SqlTransformer;

public class BbsThreadTransformer implements SqlTransformer<BbsThreadData, BBSThread> {
   @Override
   public BbsThreadData transform(BBSThread bbsThread) {
      return new BbsThreadData(bbsThread.getPosterId(), bbsThread.getTimestamp(),
            bbsThread.getName(), bbsThread.getStartPost(), bbsThread.getIcon(), bbsThread.getReplyCount(),
            bbsThread.getThreadId(), Collections.emptyList());
   }
}
