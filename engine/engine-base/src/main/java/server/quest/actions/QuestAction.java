package server.quest.actions;

import java.util.HashMap;
import java.util.Map;

import client.MapleCharacter;
import client.MapleQuestStatus;
import provider.MapleData;
import provider.MapleDataTool;
import server.processor.QuestProcessor;
import server.quest.MapleQuest;
import server.quest.MapleQuestActionType;

public class QuestAction extends MapleQuestAction {
   private int mesos;

   private Map<Integer, Integer> quests = new HashMap<>();

   public QuestAction(int questId, MapleData data) {
      super(questId, MapleQuestActionType.QUEST);
      processData(data);
   }

   @Override
   public void processData(MapleData data) {
      for (MapleData qEntry : data) {
         int questId = MapleDataTool.getInt(qEntry.getChildByPath("id"));
         int stat = MapleDataTool.getInt(qEntry.getChildByPath("state"));
         quests.put(questId, stat);
      }
   }

   @Override
   public void run(MapleCharacter chr, Integer extSelection) {
      for (Integer questID : quests.keySet()) {
         int stat = quests.get(questID);
         chr.updateQuestStatus(new MapleQuestStatus(QuestProcessor.getInstance().getQuest(questID), MapleQuestStatus.Status.getById(stat)));
      }
   }
} 
