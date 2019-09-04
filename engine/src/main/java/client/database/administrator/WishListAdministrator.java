package client.database.administrator;

import java.sql.Connection;
import java.util.List;

import client.database.AbstractQueryExecutor;
import client.database.DeleteForCharacter;

public class WishListAdministrator extends AbstractQueryExecutor implements DeleteForCharacter {
   private static WishListAdministrator instance;

   private WishListAdministrator() {
   }

   public static WishListAdministrator getInstance() {
      if (instance == null) {
         instance = new WishListAdministrator();
      }
      return instance;
   }

   public void addForCharacter(Connection connection, int characterId, List<Integer> snList) {
      String sql = "INSERT INTO `wishlists` VALUES (DEFAULT, ?, ?)";
      batch(connection, sql, (ps, data) -> {
         ps.setInt(1, characterId);
         ps.setInt(2, data);
      }, snList);
   }

   @Override
   public void deleteForCharacter(Connection connection, int characterId) {
      String sql = "DELETE FROM wishlists WHERE charid = ?";
      execute(connection, sql, ps -> ps.setInt(1, characterId));
   }
}
