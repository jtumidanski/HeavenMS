package client.database.administrator;

import java.sql.Connection;
import java.util.Collection;
import java.util.List;

import client.BuddylistEntry;
import client.database.AbstractQueryExecutor;
import client.database.DeleteForCharacter;

public class BuddyAdministrator extends AbstractQueryExecutor implements DeleteForCharacter {
   private static BuddyAdministrator instance;

   public static BuddyAdministrator getInstance() {
      if (instance == null) {
         instance = new BuddyAdministrator();
      }
      return instance;
   }

   private BuddyAdministrator() {
   }

   @Override
   public void deleteForCharacter(Connection connection, int characterId) {
      String sql = "DELETE FROM buddies WHERE characterid = ?";
      execute(connection, sql, ps -> ps.setInt(1, characterId));
   }

   public void deleteNotPendingForCharacter(Connection connection, int characterId) {
      String sql = "DELETE FROM buddies WHERE characterid = ? AND pending = 0";
      execute(connection, sql, ps -> ps.setInt(1, characterId));
   }

   public void deletePendingForCharacter(Connection connection, int characterId) {
      String sql = "DELETE FROM buddies WHERE pending = 1 AND characterid = ?";
      execute(connection, sql, ps -> ps.setInt(1, characterId));
   }

   public void addBuddy(Connection connection, int characterId, int buddyId) {
      String sql = "INSERT INTO buddies (characterid, `buddyid`, `pending`) VALUES (?, ?, 1)";
      execute(connection, sql, ps -> {
         ps.setInt(1, characterId);
         ps.setInt(2, buddyId);
      });
   }

   public void addBuddies(Connection connection, int characterId, Collection<BuddylistEntry> buddies) {
      String sql = "INSERT INTO buddies (characterid, `buddyid`, `pending`, `group`) VALUES (?, ?, 0, ?)";
      batch(connection, sql, (ps, data) -> {
         ps.setInt(1, characterId);
         ps.setInt(2, data.getCharacterId());
         ps.setString(3, data.getGroup());
      }, buddies);
   }
}
