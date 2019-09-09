package server.processor;

import java.util.List;

import client.MapleCharacter;
import server.MapleItemInformationProvider;
import server.life.MonsterDropEntry;

public class DropEntryProcessor {
   private static DropEntryProcessor ourInstance = new DropEntryProcessor();

   public static DropEntryProcessor getInstance() {
      return ourInstance;
   }

   private DropEntryProcessor() {
   }

   public void sortDropEntries(List<MonsterDropEntry> from, List<MonsterDropEntry> item,
                               List<MonsterDropEntry> visibleQuest, List<MonsterDropEntry> otherQuest,
                               MapleCharacter chr) {
      MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();

      for (MonsterDropEntry mde : from) {
         if (!ii.isQuestItem(mde.itemId)) {
            item.add(mde);
         } else {
            if (chr.needQuestItem(mde.questid, mde.itemId)) {
               visibleQuest.add(mde);
            } else {
               otherQuest.add(mde);
            }
         }
      }
   }
}
