package client.database.provider;

import java.sql.Connection;
import java.util.List;
import java.util.Optional;

import client.database.AbstractQueryExecutor;
import client.database.data.BbsThreadReplyData;
import client.database.utility.BbsThreadReplyTransformer;

public class BbsThreadReplyProvider extends AbstractQueryExecutor {
   private static BbsThreadReplyProvider instance;

   public static BbsThreadReplyProvider getInstance() {
      if (instance == null) {
         instance = new BbsThreadReplyProvider();
      }
      return instance;
   }

   private BbsThreadReplyProvider() {
   }

   public List<BbsThreadReplyData> getByThreadId(Connection connection, int threadId) {
      String sql = "SELECT * FROM bbs_replies WHERE threadid = ?";
      BbsThreadReplyTransformer transformer = new BbsThreadReplyTransformer();
      return getListNew(connection, sql, ps -> ps.setInt(1, threadId), transformer::transform);
   }

   public Optional<BbsThreadReplyData> getByReplyId(Connection connection, int replyId) {
      String sql = "SELECT * FROM bbs_replies WHERE replyid = ?";
      BbsThreadReplyTransformer transformer = new BbsThreadReplyTransformer();
      return getNew(connection, sql, ps -> ps.setInt(1, replyId), transformer::transform);
   }
}