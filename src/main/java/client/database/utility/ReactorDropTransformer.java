package client.database.utility;

import java.sql.ResultSet;
import java.sql.SQLException;

import server.maps.ReactorDropEntry;

public class ReactorDropTransformer implements SqlTransformer<ReactorDropEntry, ResultSet> {
   @Override
   public ReactorDropEntry transform(ResultSet resultSet) throws SQLException {
      return new ReactorDropEntry(resultSet.getInt("itemid"),
            resultSet.getInt("chance"),
            resultSet.getInt("questid"));
   }
}
