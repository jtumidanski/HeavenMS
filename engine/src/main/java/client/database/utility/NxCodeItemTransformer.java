package client.database.utility;

import java.sql.ResultSet;
import java.sql.SQLException;

import client.database.data.NxCodeItemData;

public class NxCodeItemTransformer implements SqlTransformer<NxCodeItemData, ResultSet> {
   @Override
   public NxCodeItemData transform(ResultSet resultSet) throws SQLException {
      return new NxCodeItemData(resultSet.getInt("type"),
            resultSet.getInt("quantity"),
            resultSet.getInt("item"));
   }
}
