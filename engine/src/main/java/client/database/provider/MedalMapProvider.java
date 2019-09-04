package client.database.provider;

import java.sql.Connection;
import java.util.List;

import client.database.AbstractQueryExecutor;
import tools.Pair;

public class MedalMapProvider extends AbstractQueryExecutor {
   private static MedalMapProvider instance;

   public static MedalMapProvider getInstance() {
      if (instance == null) {
         instance = new MedalMapProvider();
      }
      return instance;
   }

   private MedalMapProvider() {
   }

   public List<Pair<Integer, Integer>> get(Connection connection, int characterId) {
      String sql = "SELECT * FROM medalmaps WHERE characterid = ?";
      return getListNew(connection, sql, ps -> ps.setInt(1, characterId), rs -> new Pair<>(rs.getInt("queststatusid"), rs.getInt("mapid")));
   }
}