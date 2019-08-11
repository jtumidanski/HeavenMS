package client.database.administrator;

import java.sql.Connection;

import client.database.AbstractQueryExecutor;

public class IpBanAdministrator extends AbstractQueryExecutor {
   private static IpBanAdministrator instance;

   public static IpBanAdministrator getInstance() {
      if (instance == null) {
         instance = new IpBanAdministrator();
      }
      return instance;
   }

   private IpBanAdministrator() {
   }

   public void banIp(Connection connection, String ip) {
      String sql = "INSERT INTO ipbans VALUES (DEFAULT, ?)";
      execute(connection, sql, ps -> ps.setString(1, ip));
   }

   public void banIp(Connection connection, String ip, int accountId) {
      String sql = "INSERT INTO ipbans VALUES (DEFAULT, ?, ?)";
      execute(connection, sql, ps -> {
         ps.setString(1, ip);
         ps.setString(2, String.valueOf(accountId));
      });
   }

   public void removeIpBan(Connection connection, int accountId) {
      String sql = "DELETE FROM ipbans WHERE aid = ?";
      execute(connection, sql, ps -> ps.setInt(1, accountId));
   }
}
