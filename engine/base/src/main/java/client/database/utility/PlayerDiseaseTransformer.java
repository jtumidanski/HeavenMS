package client.database.utility;

import java.sql.ResultSet;
import java.sql.SQLException;

import client.database.data.PlayerDiseaseData;

public class PlayerDiseaseTransformer implements SqlTransformer<PlayerDiseaseData, ResultSet> {
   @Override
   public PlayerDiseaseData transform(ResultSet resultSet) throws SQLException {
      return new PlayerDiseaseData(resultSet.getInt("disease"),
            resultSet.getInt("mobskillid"),
            resultSet.getInt("mobskilllv"),
            resultSet.getInt("length"));
   }
}
