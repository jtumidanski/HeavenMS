package client.database.provider;

import java.sql.Connection;
import java.util.Optional;

import client.database.AbstractQueryExecutor;

public class BossLogWeeklyProvider extends AbstractQueryExecutor {
   private static BossLogWeeklyProvider instance;

   public static BossLogWeeklyProvider getInstance() {
      if (instance == null) {
         instance = new BossLogWeeklyProvider();
      }
      return instance;
   }

   private BossLogWeeklyProvider() {
   }

   public int countEntriesForCharacter(Connection connection, int characterId, String type) {
      String sql = "SELECT COUNT(*) FROM bosslog_weekly WHERE characterid = ? AND bosstype LIKE ?";
      Optional<Integer> result = getSingle(connection, sql, ps -> {
         ps.setInt(1, characterId);
         ps.setString(2, type);
      }, 1);
      return result.orElse(-1);
   }
}