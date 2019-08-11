package client.database.provider;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import client.BuddylistEntry;
import client.CharacterNameAndId;
import client.database.AbstractQueryExecutor;

public class BuddyProvider extends AbstractQueryExecutor {
   private static BuddyProvider instance;

   public static BuddyProvider getInstance() {
      if (instance == null) {
         instance = new BuddyProvider();
      }
      return instance;
   }

   private BuddyProvider() {
   }

   public List<Integer> getBuddies(Connection connection, int characterId) {
      String sql = "SELECT buddyid FROM buddies WHERE characterid = ?";
      return getListNew(connection, sql, ps -> ps.setInt(1, characterId), rs -> rs.getInt("buddyid"));
   }

   public List<BuddylistEntry> getInfoForBuddies(Connection connection, int characterId) {
      String sql = "SELECT b.buddyid, b.pending, b.group, c.name as buddyname FROM buddies as b, characters as c WHERE c.id = b.buddyid AND b.characterid = ? AND pending != 1";
      return getListNew(connection, sql, ps -> ps.setInt(1, characterId),
            rs -> new BuddylistEntry(rs.getString("buddyname"), rs.getString("group"), rs.getInt("buddyid"), (byte) -1, true));
   }

   public List<CharacterNameAndId> getInfoForPendingBuddies(Connection connection, int characterId) {
      String sql = "SELECT b.buddyid, b.pending, b.group, c.name as buddyname FROM buddies as b, characters as c WHERE c.id = b.buddyid AND b.characterid = ? AND pending = 1";
      return getListNew(connection, sql, ps -> ps.setInt(1, characterId),
            rs -> new CharacterNameAndId(rs.getInt("buddyid"), rs.getString("buddyname")));
   }

   public long getBuddyCount(Connection connection, int characterId) {
      String sql = "SELECT COUNT(*) as buddyCount FROM buddies WHERE characterid = ? AND pending = 0";
      Optional<Long> result = getSingle(connection, sql, ps -> ps.setInt(1, characterId), "buddyCount");
      return result.orElse(0L);
   }

   public boolean buddyIsPending(Connection connection, int characterId, int buddyId) {
      String sql = "SELECT pending FROM buddies WHERE characterid = ? AND buddyid = ?";
      Optional<Boolean> result = getNew(connection, sql, ps -> {
         ps.setInt(1, characterId);
         ps.setInt(2, buddyId);
      }, rs -> true);
      return result.orElse(false);
   }
}
