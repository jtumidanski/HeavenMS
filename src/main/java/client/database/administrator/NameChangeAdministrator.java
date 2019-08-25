package client.database.administrator;

import java.sql.Connection;
import java.sql.Timestamp;

import client.database.AbstractQueryExecutor;

public class NameChangeAdministrator extends AbstractQueryExecutor {
   private static NameChangeAdministrator instance;

   public static NameChangeAdministrator getInstance() {
      if (instance == null) {
         instance = new NameChangeAdministrator();
      }
      return instance;
   }

   private NameChangeAdministrator() {
   }

   public void markCompleted(Connection connection, int id) {
      String sql = "UPDATE namechanges SET completionTime = ? WHERE id = ?";
      execute(connection, sql, ps -> {
         ps.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
         ps.setInt(2, id);
      });
   }

   public void cancelPendingNameChange(Connection connection, int characterId) {
      String sql = "DELETE FROM namechanges WHERE characterid=? AND completionTime IS NULL";
      execute(connection, sql, ps -> ps.setInt(1, characterId));
   }

   public void create(Connection connection, int characterId, String oldName, String newName) {
      String sql = "INSERT INTO namechanges (characterid, old, new) VALUES (?, ?, ?)";
      execute(connection, sql, ps -> {
         ps.setInt(1, characterId);
         ps.setString(2, oldName);
         ps.setString(3, newName);
      });
   }
}