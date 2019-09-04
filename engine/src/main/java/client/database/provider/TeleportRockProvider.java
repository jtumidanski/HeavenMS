package client.database.provider;

import java.sql.Connection;
import java.util.List;

import client.database.AbstractQueryExecutor;
import tools.Pair;

public class TeleportRockProvider extends AbstractQueryExecutor {
   private static TeleportRockProvider instance;

   public static TeleportRockProvider getInstance() {
      if (instance == null) {
         instance = new TeleportRockProvider();
      }
      return instance;
   }

   private TeleportRockProvider() {
   }

   public List<Pair<Integer, Integer>> getTeleportLocations(Connection connection, int characterId) {
      String sql = "SELECT mapid,vip FROM trocklocations WHERE characterid = ? LIMIT 15";
      return getListNew(connection, sql, ps -> ps.setInt(1, characterId), rs -> new Pair<>(rs.getInt(1), rs.getInt(2)));
   }
}