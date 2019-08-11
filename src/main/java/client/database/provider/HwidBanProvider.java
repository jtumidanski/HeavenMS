package client.database.provider;

import java.sql.Connection;
import java.util.Optional;

import client.database.AbstractQueryExecutor;

public class HwidBanProvider extends AbstractQueryExecutor {
   private static HwidBanProvider instance;

   public static HwidBanProvider getInstance() {
      if (instance == null) {
         instance = new HwidBanProvider();
      }
      return instance;
   }

   private HwidBanProvider() {
   }

   public int getHwidBanCount(Connection connection, String hwid) {
      String sql = "SELECT COUNT(*) FROM hwidbans WHERE hwid LIKE ?";
      Optional<Integer> result = getSingle(connection, sql, ps -> ps.setString(1, hwid), 1);
      return result.orElse(0);
   }
}
