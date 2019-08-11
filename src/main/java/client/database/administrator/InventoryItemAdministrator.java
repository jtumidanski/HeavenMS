package client.database.administrator;

import java.sql.Connection;
import java.util.List;

import client.database.AbstractQueryExecutor;
import client.database.DeleteForCharacter;
import tools.Pair;

public class InventoryItemAdministrator extends AbstractQueryExecutor implements DeleteForCharacter {
   private static InventoryItemAdministrator instance;

   public static InventoryItemAdministrator getInstance() {
      if (instance == null) {
         instance = new InventoryItemAdministrator();
      }
      return instance;
   }

   private InventoryItemAdministrator() {
   }

   @Override
   public void deleteForCharacter(Connection connection, int characterId) {
      String sql = "DELETE FROM inventoryitems WHERE characterid = ?";
      execute(connection, sql, ps -> ps.setInt(1, characterId));
   }

   public void deleteByCharacterAndTypeBatch(Connection connection, List<Pair<Integer, Integer>> data) {
      String sql = "DELETE FROM `inventoryitems` WHERE `type` = ? AND `characterid` = ?";
      batch(connection, sql, (ps, dataPoint) -> {
         ps.setInt(1, dataPoint.getLeft());
         ps.setInt(2, dataPoint.getRight());
      }, data);
   }

   public void deleteForCharacterByType(Connection connection, int characterId, int type) {
      String sql = "DELETE `inventoryitems`, `inventoryequipment` FROM `inventoryitems` LEFT JOIN `inventoryequipment` USING(`inventoryitemid`) WHERE `characterid` = ? AND `type` = ?";
      execute(connection, sql, ps -> {
         ps.setInt(1, characterId);
         ps.setInt(2, type);
      });
   }

   public void deleteForAccountByType(Connection connection, int accountId, int type) {
      String sql = "DELETE `inventoryitems`, `inventoryequipment` FROM `inventoryitems` LEFT JOIN `inventoryequipment` USING(`inventoryitemid`) WHERE `accountid` = ? AND `type` = ?";
      execute(connection, sql, ps -> {
         ps.setInt(1, accountId);
         ps.setInt(2, type);
      });
   }

   public int create(Connection connection, int type, int characterId, int accountId, int itemId, int inventoryType,
                      int position, int quantity, String owner, int petId, int flag, long expiration, String giftFrom) {
      String sql = "INSERT INTO `inventoryitems` VALUES (DEFAULT, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
      return insertAndReturnKey(connection, sql, ps -> {
         ps.setInt(1, type);
         ps.setInt(2, characterId);
         ps.setInt(3, accountId);
         ps.setInt(4, itemId);
         ps.setInt(5, inventoryType);
         ps.setInt(6, position);
         ps.setInt(7, quantity);
         ps.setString(8, owner);
         ps.setInt(9, petId);
         ps.setInt(10, flag);
         ps.setLong(11, expiration);
         ps.setString(12, giftFrom);
      });
   }

   public void expireItem(Connection connection, int itemId, int characterId) {
      String sql = "UPDATE inventoryitems SET expiration=0 WHERE itemid=? AND characterid=?";
      execute(connection, sql, ps -> {
         ps.setInt(1, itemId);
         ps.setInt(2, characterId);
      });
   }
}