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
      return getList(connection, sql, ps -> ps.setInt(1, characterId), rs -> {
         List<Integer> buddies = new ArrayList<>();
         while (rs != null && rs.next()) {
            buddies.add(rs.getInt("buddyid"));
         }
         return buddies;
      });
   }

   public List<BuddylistEntry> getInfoForBuddies(Connection connection, int characterId) {
      String sql = "SELECT b.buddyid, b.pending, b.group, c.name as buddyname FROM buddies as b, characters as c WHERE c.id = b.buddyid AND b.characterid = ? AND pending != 1";
      return getList(connection, sql, ps -> ps.setInt(1, characterId), rs -> {
         List<BuddylistEntry> buddylistEntries = new ArrayList<>();
         while (rs != null && rs.next()) {
            buddylistEntries.add(new BuddylistEntry(rs.getString("buddyname"), rs.getString("group"), rs.getInt("buddyid"), (byte) -1, true));
         }
         return buddylistEntries;
      });
   }

   public List<CharacterNameAndId> getInfoForPendingBuddies(Connection connection, int characterId) {
      String sql = "SELECT b.buddyid, b.pending, b.group, c.name as buddyname FROM buddies as b, characters as c WHERE c.id = b.buddyid AND b.characterid = ? AND pending == 1";
      return getList(connection, sql, ps -> ps.setInt(1, characterId), rs -> {
         List<CharacterNameAndId> pendingBuddies = new ArrayList<>();
         while (rs != null && rs.next()) {
            pendingBuddies.add(new CharacterNameAndId(rs.getInt("buddyid"), rs.getString("buddyname")));
         }
         return pendingBuddies;
      });
   }

   public int getBuddyCount(Connection connection, int characterId) {
      String sql = "SELECT COUNT(*) as buddyCount FROM buddies WHERE characterid = ? AND pending = 0";
      Optional<Integer> result = getSingle(connection, sql, ps -> ps.setInt(1, characterId), "buddyCount");
      return result.orElse(0);
   }

   public boolean buddyIsPending(Connection connection, int characterId, int buddyId) {
      String sql = "SELECT pending FROM buddies WHERE characterid = ? AND buddyid = ?";
      Optional<Boolean> result = get(connection, sql, ps -> {
         ps.setInt(1, characterId);
         ps.setInt(2, buddyId);
      }, rs -> {
         if (rs == null) {
            return Optional.of(false);
         }
         if (rs.next()) {
            return Optional.of(true);
         }
         return Optional.of(false);
      });
      return result.orElse(false);
   }
}
