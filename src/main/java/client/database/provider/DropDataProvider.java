package client.database.provider;

import java.sql.Connection;
import java.util.List;

import client.database.AbstractQueryExecutor;
import client.database.utility.MonsterDropEntryTransformer;
import client.database.utility.MonsterGlobalDropEntryTransformer;
import server.life.MonsterDropEntry;
import server.life.MonsterGlobalDropEntry;

public class DropDataProvider extends AbstractQueryExecutor {
   private static DropDataProvider instance;

   public static DropDataProvider getInstance() {
      if (instance == null) {
         instance = new DropDataProvider();
      }
      return instance;
   }

   private DropDataProvider() {
   }

   public List<Integer> getMonstersWhoDrop(Connection connection, int itemId) {
      String sql = "SELECT dropperid FROM drop_data WHERE itemid = ? LIMIT 50";
      return getListNew(connection, sql, ps -> ps.setInt(1, itemId), rs -> rs.getInt("dropperid"));
   }

   public List<MonsterDropEntry> getDropDataForMonster(Connection connection, int monsterId) {
      String sql = "SELECT itemid, chance, minimum_quantity, maximum_quantity, questid FROM drop_data WHERE dropperid = ?";
      MonsterDropEntryTransformer transformer = new MonsterDropEntryTransformer();
      return getListNew(connection, sql, ps -> ps.setInt(1, monsterId), transformer::transform);
   }

   public List<MonsterGlobalDropEntry> getGlobalDropData(Connection connection) {
      String sql = "SELECT * FROM drop_data_global WHERE chance > 0";
      MonsterGlobalDropEntryTransformer transformer = new MonsterGlobalDropEntryTransformer();
      return getListNew(connection, sql, transformer::transform);
   }
}