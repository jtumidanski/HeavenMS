package client.database.provider;

import java.sql.Connection;
import java.util.List;

import client.database.AbstractQueryExecutor;
import tools.Pair;

public class EventStatProvider extends AbstractQueryExecutor {
   private static EventStatProvider instance;

   public static EventStatProvider getInstance() {
      if (instance == null) {
         instance = new EventStatProvider();
      }
      return instance;
   }

   private EventStatProvider() {
   }

   public List<Pair<String, Integer>> getInfo(Connection connection, int characterId) {
      String sql = "SELECT `name`,`info` FROM eventstats WHERE characterid = ?";
      return getListNew(connection, sql, ps -> ps.setInt(1, characterId), rs -> new Pair<>(rs.getString("name"), rs.getInt("info")));
   }
}