package client.database.provider;

import java.sql.Connection;
import java.util.List;

import client.database.AbstractQueryExecutor;

public class WishListProvider extends AbstractQueryExecutor {
   private static WishListProvider instance;

   public static WishListProvider getInstance() {
      if (instance == null) {
         instance = new WishListProvider();
      }
      return instance;
   }

   private WishListProvider() {
   }

   public List<Integer> getWishListSn(Connection connection, int characterId) {
      String sql = "SELECT `sn` FROM `wishlists` WHERE `charid` = ?";
      return getListNew(connection, sql, ps -> ps.setInt(1, characterId),
            rs -> rs.getInt("sn"));
   }
}