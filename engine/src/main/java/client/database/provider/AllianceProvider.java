package client.database.provider;

import java.sql.Connection;
import java.util.Optional;

import client.database.AbstractQueryExecutor;
import client.database.data.AllianceData;
import client.database.utility.AllianceTransformer;

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
      Optional<Boolean> result = getNew(connection, sql, ps -> ps.setString(1, allianceName), rs -> true);
      return result.orElse(false);
   }

   public Optional<AllianceData> getAllianceData(Connection connection, int allianceId) {
      String sql = "SELECT * FROM alliance WHERE id = ?";
      AllianceTransformer transformer = new AllianceTransformer();
      return getNew(connection, sql, ps -> ps.setInt(1, allianceId), transformer::transform);
   }
}