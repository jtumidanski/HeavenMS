package client.database.utility;

import java.sql.ResultSet;
import java.sql.SQLException;

import client.database.data.SavedLocationData;

public class SavedLocationTransformer implements SqlTransformer<SavedLocationData, ResultSet> {
   @Override
   public SavedLocationData transform(ResultSet resultSet) throws SQLException {
      return new SavedLocationData(resultSet.getString("locationtype"),
            resultSet.getInt("map"),
            resultSet.getInt("portal"));
   }
}
