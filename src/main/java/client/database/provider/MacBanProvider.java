package client.database.provider;

import java.sql.Connection;
import java.util.Optional;
import java.util.Set;

import client.database.AbstractQueryExecutor;

public class MacBanProvider extends AbstractQueryExecutor {
   private static MacBanProvider instance;

   public static MacBanProvider getInstance() {
      if (instance == null) {
         instance = new MacBanProvider();
      }
      return instance;
   }

   private MacBanProvider() {
   }

   public int getMacBanCount(Connection connection, Set<String> macs) {
      StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM macbans WHERE mac IN (");
      for (int i = 0; i < macs.size(); i++) {
         sql.append("?");
         if (i != macs.size() - 1) {
            sql.append(", ");
         }
      }
      sql.append(")");

      Optional<Integer> result = getNew(connection, sql.toString(), ps -> {
         int i = 0;
         for (String mac : macs) {
            i++;
            ps.setString(i, mac);
         }
      }, rs -> rs.getInt(1));
      return result.orElse(0);
   }
}
