package client.database.utility;

import client.database.data.SavedLocationData;
import entity.SavedLocation;
import transformer.SqlTransformer;

public class SavedLocationTransformer implements SqlTransformer<SavedLocationData, SavedLocation> {
   @Override
   public SavedLocationData transform(SavedLocation resultSet) {
      return new SavedLocationData(resultSet.getLocationType().name(),
            resultSet.getMap(),
            resultSet.getPortal());
   }
}
