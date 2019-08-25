package client.database.provider;

import java.sql.Connection;
import java.util.List;
import java.util.Optional;

import client.database.AbstractQueryExecutor;
import client.database.data.CharNameAndIdData;
import client.database.data.CharacterData;
import client.database.data.CharacterGuildData;
import client.database.data.CharacterIdNameAccountId;
import client.database.data.CharacterRankData;
import client.database.utility.CharNameAndIdTransformer;
import client.database.utility.CharacterFromResultSetTransformer;
import client.database.utility.CharacterGuildTransformer;
import client.database.utility.CharacterIdNameAccountIdTransformer;
import client.database.utility.CharacterJobRankTransformer;
import client.database.utility.CharacterRankTransformer;
import tools.Pair;

public class CharacterProvider extends AbstractQueryExecutor {
   private static CharacterProvider instance;

   public static CharacterProvider getInstance() {
      if (instance == null) {
         instance = new CharacterProvider();
      }
      return instance;
   }

   private CharacterProvider() {
   }

   public Optional<Integer> getCharacterForNameAndWorld(Connection connection, String name, int worldId) {
      String sql = "SELECT id FROM characters WHERE name LIKE ? AND world = ?";
      return getSingle(connection, sql, ps -> {
         ps.setString(1, name);
         ps.setInt(2, worldId);
      }, "id");
   }

   public List<CharNameAndIdData> getCharacterInfoForWorld(Connection connection, int accountId, int worldId) {
      String sql = "SELECT id, name FROM characters WHERE accountid = ? AND world = ?";
      return getListNew(connection, sql, ps -> {
         ps.setInt(1, accountId);
         ps.setInt(2, worldId);
      }, rs -> new CharNameAndIdData(rs.getString("name"), rs.getInt("id")));
   }

   public CharNameAndIdData getCharacterInfoForName(Connection connection, String name) {
      String sql = "SELECT id, name, buddyCapacity FROM characters WHERE name LIKE ?";
      CharNameAndIdTransformer transformer = new CharNameAndIdTransformer();
      Optional<CharNameAndIdData> result = getNew(connection, sql, ps -> ps.setString(1, name), transformer::transform);
      return result.orElse(null);
   }


   public int countReborns(Connection connection, int characterId) {
      String sql = "SELECT reborns FROM characters WHERE id=?;";
      Optional<Integer> result = getSingle(connection, sql, ps -> ps.setInt(1, characterId), 1);
      return result.orElse(0);
   }

   public int getWorldId(Connection connection, int characterId) {
      String sql = "SELECT world FROM characters WHERE id = ?";
      Optional<Integer> result = getSingle(connection, sql, ps -> ps.setInt(1, characterId), "world");
      return result.orElse(0);
   }

   public Optional<CharacterGuildData> getGuildDataForCharacter(Connection connection, int characterId, int accountId) {
      String sql = "SELECT id, guildid, guildrank, name, allianceRank, level, job FROM characters WHERE id = ? AND accountid = ?";
      CharacterGuildTransformer transformer = new CharacterGuildTransformer();
      return get(connection, sql, ps -> {
         ps.setInt(1, characterId);
         ps.setInt(2, accountId);
      }, rs -> {
         if (rs != null && rs.next() && rs.getInt("guildid") > 0) {
            return Optional.of(transformer.transform(rs));
         }
         return Optional.empty();
      });
   }


   public List<CharacterGuildData> getGuildCharacterData(Connection connection, int guildId) {
      String sql = "SELECT id, guildid, guildrank, name, allianceRank, level, job FROM characters WHERE guildid = ? ORDER BY guildrank ASC, name ASC";
      CharacterGuildTransformer transformer = new CharacterGuildTransformer();
      return getListNew(connection, sql, ps -> ps.setInt(1, guildId), transformer::transform);
   }

   public Optional<Pair<Integer, Integer>> getIdAndAccountIdForName(Connection connection, String name) {
      String sql = "SELECT id, accountid FROM characters WHERE name = ?";
      return get(connection, sql, ps -> ps.setString(1, name),
            rs -> Optional.of(new Pair<>(rs.getInt(2), rs.getInt(1))));
   }

   public int getAccountIdForName(Connection connection, String name) {
      String sql = "SELECT accountid FROM characters WHERE name = ?";
      Optional<Integer> result = getSingle(connection, sql, ps -> ps.setString(1, name), 1);
      return result.orElse(-1);
   }

   public int getAccountIdForCharacterId(Connection connection, int characterId) {
      String sql = "SELECT accountid FROM characters WHERE id = ?";
      Optional<Integer> result = getSingle(connection, sql, ps -> ps.setInt(1, characterId), 1);
      return result.orElse(-1);
   }

   public int getIdForName(Connection connection, String name) {
      String sql = "SELECT id FROM characters WHERE name = ?";
      Optional<Integer> result = getSingle(connection, sql, ps -> ps.setString(1, name), 1);
      return result.orElse(-1);
   }

