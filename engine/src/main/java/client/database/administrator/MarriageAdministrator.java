package client.database.administrator;

import java.sql.Connection;

import client.database.AbstractQueryExecutor;

public class MarriageAdministrator extends AbstractQueryExecutor {
   private static MarriageAdministrator instance;

   public static MarriageAdministrator getInstance() {
      if (instance == null) {
         instance = new MarriageAdministrator();
      }
      return instance;
   }

   private MarriageAdministrator() {
   }

   public void endMarriage(Connection connection, int playerId) {
      String sql = "DELETE FROM marriages WHERE marriageid = ?";
      execute(connection, sql, ps -> ps.setInt(1, playerId));
   }

   public int createMarriage(Connection connection, int spouse1, int spouse2) {
      String sql = "INSERT INTO marriages (husbandid, wifeid) VALUES (?, ?)";
      return insertAndReturnKey(connection, sql, ps -> {
         ps.setInt(1, spouse1);
         ps.setInt(2, spouse2);
      });
   }
}