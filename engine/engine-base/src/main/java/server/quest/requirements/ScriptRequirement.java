package server.quest.requirements;

import client.MapleCharacter;
import provider.MapleData;
import provider.MapleDataTool;
import server.quest.MapleQuestRequirementType;

public class ScriptRequirement extends MapleQuestRequirement {
   private boolean reqScript;

   public ScriptRequirement(int questId, MapleData data) {
      super(questId, MapleQuestRequirementType.BUFF);
      processData(data);
   }

   @Override
   public void processData(MapleData data) {
      reqScript = !MapleDataTool.getString(data, "").isEmpty();
   }

   @Override
   public boolean check(MapleCharacter chr, Integer npcId) {
      return true;
   }

   public boolean get() {
      return reqScript;
   }
}