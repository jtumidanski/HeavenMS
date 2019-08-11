package client.database.provider;

import java.sql.Connection;
import java.util.List;

import client.database.AbstractQueryExecutor;
import client.database.utility.SpecialCashItemTransformer;
import server.CashShop;

public class SpecialCashItemProvider extends AbstractQueryExecutor {
   private static SpecialCashItemProvider instance;

   public static SpecialCashItemProvider getInstance() {
      if (instance == null) {
         instance = new SpecialCashItemProvider();
      }
      return instance;
   }

   private SpecialCashItemProvider() {
   }

   public List<CashShop.SpecialCashItem> getSpecialCashItems(Connection connection) {
      String sql = "SELECT * FROM specialcashitems";
      SpecialCashItemTransformer transformer = new SpecialCashItemTransformer();
      return getListNew(connection, sql, transformer::transform);
   }
}