package client.database.provider;

import java.sql.Connection;
import java.util.List;

import client.database.AbstractQueryExecutor;

public class MacFilterProvider extends AbstractQueryExecutor {
   private static MacFilterProvider instance;

   public static MacFilterProvider getInstance() {
      if (instance == null) {
         instance = new MacFilterProvider();
      }
      return instance;
   }

   private MacFilterProvider() {
   }

   public List<String> getMacFilters(Connection connection) {
      String sql = "SELECT filter FROM macfilters";
      return getListNew(connection, sql, rs -> rs.getString("filter"));
   }
}
