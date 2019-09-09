package client.database.administrator;

import java.sql.Connection;

import client.database.AbstractQueryExecutor;
import client.database.DeleteForCharacter;

public class MtsCartAdministrator extends AbstractQueryExecutor implements DeleteForCharacter {
   private static MtsCartAdministrator instance;

   public static MtsCartAdministrator getInstance() {
      if (instance == null) {
         instance = new MtsCartAdministrator();
      }
      return instance;
   }

   private MtsCartAdministrator() {
   }

   @Override
   public void deleteForCharacter(Connection connection, int characterId) {
      String sql = "DELETE FROM mts_cart WHERE cid = ?";
      execute(connection, sql, ps -> ps.setInt(1, characterId));
   }

   public void removeItemFromCarts(Connection connection, int itemId) {
      String sql = "DELETE FROM mts_cart WHERE itemid = ?";
      execute(connection, sql, ps -> ps.setInt(1, itemId));
   }

   public void removeItemFromCart(Connection connection, int itemId, int characterId) {
      String sql = "DELETE FROM mts_cart WHERE itemid = ? AND cid = ?";
      execute(connection, sql, ps -> {
         ps.setInt(1, itemId);
         ps.setInt(2, characterId);
      });
   }

   public void addToCart(Connection connection, int characterId, int itemId) {
      String sql = "INSERT INTO mts_cart (cid, itemid) VALUES (?, ?)";
      execute(connection, sql, ps -> {
         ps.setInt(1, characterId);
         ps.setInt(2, itemId);
      });
   }
}
