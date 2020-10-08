package server.quest.requirements;

import java.util.HashMap;
import java.util.Map;

import client.MapleCharacter;
import client.MapleQuestStatus;
import client.QuestStatus;
import provider.MapleData;
import provider.MapleDataTool;
import server.processor.QuestProcessor;
import server.quest.MapleQuestRequirementType;

public class QuestRequirement extends MapleQuestRequirement {
   Map<Integer, Integer> quests = new HashMap<>();

   public QuestRequirement(int questId, MapleData data) {
      super(questId, MapleQuestRequirementType.QUEST);
      processData(data);
   }

   @Override
   public void processData(MapleData data) {
      for (MapleData questEntry : data.getChildren()) {
         int questID = MapleDataTool.getInt(questEntry.getChildByPath("id"));
         int stateReq = MapleDataTool.getInt(questEntry.getChildByPath("state"));
         quests.put(questID, stateReq);
      }
   }

   @Override
   public boolean check(MapleCharacter chr, Integer npcId) {
      for (Integer questID : quests.keySet()) {
         int stateReq = quests.get(questID);
         MapleQuestStatus q = QuestProcessor.getInstance().getQuestStatus(chr, questID);

         if (q == null && QuestStatus.NOT_STARTED.equals(QuestStatus.getById(stateReq))) {
            continue;
         }

         if (q == null || !q.status().equals(QuestStatus.getById(stateReq))) {
            return false;
         }
      }
      return true;
   }
}
