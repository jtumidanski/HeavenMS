package client.database.utility;

import java.sql.ResultSet;
import java.sql.SQLException;

import client.database.data.NxCodeData;

public class NxCodeTransformer implements SqlTransformer<NxCodeData, ResultSet> {
   @Override
   public NxCodeData transform(ResultSet resultSet) throws SQLException {
      return new NxCodeData(resultSet.getString("retriever"),
            resultSet.getLong("expiration"),
            resultSet.getInt("id"));
   }
}
