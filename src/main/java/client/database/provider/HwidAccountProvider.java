package client.database.provider;

import java.sql.Connection;
import java.util.List;

import client.database.AbstractQueryExecutor;
import tools.Pair;

public class HwidAccountProvider extends AbstractQueryExecutor {
   private static HwidAccountProvider instance;

   public static HwidAccountProvider getInstance() {
      if (instance == null) {
         instance = new HwidAccountProvider();
      }
      return instance;
   }

   private HwidAccountProvider() {
   }

   public List<String> getHwidForAccount(Connection connection, int accountId) {
      String sql = "SELECT SQL_CACHE hwid FROM hwidaccounts WHERE accountid = ?";
      return getListNew(connection, sql, ps -> ps.setInt(1, accountId), rs -> rs.getString("hwid"));
   }

   public List<Pair<String, Integer>> getForAccount(Connection connection, int accountId) {
      String sql = "SELECT SQL_CACHE * FROM hwidaccounts WHERE accountid = ?";
      return getListNew(connection, sql, ps -> ps.setInt(1, accountId), rs -> new Pair<>(rs.getString("hwid"), rs.getInt("relevance")));
   }
}