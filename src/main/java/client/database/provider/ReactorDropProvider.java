package client.database.provider;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import client.database.AbstractQueryExecutor;
import server.maps.ReactorDropEntry;

public class ReactorDropProvider extends AbstractQueryExecutor {
   private static ReactorDropProvider instance;

   public static ReactorDropProvider getInstance() {
      if (instance == null) {
         instance = new ReactorDropProvider();
      }
      return instance;
   }

   private ReactorDropProvider() {
   }

   public List<Integer> getDropIds(Connection connection, int minimumId, int maximumId) {
      String sql = "SELECT itemid FROM reactordrops WHERE itemid >= ? AND itemid < ?;";
      return getList(connection, sql, ps -> {
         ps.setInt(1, minimumId);
         ps.setInt(2, maximumId);
      }, rs -> {
         List<Integer> result = new ArrayList<>();
         while (rs != null && rs.next()) {
            result.add(rs.getInt("itemid"));
         }
         return result;
      });
   }

   public List<ReactorDropEntry> getDropsForReactor(Connection connection, int reactorId) {
      String sql = "SELECT itemid, chance, questid FROM reactordrops WHERE reactorid = ? AND chance >= 0";
      return getList(connection, sql, ps -> ps.setInt(1, reactorId), rs -> {
         List<ReactorDropEntry> dropEntries = new ArrayList<>();
         while (rs != null && rs.next()) {
            dropEntries.add(new ReactorDropEntry(rs.getInt("itemid"), rs.getInt("chance"), rs.getInt("questid")));
         }
         return dropEntries;
      });
   }
}