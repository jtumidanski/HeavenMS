package client.database.administrator;

import java.sql.Connection;
import java.sql.Timestamp;

import client.database.AbstractQueryExecutor;

public class BossLogWeeklyAdministrator extends AbstractQueryExecutor {
   private static BossLogWeeklyAdministrator instance;

   public static BossLogWeeklyAdministrator getInstance() {
      if (instance == null) {
         instance = new BossLogWeeklyAdministrator();
      }
      return instance;
   }

   private BossLogWeeklyAdministrator() {
   }

   public void deleteByAttemptTimeAndBossType(Connection connection, Timestamp timestamp, String type) {
      String sql = "DELETE FROM bosslog_weekly WHERE attempttime <= ? AND bosstype LIKE ?";
      execute(connection, sql, ps -> {
         ps.setTimestamp(1, timestamp);
         ps.setString(2, type);
      });
   }

   public void addAttempt(Connection connection, int characterId, String type) {
      String sql = "INSERT INTO bosslog_weekly (characterid, bosstype) VALUES (?,?)";
      execute(connection, sql, ps -> {
         ps.setInt(1, characterId);
         ps.setString(2, type);
      });
   }
}