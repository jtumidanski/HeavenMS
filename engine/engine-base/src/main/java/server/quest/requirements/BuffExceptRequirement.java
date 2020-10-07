package server.quest.requirements;

import client.MapleCharacter;
import provider.MapleData;
import provider.MapleDataTool;
import server.quest.MapleQuestRequirementType;

public class BuffExceptRequirement extends MapleQuestRequirement {
   private int buffId = -1;

   public BuffExceptRequirement(int questId, MapleData data) {
      super(questId, MapleQuestRequirementType.BUFF);
      processData(data);
   }

   @Override
   public void processData(MapleData data) {
      // item buffs are negative
      buffId = -1 * Integer.parseInt(MapleDataTool.getString(data));
   }

   @Override
   public boolean check(MapleCharacter chr, Integer npcId) {
      return !chr.hasBuffFromSourceId(buffId);
   }
}
