package database.transformer;

import client.database.data.KeyMapData;
import entity.KeyMap;
import transformer.SqlTransformer;

public class KeyMapDataTransformer implements SqlTransformer<KeyMapData, KeyMap> {
   @Override
   public KeyMapData transform(KeyMap keyMap) {
      return new KeyMapData(keyMap.getKey(), keyMap.getType(), keyMap.getAction());
   }
}
