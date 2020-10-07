package server.quest.requirements;

import client.MapleCharacter;
import provider.MapleData;
import provider.MapleDataTool;
import server.quest.MapleQuestRequirementType;

public class CompletedQuestRequirement extends MapleQuestRequirement {
   private int reqQuest;

   public CompletedQuestRequirement(int questId, MapleData data) {
      super(questId, MapleQuestRequirementType.COMPLETED_QUEST);
      processData(data);
   }

   @Override
   public void processData(MapleData data) {
      reqQuest = MapleDataTool.getInt(data);
   }

   @Override
   public boolean check(MapleCharacter chr, Integer npcId) {
      return chr.getCompletedQuests().size() >= reqQuest;
   }
}
