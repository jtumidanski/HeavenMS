package database.transformer;

import client.database.data.MonsterBookData;
import entity.MonsterBook;
import transformer.SqlTransformer;

public class MonsterBookDataTransformer implements SqlTransformer<MonsterBookData, MonsterBook> {
   @Override
   public MonsterBookData transform(MonsterBook monsterBook) {
      return new MonsterBookData(monsterBook.getCardId(), monsterBook.getLevel());
   }
}
