package client.database.utility;

import java.sql.ResultSet;
import java.sql.SQLException;

import client.database.data.PetData;

public class PetTransformer implements SqlTransformer<PetData, ResultSet> {
   @Override
   public PetData transform(ResultSet resultSet) throws SQLException {
      return new PetData(
            resultSet.getString("name"),
            (byte) Math.min(resultSet.getByte("level"), 30),
            Math.min(resultSet.getInt("closeness"), 30000),
            Math.min(resultSet.getInt("fullness"), 100),
            resultSet.getInt("summoned") == 1,
            resultSet.getInt("flag"));
   }
}
