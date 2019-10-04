package client.database.administrator;

import java.sql.Connection;
import java.sql.Timestamp;

import client.database.AbstractQueryExecutor;

public class WorldTransferAdministrator extends AbstractQueryExecutor {
   private static WorldTransferAdministrator instance;

   public static WorldTransferAdministrator getInstance() {
      if (instance == null) {
         instance = new WorldTransferAdministrator();
      }
      return instance;
   }

   private WorldTransferAdministrator() {
   }

   public void create(Connection connection, int characterId, int oldWorld, int newWorld) {
      String sql = "INSERT INTO worldtransfers (characterid, `from`, `to`) VALUES (?, ?, ?)";
      execute(connection, sql, ps -> {
         ps.setInt(1, characterId);
         ps.setInt(2, oldWorld);
         ps.setInt(3, newWorld);
      });
   }

   public void cancelPendingForCharacter(Connection connection, int characterId) {
      String sql = "DELETE FROM worldtransfers WHERE characterid=? AND completionTime IS NULL";
      execute(connection, sql, ps -> ps.setInt(1, characterId));
   }

   public void markComplete(Connection connection, int transferId) {
      String sql = "UPDATE worldtransfers SET completionTime = ? WHERE id = ?";
      execute(connection, sql, ps -> {
         ps.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
         ps.setInt(2, transferId);
      });
   }

   public void cancelById(Connection connection, int transferId) {
      String sql = "DELETE FROM worldtransfers WHERE id = ?";
      execute(connection, sql, ps -> ps.setInt(1, transferId));
   }
}