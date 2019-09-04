package client.database.provider;

import java.sql.Connection;
import java.util.List;
import java.util.Optional;

import client.database.AbstractQueryExecutor;

public class MtsCartProvider extends AbstractQueryExecutor {
   private static MtsCartProvider instance;

   public static MtsCartProvider getInstance() {
      if (instance == null) {
         instance = new MtsCartProvider();
      }
      return instance;
   }

   private MtsCartProvider() {
   }

   public boolean isItemInCart(Connection connection, int characterId, int itemId) {
      String sql = "SELECT cid FROM mts_cart WHERE cid = ? AND itemid = ?";
      return getSingle(connection, sql, ps -> {
         ps.setInt(1, characterId);
         ps.setInt(2, itemId);
      }, "cid").isPresent();
   }

   public long countCartSize(Connection connection, int characterId) {
      String sql = "SELECT COUNT(*) FROM mts_cart WHERE cid = ?";
      Optional<Long> result = getSingle(connection, sql, ps -> ps.setInt(1, characterId), 1);
      return result.orElse(0L);
   }

   public List<Integer> getCartItems(Connection connection, int characterId) {
      String sql = "SELECT * FROM mts_cart WHERE cid = ? ORDER BY id DESC";
      return getListNew(connection, sql, ps -> ps.setInt(1, characterId), rs -> rs.getInt("itemid"));
   }
}