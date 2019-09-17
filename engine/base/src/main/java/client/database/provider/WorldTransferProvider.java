package client.database.provider;

import java.sql.Connection;
import java.sql.Timestamp;
import java.util.List;

import client.database.AbstractQueryExecutor;
import client.database.data.PendingWorldTransfers;

public class WorldTransferProvider extends AbstractQueryExecutor {
   private static WorldTransferProvider instance;

   public static WorldTransferProvider getInstance() {
      if (instance == null) {
         instance = new WorldTransferProvider();
      }
      return instance;
   }

   private WorldTransferProvider() {
   }

   public Timestamp getCompletionTimeByCharacterId(Connection connection, int characterId) {
      String sql = "SELECT completionTime FROM worldtransfers WHERE characterid=?";
      return getNew(connection, sql,
            ps -> ps.setInt(1, characterId),
            rs -> rs.getTimestamp("completionTime")).get();
   }

   public List<PendingWorldTransfers> getPendingTransfers(Connection connection) {
      String sql = "SELECT * FROM worldtransfers WHERE completionTime IS NULL";
      return getListNew(connection, sql, rs -> new PendingWorldTransfers(
            rs.getInt("id"),
            rs.getInt("characterId"),
            rs.getInt("from"),
            rs.getInt("to")
      ));
   }

   public int countOutstandingWorldTransfers(Connection connection, int characterId) {
      String sql = "SELECT COUNT(*) as rowcount FROM worldtransfers WHERE `characterid` = ? AND completionTime IS NULL";
      return getNew(connection, sql, ps -> ps.setInt(1, characterId), rs -> rs.getInt("rowcount")).orElse(0);
   }
}