package client.database.utility;

import java.sql.ResultSet;
import java.sql.SQLException;

import client.database.data.BbsThreadData;

public class BbsThreadTransformer implements SqlTransformer<BbsThreadData, ResultSet> {
   @Override
   public BbsThreadData transform(ResultSet resultSet) throws SQLException {
      return null;
   }
}
