package client.database.utility;

import java.sql.ResultSet;
import java.sql.SQLException;

import client.database.data.CharNameAndIdData;

public class CharNameAndIdTransformer implements SqlTransformer<CharNameAndIdData, ResultSet> {
   @Override
   public CharNameAndIdData transform(ResultSet resultSet) throws SQLException {
      return new CharNameAndIdData(resultSet.getString("name"),
            resultSet.getInt("id"),
            resultSet.getInt("buddyCapacity"));
   }
}
