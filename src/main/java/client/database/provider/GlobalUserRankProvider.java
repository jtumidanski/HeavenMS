package client.database.provider;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import client.database.AbstractQueryExecutor;
import client.database.data.CharacterData;
import client.database.data.GlobalUserRank;
import client.database.data.WorldRankData;
import constants.ServerConstants;
import tools.Pair;

public class GlobalUserRankProvider extends AbstractQueryExecutor {
   private static GlobalUserRankProvider instance;

   public static GlobalUserRankProvider getInstance() {
      if (instance == null) {
         instance = new GlobalUserRankProvider();
      }
      return instance;
   }

   private GlobalUserRankProvider() {
   }

   public List<WorldRankData> getWorldRanks(Connection connection, int worldId) {
      String sql = "SELECT `characters`.`name`, `characters`.`level`, `characters`.`world` FROM `characters` LEFT JOIN accounts ON accounts.id = characters.accountid WHERE `characters`.`gm` < 2 AND `accounts`.`banned` = '0' AND `characters`.`world` = ? ORDER BY world, level DESC, exp DESC, lastExpGainTime ASC LIMIT 50";
      List<CharacterData> characterDataList = getListNew(connection, sql, ps -> ps.setInt(1, worldId), rs -> {
         CharacterData characterData = new CharacterData();
         characterData.setWorld(rs.getInt("world"));
         characterData.setName(rs.getString("name"));
         characterData.setLevel(rs.getInt("level"));
         return characterData;
      });

      List<WorldRankData> rankSystem = new ArrayList<>();
      int currentWorld = -1;
      for (CharacterData characterData : characterDataList) {
         if (currentWorld < characterData.getWorld()) {
            currentWorld = characterData.getWorld();
            rankSystem.add(new WorldRankData(characterData.getWorld()));
         }
         rankSystem.get(characterData.getWorld()).addUserRank(new GlobalUserRank(characterData.getName(), characterData.getLevel()));
      }
      return rankSystem;
   }

   public List<WorldRankData> getWorldRanksRange(Connection connection, int worldId) {
      String sql = "SELECT `characters`.`name`, `characters`.`level`, `characters`.`world` FROM `characters` LEFT JOIN accounts ON accounts.id = characters.accountid WHERE `characters`.`gm` < 2 AND `accounts`.`banned` = '0' AND `characters`.`world` >= 0 AND `characters`.`world` <= ? ORDER BY world, level DESC, exp DESC, lastExpGainTime ASC LIMIT 50";
      List<CharacterData> characterDataList = getListNew(connection, sql, ps -> ps.setInt(1, worldId), rs -> {
         CharacterData characterData = new CharacterData();
         characterData.setWorld(rs.getInt("world"));
         characterData.setName(rs.getString("name"));
         characterData.setLevel(rs.getInt("level"));
         return characterData;
      });

      List<WorldRankData> rankSystem = new ArrayList<>();
      int currentWorld = -1;
      for (CharacterData characterData : characterDataList) {
         if (currentWorld < characterData.getWorld()) {
            currentWorld = characterData.getWorld();
            rankSystem.add(new WorldRankData(characterData.getWorld()));
         }
         rankSystem.get(characterData.getWorld()).addUserRank(new GlobalUserRank(characterData.getName(), characterData.getLevel()));
      }
      return rankSystem;
   }

   public List<WorldRankData> getRanksWholeServer(Connection connection, int worldId) {
      String sql = "SELECT `characters`.`name`, `characters`.`level`, `characters`.`world` FROM `characters` LEFT JOIN accounts ON accounts.id = characters.accountid WHERE `characters`.`gm` < 2 AND `accounts`.`banned` = '0' AND `characters`.`world` >= 0 AND `characters`.`world` <= ? ORDER BY level DESC, exp DESC, lastExpGainTime ASC LIMIT 50";
      List<CharacterData> characterDataList = getListNew(connection, sql, ps -> ps.setInt(1, Math.abs(worldId)), rs -> {
         CharacterData characterData = new CharacterData();
         characterData.setWorld(rs.getInt("world"));
         characterData.setName(rs.getString("name"));
         characterData.setLevel(rs.getInt("level"));
         return characterData;
      });

      List<WorldRankData> rankSystem = new ArrayList<>();
      rankSystem.add(new WorldRankData(0));

      for (CharacterData characterData : characterDataList) {
         rankSystem.get(characterData.getWorld()).addUserRank(new GlobalUserRank(characterData.getName(), characterData.getLevel()));
      }
      return rankSystem;
   }
}