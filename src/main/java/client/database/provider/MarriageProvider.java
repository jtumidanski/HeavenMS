package client.database.provider;

import java.sql.Connection;
import java.util.Optional;

import client.database.AbstractQueryExecutor;
import client.database.data.MarriageData;

public class MarriageProvider extends AbstractQueryExecutor {
   private static MarriageProvider instance;

   public static MarriageProvider getInstance() {
      if (instance == null) {
         instance = new MarriageProvider();
      }
      return instance;
   }

   private MarriageProvider() {
   }

   public Optional<MarriageData> getById(Connection connection, int marriageId) {
      String sql = "SELECT * FROM marriages WHERE marriageid = ?";
      return get(connection, sql, ps -> ps.setInt(1, marriageId), rs -> {
         if (rs != null && rs.next()) {
            return Optional.of(new MarriageData(rs.getInt("marriageid"), rs.getInt("husbandid"), rs.getInt("wifeid")));
         }
         return Optional.empty();
      });
   }

   public Optional<MarriageData> getBySpouses(Connection connection, int spouse1, int spouse2) {
      String sql = "SELECT * FROM marriages WHERE husbandid = ? OR wifeid = ?";
      return get(connection, sql, ps -> {
         ps.setInt(1, spouse1);
         ps.setInt(2, spouse2);
      }, rs -> {
         if (rs != null && rs.next()) {
            return Optional.of(new MarriageData(rs.getInt("marriageid"), rs.getInt("husbandid"), rs.getInt("wifeid")));
         }
         return Optional.empty();
      });
   }
}