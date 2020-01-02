package server.quest.actions;

import client.MapleCharacter;
import provider.MapleData;
import provider.MapleDataTool;
import server.quest.MapleQuest;
import server.quest.MapleQuestActionType;

public class FameAction extends MapleQuestAction {
   int fame;

   public FameAction(MapleQuest quest, MapleData data) {
      super(MapleQuestActionType.FAME, quest);
      questID = quest.getId();
      processData(data);
   }

   @Override
   public void processData(MapleData data) {
      fame = MapleDataTool.getInt(data);
   }

   @Override
   public void run(MapleCharacter chr, Integer extSelection) {
      chr.gainFame(fame);
   }
} 
