package client.database.provider;

import java.sql.Connection;
import java.util.List;

import client.database.AbstractQueryExecutor;
import client.database.data.NxCodeItemData;
import client.database.utility.NxCodeItemTransformer;

public class NxCodeItemProvider extends AbstractQueryExecutor {
   private static NxCodeItemProvider instance;

   public static NxCodeItemProvider getInstance() {
      if (instance == null) {
         instance = new NxCodeItemProvider();
      }
      return instance;
   }

   private NxCodeItemProvider() {
   }

   public List<NxCodeItemData> get(Connection connection, int codeId) {
      String sql = "SELECT * FROM nxcode_items WHERE codeid = ?";
      NxCodeItemTransformer transformer = new NxCodeItemTransformer();
      return getListNew(connection, sql, ps -> ps.setInt(1, codeId), transformer::transform);
   }
}