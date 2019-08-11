package client.database.provider;

import java.sql.Connection;
import java.util.Optional;

import client.database.AbstractQueryExecutor;
import client.database.data.AllianceData;

public class AllianceProvider extends AbstractQueryExecutor {
   private static AllianceProvider instance;

   public static AllianceProvider getInstance() {
      if (instance == null) {
         instance = new AllianceProvider();
      }
      return instance;
   }

   private AllianceProvider() {
   }

   public boolean allianceExists(Connection connection, String allianceName) {
      String sql = "SELECT name FROM alliance WHERE name = ?";
      Optional<Boolean> result = get(connection, sql, ps -> ps.setString(1, allianceName), rs -> {
         if (rs != null && rs.next()) {
            return Optional.of(true);
         }
         return Optional.of(false);
      });
      return result.orElse(false);
   }

   public Optional<AllianceData> getAllianceData(Connection connection, int allianceId) {
      String sql = "SELECT * FROM alliance WHERE id = ?";
      return get(connection, sql, ps -> ps.setInt(1, allianceId), rs -> {
         if (rs != null && rs.next()) {
            return Optional.of(new AllianceData(rs.getInt("capacity"), rs.getString("name"),
                  rs.getString("notice"), rs.getString("rank1"), rs.getString("rank2"),
                  rs.getString("rank3"), rs.getString("rank4"), rs.getString("rank5")));
         }
         return Optional.empty();
      });
   }
}