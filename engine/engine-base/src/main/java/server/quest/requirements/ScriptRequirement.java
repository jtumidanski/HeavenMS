package server.quest.requirements;

import client.MapleCharacter;
import provider.MapleData;
import provider.MapleDataTool;
import server.quest.MapleQuest;
import server.quest.MapleQuestRequirementType;

public class ScriptRequirement extends MapleQuestRequirement {
   private boolean reqScript;

   public ScriptRequirement(MapleQuest quest, MapleData data) {
      super(MapleQuestRequirementType.BUFF);
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