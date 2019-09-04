package client.database.provider;

import java.sql.Connection;
import java.util.List;

import client.database.AbstractQueryExecutor;
import client.database.data.PlayerDiseaseData;
import client.database.utility.PlayerDiseaseTransformer;

public class PlayerDiseaseProvider extends AbstractQueryExecutor {
   private static PlayerDiseaseProvider instance;

   public static PlayerDiseaseProvider getInstance() {
      if (instance == null) {
         instance = new PlayerDiseaseProvider();
      }
      return instance;
   }

   private PlayerDiseaseProvider() {
   }

   public List<PlayerDiseaseData> getForCharacter(Connection connection, int characterId) {
      String sql = "SELECT * FROM playerdiseases WHERE charid = ?";
      PlayerDiseaseTransformer transformer = new PlayerDiseaseTransformer();
      return getListNew(connection, sql, ps -> ps.setInt(1, characterId), transformer::transform);
   }
}