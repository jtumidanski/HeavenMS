package database.transformer;

import client.database.data.MonsterCardData;
import transformer.SqlTransformer;

public class MonsterCardDataTransformer implements SqlTransformer<MonsterCardData, entity.MonsterCardData> {
   @Override
   public MonsterCardData transform(entity.MonsterCardData monsterCardData) {
      return new MonsterCardData(monsterCardData.getCardId(), monsterCardData.getMobId());
   }
}
