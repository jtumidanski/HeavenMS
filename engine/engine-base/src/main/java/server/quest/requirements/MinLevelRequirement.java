package server.quest.requirements;

import client.MapleCharacter;
import provider.MapleData;
import provider.MapleDataTool;
import server.quest.MapleQuestRequirementType;

public class MinLevelRequirement extends MapleQuestRequirement {
   private int minLevel;

   public MinLevelRequirement(int questId, MapleData data) {
      super(questId, MapleQuestRequirementType.MIN_LEVEL);
      processData(data);
   }

   @Override
   public void processData(MapleData data) {
      minLevel = MapleDataTool.getInt(data);
   }

   @Override
   public boolean check(MapleCharacter chr, Integer npcId) {
      return chr.getLevel() >= minLevel;
   }
}
