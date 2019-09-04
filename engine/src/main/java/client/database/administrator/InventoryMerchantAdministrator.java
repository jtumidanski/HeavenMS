package client.database.administrator;

import java.sql.Connection;

import client.database.AbstractQueryExecutor;

public class InventoryMerchantAdministrator extends AbstractQueryExecutor {
   private static InventoryMerchantAdministrator instance;

   public static InventoryMerchantAdministrator getInstance() {
      if (instance == null) {
         instance = new InventoryMerchantAdministrator();
      }
      return instance;
   }

   private InventoryMerchantAdministrator() {
   }

   public void deleteForCharacter(Connection connection, int characterId) {
      String sql = "DELETE FROM `inventorymerchant` WHERE `characterid` = ?";
      execute(connection, sql, ps -> ps.setInt(1, characterId));
   }

   public void create(Connection connection, int itemId, int characterId, int bundle) {
      String sql = "INSERT INTO `inventorymerchant` VALUES (DEFAULT, ?, ?, ?)";
      execute(connection, sql, ps -> {
         ps.setInt(1, itemId);
         ps.setInt(2, characterId);
         ps.setInt(3, bundle);
      });
   }
}