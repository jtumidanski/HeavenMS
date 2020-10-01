package database.transformer;

import entity.DropData;
import server.life.MonsterDropEntry;
import transformer.SqlTransformer;

public class MonsterDropEntryTransformer implements SqlTransformer<MonsterDropEntry, DropData> {
   @Override
   public MonsterDropEntry transform(DropData dropData) {
      return new MonsterDropEntry(dropData.getItemId(), dropData.getChance(), dropData.getMinimumQuantity(), dropData.getMaximumQuantity(), dropData.getQuestId());
   }
}
