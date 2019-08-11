package client.database.provider;

import java.sql.Connection;
import java.util.LinkedList;
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
      return getList(connection, sql, ps -> {
      }, rs -> {
         List<String> filtered = new LinkedList<>();
         while (rs.next()) {
            filtered.add(rs.getString("filter"));
         }
         return filtered;
      });
   }
}
