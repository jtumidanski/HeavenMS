package database.transformer;

import client.database.data.PlayerNpcFieldData;
import entity.PlayerNpcField;
import transformer.SqlTransformer;

public class PlayerNpcFieldTransformer implements SqlTransformer<PlayerNpcFieldData, PlayerNpcField> {
   @Override
   public PlayerNpcFieldData transform(PlayerNpcField resultSet) {
      return new PlayerNpcFieldData(resultSet.getWorld(),
            resultSet.getMap(),
            resultSet.getStep(),
            resultSet.getPodium());
   }
}
