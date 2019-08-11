package client.database.utility;

import java.sql.ResultSet;
import java.sql.SQLException;

import client.database.data.PlayerLifeData;

public class PlayerLifeTransformer implements SqlTransformer<PlayerLifeData, ResultSet> {
   @Override
   public PlayerLifeData transform(ResultSet resultSet) throws SQLException {
      return new PlayerLifeData(
            resultSet.getInt("life"),
            resultSet.getString("type"),
            resultSet.getInt("cy"),
            resultSet.getInt("f"),
            resultSet.getInt("fh"),
            resultSet.getInt("rx0"),
            resultSet.getInt("rx1"),
            resultSet.getInt("x"),
            resultSet.getInt("y"),
            resultSet.getInt("hide"),
            resultSet.getInt("mobtime"),
            resultSet.getInt("team")
      );
   }
}
