package client.database.provider;

import java.sql.Connection;
import java.util.List;

import client.database.AbstractQueryExecutor;
import tools.Pair;

public class AreaInfoProvider extends AbstractQueryExecutor {
   private static AreaInfoProvider instance;

   public static AreaInfoProvider getInstance() {
      if (instance == null) {
         instance = new AreaInfoProvider();
      }
      return instance;
   }

   private AreaInfoProvider() {
   }

   public List<Pair<Short, String>> getAreaInfo(Connection connection, int characterId) {
      String sql = "SELECT `area`,`info` FROM area_info WHERE charid = ?";
      return getListNew(connection, sql, ps -> ps.setInt(1, characterId), rs -> new Pair<>(rs.getShort("area"), rs.getString("info")));
   }
}