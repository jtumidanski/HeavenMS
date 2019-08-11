package client.database.provider;

import java.sql.Connection;
import java.util.Optional;

import client.database.AbstractQueryExecutor;
import server.MapleStorage;

public class StorageProvider extends AbstractQueryExecutor {
   private static StorageProvider instance;

   public static StorageProvider getInstance() {
      if (instance == null) {
         instance = new StorageProvider();
      }
      return instance;
   }

   private StorageProvider() {
   }

   public Optional<MapleStorage> getByAccountAndWorld(Connection connection, int accountId, int worldId) {
      String sql = "SELECT storageid, slots, meso FROM storages WHERE accountid = ? AND world = ?";
      return get(connection, sql, ps -> {
         ps.setInt(1, accountId);
         ps.setInt(2, worldId);
      }, rs -> {
         if (rs != null && rs.next()) {
            return Optional.of(new MapleStorage(rs.getInt("storageid"), (byte) rs.getInt("slots"), rs.getInt("meso")));
         }
         return Optional.empty();
      });
   }
}