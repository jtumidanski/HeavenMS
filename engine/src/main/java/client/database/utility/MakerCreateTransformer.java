package client.database.utility;

import java.sql.ResultSet;
import java.sql.SQLException;

import client.database.data.MakerCreateData;

public class MakerCreateTransformer implements SqlTransformer<MakerCreateData, ResultSet> {
   @Override
   public MakerCreateData transform(ResultSet resultSet) throws SQLException {
      return new MakerCreateData(resultSet.getInt("req_level"),
            resultSet.getInt("req_maker_level"),
            resultSet.getInt("req_meso"),
            resultSet.getInt("quantity"));
   }
}
