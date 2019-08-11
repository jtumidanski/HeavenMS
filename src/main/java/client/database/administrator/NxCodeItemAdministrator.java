package client.database.administrator;

import java.sql.Connection;
import java.util.List;

import client.database.AbstractQueryExecutor;

public class NxCodeItemAdministrator extends AbstractQueryExecutor {
   private static NxCodeItemAdministrator instance;

   public static NxCodeItemAdministrator getInstance() {
      if (instance == null) {
         instance = new NxCodeItemAdministrator();
      }
      return instance;
   }

   private NxCodeItemAdministrator() {
   }

   public void deleteItems(Connection connection, List<Integer> itemIds) {
      String sql = "DELETE FROM nxcode_items WHERE codeid = ?";
      batch(connection, sql, (ps, data) -> ps.setInt(1, data), itemIds);
   }
}