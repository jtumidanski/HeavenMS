package client.database.utility;

import java.sql.ResultSet;
import java.sql.SQLException;

import client.database.data.GiftData;

public class GiftDataTransformer implements SqlTransformer<GiftData, ResultSet> {
   @Override
   public GiftData transform(ResultSet resultSet) throws SQLException {
      return new GiftData(resultSet.getInt("sn"),
            resultSet.getInt("ringid"),
            resultSet.getString("message"),
            resultSet.getString("from"));
   }
}
