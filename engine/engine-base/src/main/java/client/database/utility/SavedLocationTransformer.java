package client.database.utility;

import client.database.data.SavedLocationData;
import database.SqlTransformer;
import entity.SavedLocation;

public class SavedLocationTransformer implements SqlTransformer<SavedLocationData, SavedLocation> {
   @Override
   public SavedLocationData transform(SavedLocation resultSet) {
      return new SavedLocationData(resultSet.getLocationType().name(),
            resultSet.getMap(),
            resultSet.getPortal());
   }
}
