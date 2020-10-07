package server.quest.requirements;

import client.MapleCharacter;
import provider.MapleData;
import provider.MapleDataTool;
import server.quest.MapleQuestRequirementType;

public class MaxLevelRequirement extends MapleQuestRequirement {
   private int maxLevel;

   public MaxLevelRequirement(int questId, MapleData data) {
      super(questId, MapleQuestRequirementType.MAX_LEVEL);
      processData(data);
   }

   @Override
   public void processData(MapleData data) {
      maxLevel = MapleDataTool.getInt(data);
   }

   @Override
   public boolean check(MapleCharacter chr, Integer npcId) {
      return maxLevel >= chr.getLevel();
   }
}
