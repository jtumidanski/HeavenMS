package client.database.provider;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

import client.database.AbstractQueryExecutor;
import server.MapleShop;

public class ShopProvider extends AbstractQueryExecutor {
   private static ShopProvider instance;

   public static ShopProvider getInstance() {
      if (instance == null) {
         instance = new ShopProvider();
      }
      return instance;
   }

   private ShopProvider() {
   }

   public Optional<MapleShop> getById(Connection connection, int shopId) {
      String sql = "SELECT * FROM shops WHERE shopid = ?";
      return getNew(connection, sql, ps -> ps.setInt(1, shopId), this::processGetShopResultSet);
   }

   public Optional<MapleShop> getByNPC(Connection connection, int npcId) {
      String sql = "SELECT * FROM shops WHERE npcid = ?";
      return getNew(connection, sql, ps -> ps.setInt(1, npcId), this::processGetShopResultSet);
   }

   private MapleShop processGetShopResultSet(ResultSet rs) throws SQLException {
      return new MapleShop(rs.getInt("shopid"), rs.getInt("npcid"));
   }
}