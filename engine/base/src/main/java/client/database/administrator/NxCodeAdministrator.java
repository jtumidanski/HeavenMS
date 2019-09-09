package client.database.administrator;

import java.sql.Connection;

import client.database.AbstractQueryExecutor;

public class NxCodeAdministrator extends AbstractQueryExecutor {
   private static NxCodeAdministrator instance;

   public static NxCodeAdministrator getInstance() {
      if (instance == null) {
         instance = new NxCodeAdministrator();
      }
      return instance;
   }

   private NxCodeAdministrator() {
   }

   public void setRetriever(Connection connection, int codeId, String name) {
      String sql = "UPDATE nxcode SET retriever = ? WHERE code = ?";
      execute(connection, sql, ps -> {
         ps.setString(1, name);
         ps.setInt(2, codeId);
      });
   }

   public void deleteExpired(Connection connection, long time) {
      String sql = "DELETE FROM nxcode WHERE expiration <= ?";
      execute(connection, sql, ps -> ps.setLong(1, time));
   }
}