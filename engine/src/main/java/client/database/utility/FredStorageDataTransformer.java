package client.database.utility;

import java.sql.ResultSet;
import java.sql.SQLException;

import client.database.data.FredStorageData;

public class FredStorageDataTransformer implements SqlTransformer<FredStorageData, ResultSet> {
   @Override
   public FredStorageData transform(ResultSet resultSet) throws SQLException {
      return new FredStorageData(resultSet.getInt("cid"),
            resultSet.getString("name"),
            resultSet.getInt("world"),
            resultSet.getTimestamp("timestamp"),
            resultSet.getInt("daynotes"),
            resultSet.getTimestamp("lastLogoutTime"));
   }
}
