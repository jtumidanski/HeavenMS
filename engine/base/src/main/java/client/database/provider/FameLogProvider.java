package client.database.provider;

import java.sql.Connection;
import java.sql.Timestamp;
import java.util.List;

import client.database.AbstractQueryExecutor;
import tools.Pair;

public class FameLogProvider extends AbstractQueryExecutor {
   private static FameLogProvider instance;

   public static FameLogProvider getInstance() {
      if (instance == null) {
         instance = new FameLogProvider();
      }
      return instance;
   }

   private FameLogProvider() {
   }

   public List<Pair<Integer, Timestamp>> getForCharacter(Connection connection, int characterId) {
      String sql = "SELECT `characterid_to`,`when` FROM famelog WHERE characterid = ? AND DATEDIFF(NOW(),`when`) < 30";
      return getListNew(connection, sql, ps -> ps.setInt(1, characterId), rs -> new Pair<>(rs.getInt("characterid_to"), rs.getTimestamp("when")));
   }
}