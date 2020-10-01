package database.transformer;

import entity.Storage;
import server.MapleStorage;
import transformer.SqlTransformer;

public class MapleStorageTransformer implements SqlTransformer<MapleStorage, Storage> {
   @Override
   public MapleStorage transform(Storage resultSet) {
      return new MapleStorage(resultSet.getStorageId(),
            resultSet.getSlots().byteValue(),
            resultSet.getMeso());
   }
}
