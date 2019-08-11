package client.database.provider;

import java.sql.Connection;
import java.util.List;

import client.database.AbstractQueryExecutor;

public class InventoryEquipmentProvider extends AbstractQueryExecutor {
   private static InventoryEquipmentProvider instance;

   public static InventoryEquipmentProvider getInstance() {
      if (instance == null) {
         instance = new InventoryEquipmentProvider();
      }
      return instance;
   }

   private InventoryEquipmentProvider() {
   }

   public List<Integer> getRings(Connection connection, int inventoryItemId) {
      String sql = "SELECT ringid FROM inventoryequipment WHERE inventoryitemid = ?";
      return getListNew(connection, sql, ps -> ps.setInt(1, inventoryItemId), rs -> rs.getInt("ringid"));
   }
}