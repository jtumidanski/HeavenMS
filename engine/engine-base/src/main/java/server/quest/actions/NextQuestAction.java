package server.quest.actions;

import client.MapleCharacter;
import client.MapleQuestStatus;
import provider.MapleData;
import provider.MapleDataTool;
import server.processor.QuestProcessor;
import server.quest.MapleQuest;
import server.quest.MapleQuestActionType;
import tools.PacketCreator;
import tools.packet.quest.info.QuestFinish;

public class NextQuestAction extends MapleQuestAction {
   private int nextQuest;

   public NextQuestAction(int questId, MapleData data) {
      super(questId, MapleQuestActionType.NEXT_QUEST);
      processData(data);
   }

   @Override
   public void processData(MapleData data) {
      nextQuest = MapleDataTool.getInt(data);
   }

   @Override
   public void run(MapleCharacter chr, Integer extSelection) {
      MapleQuestStatus status = QuestProcessor.getInstance().getQuestStatus(chr, questId);
      PacketCreator.announce(chr, new QuestFinish((short) questId, status.npcId(), (short) nextQuest));
   }
} 
