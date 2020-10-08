package server.quest.actions;

import java.util.HashMap;
import java.util.Map;

import client.MapleCharacter;
import client.MapleQuestStatus;
import client.MapleQuestStatusBuilder;
import client.QuestStatus;
import provider.MapleData;
import provider.MapleDataTool;
import server.processor.QuestProcessor;
import server.quest.MapleQuest;
import server.quest.MapleQuestActionType;

public class QuestAction extends MapleQuestAction {

   private final Map<Integer, Integer> quests = new HashMap<>();

   public QuestAction(int questId, MapleData data) {
      super(questId, MapleQuestActionType.QUEST);
      processData(data);
   }

   @Override
   public void processData(MapleData data) {
      for (MapleData entry : data) {
         int questId = MapleDataTool.getInt(entry.getChildByPath("id"));
         int stat = MapleDataTool.getInt(entry.getChildByPath("state"));
         quests.put(questId, stat);
      }
   }

   @Override
   public void run(MapleCharacter chr, Integer extSelection) {
      for (Integer questID : quests.keySet()) {
         int stat = quests.get(questID);
         MapleQuest quest = QuestProcessor.getInstance().getQuest(questID);
         MapleQuestStatus questStatus = new MapleQuestStatusBuilder(quest, QuestStatus.getById(stat)).build();
         QuestProcessor.getInstance().updateQuestStatus(chr, questStatus);
      }
   }
} 
