package server.quest.requirements;

import client.MapleCharacter;
import provider.MapleData;
import provider.MapleDataTool;
import server.quest.MapleQuestRequirementType;

public class FieldEnterRequirement extends MapleQuestRequirement {
   private int mapId = -1;

   public FieldEnterRequirement(int questId, MapleData data) {
      super(questId, MapleQuestRequirementType.FIELD_ENTER);
      processData(data);
   }

   @Override
   public void processData(MapleData data) {
      MapleData zeroField = data.getChildByPath("0");
      if (zeroField != null) {
         mapId = MapleDataTool.getInt(zeroField);
      }
   }

   @Override
   public boolean check(MapleCharacter chr, Integer npcId) {
      return mapId == chr.getMapId();
   }
}
