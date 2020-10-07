package server.quest.actions;

import client.MapleCharacter;
import provider.MapleData;
import provider.MapleDataTool;
import server.quest.MapleQuestActionType;

public class InfoAction extends MapleQuestAction {
   private String info;

   public InfoAction(int questId, MapleData data) {
      super(questId, MapleQuestActionType.INFO);
      processData(data);
   }

   @Override
   public void processData(MapleData data) {
      info = MapleDataTool.getString(data, "");
   }

   @Override
   public void run(MapleCharacter chr, Integer extSelection) {
      chr.getAbstractPlayerInteraction().setQuestProgress(questId, info);
   }
}