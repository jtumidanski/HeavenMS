package client.database.provider;

import java.sql.Connection;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

import client.database.AbstractQueryExecutor;
import client.database.data.FrederickStorageData;
import client.database.utility.FredStorageDataTransformer;
import client.processor.FredrickProcessor;

public class FredStorageProvider extends AbstractQueryExecutor {
   private static FredStorageProvider instance;

   public static FredStorageProvider getInstance() {
      if (instance == null) {
         instance = new FredStorageProvider();
      }
      return instance;
   }

   private FredStorageProvider() {
   }

   public int get(Connection connection, int characterId) {
      String sql = "SELECT `timestamp` FROM `fredstorage` WHERE `cid` = ?";
      Optional<Timestamp> result = getSingle(connection, sql, ps -> ps.setInt(1, characterId), "timestamp");
      return result.map(timestamp -> FredrickProcessor.timestampElapsedDays(timestamp, System.currentTimeMillis())).orElse(0);
   }

   public List<FrederickStorageData> get(Connection connection) {
      String sql = "SELECT * FROM fredstorage f LEFT JOIN (SELECT id, name, world, lastLogoutTime FROM characters) AS c ON c.id = f.cid";
      FredStorageDataTransformer transformer = new FredStorageDataTransformer();
      return getListNew(connection, sql, transformer::transform);
   }
}