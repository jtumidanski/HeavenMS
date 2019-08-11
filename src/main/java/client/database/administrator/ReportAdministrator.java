package client.database.administrator;

import java.sql.Connection;

import client.database.AbstractQueryExecutor;

public class ReportAdministrator extends AbstractQueryExecutor {
   private static ReportAdministrator instance;

   public static ReportAdministrator getInstance() {
      if (instance == null) {
         instance = new ReportAdministrator();
      }
      return instance;
   }

   private ReportAdministrator() {
   }

   public void create(Connection connection, String reportTime, int reporterId, int victimId, int reason,
                      String chatLog, String description) {
      String sql = "INSERT INTO reports (`reporttime`, `reporterid`, `victimid`, `reason`, `chatlog`, `description`) VALUES (?, ?, ?, ?, ?, ?)";
      execute(connection, sql, ps -> {
         ps.setString(1, reportTime);
         ps.setInt(2, reporterId);
         ps.setInt(3, victimId);
         ps.setInt(4, reason);
         ps.setString(5, chatLog);
         ps.setString(6, description);
      });
   }
}