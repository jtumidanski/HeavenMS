package client.database.administrator;

import java.sql.Connection;

import client.database.AbstractQueryExecutor;

public class StorageAdministrator extends AbstractQueryExecutor {
   private static StorageAdministrator instance;

   public static StorageAdministrator getInstance() {
      if (instance == null) {
         instance = new StorageAdministrator();
      }
      return instance;
   }

   private StorageAdministrator() {
   }

   public void create(Connection connection, int accountId, int worldId) {
      String sql = "INSERT INTO storages (accountid, world, slots, meso) VALUES (?, ?, 4, 0)";
      execute(connection, sql, ps -> {
         ps.setInt(1, accountId);
         ps.setInt(2, worldId);
      });
   }

   public void update(Connection connection, int storageId, int slots, int mesos) {
      String sql = "UPDATE storages SET slots = ?, meso = ? WHERE storageid = ?";
      execute(connection, sql, ps -> {
         ps.setInt(1, slots);
         ps.setInt(2, mesos);
         ps.setInt(3, storageId);
      });
   }
}