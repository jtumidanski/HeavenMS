package server.quest.actions;

import client.MapleCharacter;
import provider.MapleData;
import provider.MapleDataTool;
import server.quest.MapleQuest;
import server.quest.MapleQuestActionType;

public class InfoAction extends MapleQuestAction {

   private String info;
   private int questID;

   public InfoAction(MapleQuest quest, MapleData data) {
      super(MapleQuestActionType.INFO, quest);
      questID = quest.getId();
      processData(data);
   }

   @Override
   public void processData(MapleData data) {
      info = MapleDataTool.getString(data, "");
   }


   @Override
   public void run(MapleCharacter chr, Integer extSelection) {
      chr.getAbstractPlayerInteraction().setQuestProgress(questID, info);
   }

}