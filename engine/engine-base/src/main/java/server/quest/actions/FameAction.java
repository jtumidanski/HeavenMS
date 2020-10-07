package server.quest.actions;

import client.MapleCharacter;
import provider.MapleData;
import provider.MapleDataTool;
import server.quest.MapleQuestActionType;

public class FameAction extends MapleQuestAction {
   private int fame;

   public FameAction(int questId, MapleData data) {
      super(questId, MapleQuestActionType.FAME);
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
