package client.database.administrator;

import java.sql.Connection;
import java.sql.Timestamp;
import java.util.List;

import client.database.AbstractQueryExecutor;
import tools.Pair;

public class FredStorageAdministrator extends AbstractQueryExecutor {
   private static FredStorageAdministrator instance;

   public static FredStorageAdministrator getInstance() {
      if (instance == null) {
         instance = new FredStorageAdministrator();
      }
      return instance;
   }

   private FredStorageAdministrator() {
   }

   public void deleteForCharacter(Connection connection, int characterId) {
      String sql = "DELETE FROM `fredstorage` WHERE `cid` = ?";
      execute(connection, sql, ps -> ps.setInt(1, characterId));
   }

   public void deleteForCharacterBatch(Connection connection, List<Integer> characterIds) {
      String sql = "DELETE FROM `fredstorage` WHERE `cid` = ?";
      batch(connection, sql, (ps, data) -> ps.setInt(1, data), characterIds);
   }

   public void create(Connection connection, int characterId) {
      String sql = "INSERT INTO `fredstorage` (`cid`, `daynotes`, `timestamp`) VALUES (?, 0, ?)";
      execute(connection, sql, ps -> {
         ps.setInt(1, characterId);
         ps.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
      });
   }

   public void updateNotesBatch(Connection connection, List<Pair<Integer, Integer>> data) {
      String sql = "UPDATE `fredstorage` SET `daynotes` = ? WHERE `cid` = ?";
      batch(connection, sql, (ps, dataPoint) -> {
         ps.setInt(1, dataPoint.getLeft());
         ps.setInt(2, dataPoint.getRight());
      }, data);
   }
}