package client.database.utility;

import java.sql.ResultSet;
import java.sql.SQLException;

import client.database.data.AllianceData;

public class AllianceTransformer implements SqlTransformer<AllianceData, ResultSet> {
   @Override
   public AllianceData transform(ResultSet resultSet) throws SQLException {
      return new AllianceData(resultSet.getInt("capacity"), resultSet.getString("name"),
            resultSet.getString("notice"),
            resultSet.getString("rank1"),
            resultSet.getString("rank2"),
            resultSet.getString("rank3"),
            resultSet.getString("rank4"),
            resultSet.getString("rank5"));
   }
}
