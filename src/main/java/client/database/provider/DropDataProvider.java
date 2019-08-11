package client.database.provider;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import client.database.AbstractQueryExecutor;
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
      return getList(connection, sql, ps -> ps.setInt(1, itemId), rs -> {
         List<Integer> monsterIds = new ArrayList<>();
         while (rs != null && rs.next()) {
            monsterIds.add(rs.getInt("dropperid"));
         }
         return monsterIds;
      });
   }

   public List<MonsterDropEntry> getDropDataForMonster(Connection connection, int monsterId) {
      String sql = "SELECT itemid, chance, minimum_quantity, maximum_quantity, questid FROM drop_data WHERE dropperid = ?";
      return getList(connection, sql, ps -> ps.setInt(1, monsterId), rs -> {
         List<MonsterDropEntry> dropEntries = new ArrayList<>();
         while (rs != null && rs.next()) {
            dropEntries.add(new MonsterDropEntry(rs.getInt("itemid"), rs.getInt("chance"),
                  rs.getInt("minimum_quantity"), rs.getInt("maximum_quantity"),
                  rs.getShort("questid")));
         }
         return dropEntries;
      });
   }

   public List<MonsterGlobalDropEntry> getGlobalDropData(Connection connection) {
      String sql = "SELECT * FROM drop_data_global WHERE chance > 0";
      return getList(connection, sql, ps -> {
      }, rs -> {
         List<MonsterGlobalDropEntry> dropEntries = new ArrayList<>();
         while (rs != null && rs.next()) {
            dropEntries.add(new MonsterGlobalDropEntry(
                  rs.getInt("itemid"),
                  rs.getInt("chance"),
                  rs.getByte("continent"),
                  rs.getInt("minimum_quantity"),
                  rs.getInt("maximum_quantity"),
                  rs.getShort("questid")));
         }
         return dropEntries;
      });
   }
}