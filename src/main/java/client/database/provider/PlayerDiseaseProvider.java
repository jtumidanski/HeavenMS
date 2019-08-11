package client.database.provider;

import java.sql.Connection;
import java.util.List;

import client.database.AbstractQueryExecutor;
import client.database.data.PlayerDiseaseData;

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
      return getListNew(connection, sql, ps -> ps.setInt(1, characterId),
            rs -> new PlayerDiseaseData(rs.getInt("disease"), rs.getInt("mobskillid"), rs.getInt("mobskilllv"), rs.getInt("length")));
   }
}