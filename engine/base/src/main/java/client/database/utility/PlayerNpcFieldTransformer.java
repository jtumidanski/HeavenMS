package client.database.utility;

import java.sql.ResultSet;
import java.sql.SQLException;

import client.database.data.PlayerNpcFieldData;

public class PlayerNpcFieldTransformer implements SqlTransformer<PlayerNpcFieldData, ResultSet> {
   @Override
   public PlayerNpcFieldData transform(ResultSet resultSet) throws SQLException {
      return new PlayerNpcFieldData(resultSet.getInt("world"),
            resultSet.getInt("map"),
            resultSet.getInt("step"),
            resultSet.getInt("podium"));
   }
}
