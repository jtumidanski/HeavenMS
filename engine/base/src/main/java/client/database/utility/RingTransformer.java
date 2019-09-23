package client.database.utility;

import java.sql.ResultSet;
import java.sql.SQLException;

import client.Ring;

public class RingTransformer implements SqlTransformer<Ring, ResultSet> {
   @Override
   public Ring transform(ResultSet resultSet) throws SQLException {
      return new Ring(resultSet.getInt("id"),
            resultSet.getInt("partnerRingId"),
            resultSet.getInt("partnerChrId"),
            resultSet.getInt("itemid"),
            resultSet.getString("partnerName"));
   }
}
