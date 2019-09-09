package client.database.provider;

import java.sql.Connection;
import java.util.Optional;

import client.database.AbstractQueryExecutor;
import client.database.data.MarriageData;
import client.database.utility.MarriageTransformer;

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
      MarriageTransformer transformer = new MarriageTransformer();
      return getNew(connection, sql, ps -> ps.setInt(1, marriageId), transformer::transform);
   }

   public Optional<MarriageData> getBySpouses(Connection connection, int spouse1, int spouse2) {
      String sql = "SELECT * FROM marriages WHERE husbandid = ? OR wifeid = ?";
      MarriageTransformer transformer = new MarriageTransformer();
      return getNew(connection, sql, ps -> {
         ps.setInt(1, spouse1);
         ps.setInt(2, spouse2);
      }, transformer::transform);
   }
}