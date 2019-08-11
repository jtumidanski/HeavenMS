package client.database.provider;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import client.database.AbstractQueryExecutor;
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
      return getList(connection, sql, ps -> {
      }, rs -> {
         List<CashShop.SpecialCashItem> specialCashItems = new ArrayList<>();
         while (rs.next()) {
            specialCashItems.add(new CashShop.SpecialCashItem(rs.getInt("sn"), rs.getInt("modifier"), rs.getByte("info")));
         }
         return specialCashItems;
      });
   }
}