package client.database.utility;

import java.sql.ResultSet;
import java.sql.SQLException;

import client.database.data.FrederickStorageData;

public class FredStorageDataTransformer implements SqlTransformer<FrederickStorageData, ResultSet> {
   @Override
   public FrederickStorageData transform(ResultSet resultSet) throws SQLException {
      return new FrederickStorageData(resultSet.getInt("cid"),
            resultSet.getString("name"),
            resultSet.getInt("world"),
            resultSet.getTimestamp("timestamp"),
            resultSet.getInt("daynotes"),
            resultSet.getTimestamp("lastLogoutTime"));
   }
}
