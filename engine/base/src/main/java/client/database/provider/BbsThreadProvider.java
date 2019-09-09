package client.database.provider;

import java.sql.Connection;
import java.util.List;
import java.util.Optional;

import client.database.AbstractQueryExecutor;
import client.database.data.BbsThreadData;
import client.database.utility.BbsThreadTransformer;

public class BbsThreadProvider extends AbstractQueryExecutor {
   private static BbsThreadProvider instance;

   public static BbsThreadProvider getInstance() {
      if (instance == null) {
         instance = new BbsThreadProvider();
      }
      return instance;
   }

   private BbsThreadProvider() {
   }

   public Optional<BbsThreadData> getByThreadAndGuildId(Connection connection, int threadId, int guildId, boolean localThread) {
      String sql = "SELECT * FROM bbs_threads WHERE guildid = ? AND " + (localThread ? "local" : "") + "threadid = ?";
      BbsThreadTransformer transformer = new BbsThreadTransformer();
      return get(connection, sql, ps -> {
         ps.setInt(1, guildId);
         ps.setInt(2, threadId);
      }, rs -> {
         if (rs != null && rs.next()) {
            BbsThreadData threadData = transformer.transform(rs);
            BbsThreadReplyProvider.getInstance().getByThreadId(connection, !localThread ? threadId : threadData.getThreadId())
                  .forEach(threadData::addReply);
            return Optional.of(threadData);
         }
         return Optional.empty();
      });
   }

   public List<BbsThreadData> getThreadsForGuild(Connection connection, int guildId) {
      String sql = "SELECT * FROM bbs_threads WHERE guildid = ? ORDER BY localthreadid DESC";
      BbsThreadTransformer transformer = new BbsThreadTransformer();
      return getListNew(connection, sql, ps -> ps.setInt(1, guildId), transformer::transform);
   }

   public int getNextLocalThreadId(Connection connection, int guildId) {
      String sql = "SELECT MAX(localthreadid) AS lastLocalId FROM bbs_threads WHERE guildid = ?";
      Optional<Integer> result = getSingle(connection, sql, ps -> ps.setInt(1, guildId), "lastLocalId");
      return result.orElse(-1) + 1;
   }
}