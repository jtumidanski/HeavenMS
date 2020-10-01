package database.transformer;

import entity.DropDataGlobal;
import server.life.MonsterGlobalDropEntry;
import transformer.SqlTransformer;

public class MonsterGlobalDropEntryTransformer implements SqlTransformer<MonsterGlobalDropEntry, DropDataGlobal> {
   @Override
   public MonsterGlobalDropEntry transform(DropDataGlobal dropDataGlobal) {
      return new MonsterGlobalDropEntry(dropDataGlobal.getItemId(), dropDataGlobal.getChance(), dropDataGlobal.getContinent(), dropDataGlobal.getMinimumQuantity(), dropDataGlobal.getMaximumQuantity(), dropDataGlobal.getQuestId());
   }
}
