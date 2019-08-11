package client.database.provider;

import java.sql.Connection;
import java.util.Optional;

import client.database.AbstractQueryExecutor;

public class IpBanProvider extends AbstractQueryExecutor {
   private static IpBanProvider instance;

   public static IpBanProvider getInstance() {
      if (instance == null) {
         instance = new IpBanProvider();
      }
      return instance;
   }

   private IpBanProvider() {
   }

   public int getIpBanCount(Connection connection, String ipAddress) {
      String sql = "SELECT COUNT(*) FROM ipbans WHERE ? LIKE CONCAT(ip, '%')";
      Optional<Integer> result = getSingle(connection, sql, ps -> ps.setString(1, ipAddress), 1);
      return result.orElse(0);
   }
}
