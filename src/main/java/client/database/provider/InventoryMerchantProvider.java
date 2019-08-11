package client.database.provider;

import java.sql.Connection;
import java.util.Optional;

import client.database.AbstractQueryExecutor;

public class InventoryMerchantProvider extends AbstractQueryExecutor {
   private static InventoryMerchantProvider instance;

   public static InventoryMerchantProvider getInstance() {
      if (instance == null) {
         instance = new InventoryMerchantProvider();
      }
      return instance;
   }

   private InventoryMerchantProvider() {
   }

   public Short getBundleForItem(Connection connection, int inventoryItemId) {
      String sql = "SELECT `bundles` FROM `inventorymerchant` WHERE `inventoryitemid` = ?";
      Optional<Short> result = getSingle(connection, sql, ps -> ps.setInt(1, inventoryItemId), "bundles");
      return result.orElse((short) 0);
   }
}