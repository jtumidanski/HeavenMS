package server.quest.actions;

import client.MapleCharacter;
import client.MapleQuestStatus;
import provider.MapleData;
import provider.MapleDataTool;
import server.quest.MapleQuest;
import server.quest.MapleQuestActionType;
import tools.PacketCreator;
import tools.packet.quest.info.QuestFinish;

public class NextQuestAction extends MapleQuestAction {
   int nextQuest;

   public NextQuestAction(MapleQuest quest, MapleData data) {
      super(MapleQuestActionType.NEXT_QUEST, quest);
      processData(data);
   }


   @Override
   public void processData(MapleData data) {
      nextQuest = MapleDataTool.getInt(data);
   }

   @Override
   public void run(MapleCharacter chr, Integer extSelection) {
      MapleQuestStatus status = chr.getQuest(MapleQuest.getInstance(questID));
      PacketCreator.announce(chr, new QuestFinish((short) questID, status.getNpc(), (short) nextQuest));
   }
} 
