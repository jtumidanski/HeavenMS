package client.database.utility;

import java.sql.ResultSet;
import java.sql.SQLException;

import server.MapleStorage;

public class MapleStorageTransformer implements SqlTransformer<MapleStorage, ResultSet> {
   @Override
   public MapleStorage transform(ResultSet resultSet) throws SQLException {
      return new MapleStorage(resultSet.getInt("storageid"),
            (byte) resultSet.getInt("slots"),
            resultSet.getInt("meso"));
   }
}
