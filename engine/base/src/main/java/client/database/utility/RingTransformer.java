package client.database.utility;

import java.sql.ResultSet;
import java.sql.SQLException;

import client.MapleRing;

public class RingTransformer implements SqlTransformer<MapleRing, ResultSet> {
   @Override
   public MapleRing transform(ResultSet resultSet) throws SQLException {
      return new MapleRing(resultSet.getInt("id"),
            resultSet.getInt("partnerRingId"),
            resultSet.getInt("partnerChrId"),
            resultSet.getInt("itemid"),
            resultSet.getString("partnerName"));
   }
}
