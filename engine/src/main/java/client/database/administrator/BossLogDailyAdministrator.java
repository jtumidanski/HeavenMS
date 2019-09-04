package client.database.administrator;

import java.sql.Connection;
import java.sql.Timestamp;

import client.database.AbstractQueryExecutor;

public class BossLogDailyAdministrator extends AbstractQueryExecutor {
   private static BossLogDailyAdministrator instance;

   public static BossLogDailyAdministrator getInstance() {
      if (instance == null) {
         instance = new BossLogDailyAdministrator();
      }
      return instance;
   }

   private BossLogDailyAdministrator() {
   }

   public void deleteByAttemptTimeAndBossType(Connection connection, Timestamp timestamp, String type) {
      String sql = "DELETE FROM bosslog_daily WHERE attempttime <= ? AND bosstype LIKE ?";
      execute(connection, sql, ps -> {
         ps.setTimestamp(1, timestamp);
         ps.setString(2, type);
      });
   }

   public void addAttempt(Connection connection, int characterId, String type) {
      String sql = "INSERT INTO bosslog_daily (characterid, bosstype) VALUES (?,?)";
      execute(connection, sql, ps -> {
         ps.setInt(1, characterId);
         ps.setString(2, type);
      });
   }
}