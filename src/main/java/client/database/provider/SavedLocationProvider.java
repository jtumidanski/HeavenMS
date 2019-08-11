package client.database.provider;

import java.sql.Connection;
import java.util.List;

import client.database.AbstractQueryExecutor;
import client.database.data.SavedLocationData;
import client.database.utility.SavedLocationTransformer;

public class SavedLocationProvider extends AbstractQueryExecutor {
   private static SavedLocationProvider instance;

   public static SavedLocationProvider getInstance() {
      if (instance == null) {
         instance = new SavedLocationProvider();
      }
      return instance;
   }

   private SavedLocationProvider() {
   }

   public List<SavedLocationData> getForCharacter(Connection connection, int characterId) {
      String sql = "SELECT `locationtype`,`map`,`portal` FROM savedlocations WHERE characterid = ?";
      SavedLocationTransformer transformer = new SavedLocationTransformer();
      return getListNew(connection, sql, ps -> ps.setInt(1, characterId), transformer::transform);
   }
}