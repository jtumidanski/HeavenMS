package client.database.utility;

import java.sql.ResultSet;
import java.sql.SQLException;

import client.database.data.MarriageData;

public class MarriageTransformer implements SqlTransformer<MarriageData, ResultSet> {
   @Override
   public MarriageData transform(ResultSet resultSet) throws SQLException {
      return new MarriageData(resultSet.getInt("marriageid"),
            resultSet.getInt("husbandid"),
            resultSet.getInt("wifeid"));
   }
}
