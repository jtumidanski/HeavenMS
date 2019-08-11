package client.database.provider;

import java.sql.Connection;
import java.util.Optional;

import client.database.AbstractQueryExecutor;

public class BossLogDailyProvider extends AbstractQueryExecutor {
   private static BossLogDailyProvider instance;

   public static BossLogDailyProvider getInstance() {
      if (instance == null) {
         instance = new BossLogDailyProvider();
      }
      return instance;
   }

   private BossLogDailyProvider() {
   }

   public int countEntriesForCharacter(Connection connection, int characterId, String type) {
      String sql = "SELECT COUNT(*) FROM bosslog_daily WHERE characterid = ? AND bosstype LIKE ?";
      Optional<Integer> result = getSingle(connection, sql, ps -> {
         ps.setInt(1, characterId);
         ps.setString(2, type);
      }, 1);
      return result.orElse(-1);
   }
}