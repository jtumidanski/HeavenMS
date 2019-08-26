package client.database.administrator;

import java.sql.Connection;
import java.sql.Timestamp;

import client.database.AbstractQueryExecutor;

public class DueyPackageAdministrator extends AbstractQueryExecutor {
   private static DueyPackageAdministrator instance;

   public static DueyPackageAdministrator getInstance() {
      if (instance == null) {
         instance = new DueyPackageAdministrator();
      }
      return instance;
   }

   private DueyPackageAdministrator() {
   }

   public void uncheck(Connection connection, int characterId) {
      String sql = "UPDATE dueypackages SET Checked = 0 WHERE ReceiverId = ?";
      execute(connection, sql, ps -> ps.setInt(1, characterId));
   }

   public void removePackage(Connection connection, int packageId) {
      String sql = "DELETE FROM dueypackages WHERE PackageId = ?";
      execute(connection, sql, ps -> ps.setInt(1, packageId));
   }

   public void deletePackagesAfter(Connection connection, Timestamp timestamp) {
      String sql = "DELETE FROM dueypackages WHERE `TimeStamp` < ?";
      execute(connection, sql, ps -> ps.setTimestamp(1, timestamp));
   }

   public int create(Connection connection, int receipientId, String senderName, int mesos, String message, boolean quick) {
      String sql = "INSERT INTO `dueypackages` (ReceiverId, SenderName, Mesos, TimeStamp, Message, Type, Checked) VALUES (?, ?, ?, ?, ?, ?, 1)";

      Timestamp timestamp = new Timestamp(System.currentTimeMillis());

      return insertAndReturnKey(connection, sql, ps -> {
         ps.setInt(1, receipientId);
         ps.setString(2, senderName);
         ps.setInt(3, mesos);
         ps.setTimestamp(4, timestamp);
         ps.setString(5, message);
         ps.setInt(6, quick ? 1 : 0);
      });
   }
}