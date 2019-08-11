package client.database.administrator;

import java.sql.Connection;
import java.sql.Timestamp;

import client.database.AbstractQueryExecutor;

public class HwidAccountAdministrator extends AbstractQueryExecutor {
   private static HwidAccountAdministrator instance;

   public static HwidAccountAdministrator getInstance() {
      if (instance == null) {
         instance = new HwidAccountAdministrator();
      }
      return instance;
   }

   private HwidAccountAdministrator() {
   }

   public void updateByAccountId(Connection connection, int accountId, String hwid, int relevance, Timestamp timestamp) {
      String sql = "UPDATE hwidaccounts SET relevance = ?, expiresat = ? WHERE accountid = ? AND hwid LIKE ?";
      execute(connection, sql, ps -> {
         ps.setInt(1, relevance);
         ps.setTimestamp(2, timestamp);
         ps.setInt(3, accountId);
         ps.setString(4, hwid);
      });
   }

   public void create(Connection connection, int accountId, String hwid, Timestamp timestamp) {
      String sql = "INSERT INTO hwidaccounts (accountid, hwid, expiresat) VALUES (?, ?, ?)";
      execute(connection, sql, ps -> {
         ps.setInt(1, accountId);
         ps.setString(2, hwid);
         ps.setTimestamp(3, timestamp);
      });
   }

   public void deleteExpired(Connection connection) {
      String sql = "DELETE FROM hwidaccounts WHERE expiresat < CURRENT_TIMESTAMP";
      executeNoParam(connection, sql);
   }
}