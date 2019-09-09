package client.database.administrator;

import java.sql.Connection;

import client.database.AbstractQueryExecutor;

public class HwidBanAdministrator extends AbstractQueryExecutor {
   private static HwidBanAdministrator instance;

   public static HwidBanAdministrator getInstance() {
      if (instance == null) {
         instance = new HwidBanAdministrator();
      }
      return instance;
   }

   private HwidBanAdministrator() {
   }

   public void banHwid(Connection connection, String hwid) {
      String sql = "INSERT INTO hwidbans (hwid) VALUES (?)";
      execute(connection, sql, ps -> ps.setString(1, hwid));
   }
}