   public String getNameForId(Connection connection, int characterId) {
      String sql = "SELECT name FROM characters WHERE id = ?";
      Optional<String> result = getSingle(connection, sql, ps -> ps.setInt(1, characterId), 1);
      return result.orElse(null);
   }

   public long getMerchantMesos(Connection connection, int characterId) {
      String sql = "SELECT MerchantMesos FROM characters WHERE id = ?";
      Optional<Long> merchantMesos = getSingle(connection, sql, ps -> ps.setInt(1, characterId), "MerchantMesos");
      return merchantMesos.orElse((long) 0);
   }

   public List<Integer> getCharacterLevels(Connection connection, int accountId) {
      String sql = "SELECT `level` FROM `characters` WHERE accountid = ?";
      return getListNew(connection, sql, ps -> ps.setInt(1, accountId), rs -> rs.getInt("level"));
   }

   public Optional<Integer> getGmLevel(Connection connection, String name) {
      String sql = "SELECT gm FROM characters WHERE name = ?";
      return getSingle(connection, sql, ps -> ps.setString(1, name), "gm");
   }

   public Optional<Integer> getMarriageItem(Connection connection, int characterId) {
      String sql = "SELECT marriageItemId FROM characters WHERE id=?";
      return getSingle(connection, sql, ps -> ps.setInt(1, characterId), "marriageItemId");
   }

   public Optional<CharacterIdNameAccountId> getByName(Connection connection, String name) {
      String sql = "SELECT `id`, `accountid`, `name` FROM `characters` WHERE `name` = ?";
      CharacterIdNameAccountIdTransformer transformer = new CharacterIdNameAccountIdTransformer();
      return getNew(connection, sql, ps -> ps.setString(1, name), transformer::transform);
   }

   public List<CharacterRankData> getRankByJob(Connection connection, int worldId, int jobId) {
      String sql = "SELECT c.id, c.jobRank, c.jobRankMove, a.lastlogin AS lastlogin, a.loggedin FROM characters AS c LEFT JOIN accounts AS a ON c.accountid = a.id WHERE c.gm < 2 AND c.world = ? AND c.job DIV 100 = ? ORDER BY c.level DESC , c.exp DESC , c.lastExpGainTime ASC, c.fame DESC , c.meso DESC";
      CharacterJobRankTransformer transformer = new CharacterJobRankTransformer();
      return getListNew(connection, sql, ps -> {
         ps.setInt(1, worldId);
         ps.setInt(2, jobId);
      }, transformer::transform);
   }

   public List<CharacterRankData> getRank(Connection connection, int worldId) {
      String sql = "SELECT c.id, c.rank, c.rankMove, a.lastlogin AS lastlogin, a.loggedin FROM characters AS c LEFT JOIN accounts AS a ON c.accountid = a.id WHERE c.gm < 2 AND c.world = ? ORDER BY c.level DESC , c.exp DESC , c.lastExpGainTime ASC, c.fame DESC , c.meso DESC";
      CharacterRankTransformer transformer = new CharacterRankTransformer();
      return getListNew(connection, sql, ps -> ps.setInt(1, worldId), transformer::transform);
   }

   public Optional<CharacterData> getById(Connection connection, int characterId) {
      String sql = "SELECT * FROM characters WHERE id = ?";
      CharacterFromResultSetTransformer transformer = new CharacterFromResultSetTransformer();
      return getNew(connection, sql, ps -> ps.setInt(1, characterId), transformer::transform);
   }

   public List<CharacterData> getByAccountId(Connection connection, int accountId) {
      String sql = "SELECT * FROM characters WHERE accountid = ? ORDER BY world, id";
      CharacterFromResultSetTransformer transformer = new CharacterFromResultSetTransformer();
      return getListNew(connection, sql, ps -> ps.setInt(1, accountId), transformer::transform);
   }

   public Optional<CharacterData> getHighestLevelOtherCharacterData(Connection connection, int accountId, int characterId) {
      String sql = "SELECT name, level FROM characters WHERE accountid = ? AND id != ? ORDER BY level DESC limit 1";
      return getNew(connection, sql, ps -> {
         ps.setInt(1, accountId);
         ps.setInt(2, characterId);
      }, rs -> {
         CharacterData characterData = new CharacterData();
         characterData.setName(rs.getString("name"));
         characterData.setLevel(rs.getInt("level"));
         return characterData;
      });
   }

   public int getCharactersInWorld(Connection connection, int accountId, int worldId) {
      String sql = "SELECT COUNT(*) AS rowcount FROM characters WHERE accountid = ? AND world = ?";
      return getNew(connection, sql, ps -> {
         ps.setInt(1, accountId);
         ps.setInt(2, worldId);
      }, rs -> rs.getInt("rowcount")).orElse(0);
   }

   public int getMesosForCharacter(Connection connection, int characterId) {
      String sql = "SELECT meso FROM characters WHERE id = ?";
      return getNew(connection, sql, ps -> ps.setInt(1, characterId), rs -> rs.getInt("meso")).orElse(0);
   }
}
