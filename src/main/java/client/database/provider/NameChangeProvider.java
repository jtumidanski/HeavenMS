package client.database.provider;

import java.sql.Connection;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

import client.database.AbstractQueryExecutor;
import client.database.data.PendingNameChanges;

public class NameChangeProvider extends AbstractQueryExecutor {
   private static NameChangeProvider instance;

   public static NameChangeProvider getInstance() {
      if (instance == null) {
         instance = new NameChangeProvider();
      }
      return instance;
   }

   private NameChangeProvider() {
   }

   public Optional<Timestamp> getCompletionTimeByCharacterId(Connection connection, int characterId) {
      String sql = "SELECT completionTime FROM namechanges WHERE characterid=?";
      return getNew(connection, sql,
            ps -> ps.setInt(1, characterId),
            rs -> rs.getTimestamp("completionTime"));
   }

   public List<PendingNameChanges> getPendingNameChanges(Connection connection) {
      String sql = "SELECT * FROM namechanges WHERE completionTime IS NULL";
      return getListNew(connection, sql, rs -> new PendingNameChanges(
            rs.getInt("id"),
            rs.getInt("characterId"),
            rs.getString("old"),
            rs.getString("new")
      ));
   }

   public Optional<PendingNameChanges> getPendingNameChangeForCharacter(Connection connection, int characterId) {
      String sql = "SELECT * FROM namechanges WHERE characterid = ? AND completionTime IS NULL";
      return getNew(connection, sql, ps -> ps.setInt(1, characterId) , rs -> new PendingNameChanges(
            rs.getInt("id"),
            rs.getInt("characterId"),
            rs.getString("old"),
            rs.getString("new")
      ));
   }
}