package database.administrator;


import java.sql.Timestamp;
import javax.persistence.EntityManager;

import database.AbstractQueryExecutor;
import entity.Report;

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

   public void create(EntityManager entityManager, String reportTime, int reporterId, int victimId, int reason,
                      String chatLog, String description) {
      Report report = new Report();
      report.setReportTime(Timestamp.valueOf(reportTime));
      report.setReporterId(reporterId);
      report.setVictimId(victimId);
      report.setReason(reason);
      report.setChatLog(chatLog);
      report.setDescription(description);
      insert(entityManager, report);
   }
}