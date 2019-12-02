package client.database.utility;

import database.SqlTransformer;
import entity.Storage;
import server.MapleStorage;

public class MapleStorageTransformer implements SqlTransformer<MapleStorage, Storage> {
   @Override
   public MapleStorage transform(Storage resultSet) {
      return new MapleStorage(resultSet.getStorageId(),
            resultSet.getSlots().byteValue(),
            resultSet.getMeso());
   }
}
