package server.quest.requirements;

import client.MapleCharacter;
import provider.MapleData;
import provider.MapleDataTool;
import server.quest.MapleQuestRequirementType;

public class NpcRequirement extends MapleQuestRequirement {
   private int reqNPC;

   public NpcRequirement(int questId, MapleData data) {
      super(questId, MapleQuestRequirementType.NPC);
      processData(data);
   }

   @Override
   public void processData(MapleData data) {
      reqNPC = MapleDataTool.getInt(data);
   }

   @Override
   public boolean check(MapleCharacter chr, Integer npcId) {
      return npcId != null && npcId == reqNPC;
   }

   public int get() {
      return reqNPC;
   }
}
