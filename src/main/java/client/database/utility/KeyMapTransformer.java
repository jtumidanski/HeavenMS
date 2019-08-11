package client.database.utility;

import java.sql.ResultSet;
import java.sql.SQLException;

import client.database.data.KeyMapData;

public class KeyMapTransformer implements SqlTransformer<KeyMapData, ResultSet> {
   @Override
   public KeyMapData transform(ResultSet resultSet) throws SQLException {
      return new KeyMapData(resultSet.getInt("key"),
            resultSet.getInt("type"),
            resultSet.getInt("action"));
   }
}
