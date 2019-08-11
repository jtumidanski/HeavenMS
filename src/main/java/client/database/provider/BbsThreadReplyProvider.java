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
      BbsThreadReplyTransformer transformer = new BbsThreadReplyTransformer();
      String sql = "SELECT * FROM bbs_replies WHERE threadid = ?";
      return getListNew(connection, sql, ps -> ps.setInt(1, threadId), transformer::transform);
   }

   public Optional<BbsThreadReplyData> getByReplyId(Connection connection, int replyId) {
      BbsThreadReplyTransformer transformer = new BbsThreadReplyTransformer();
      String sql = "SELECT * FROM bbs_replies WHERE replyid = ?";
      return get(connection, sql, ps -> ps.setInt(1, replyId), rs -> {
         if (rs != null && rs.next()) {
            return Optional.of(transformer.transform(rs));
         }
         return Optional.empty();
      });
   }
}