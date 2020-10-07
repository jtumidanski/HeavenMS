package server.quest.requirements;

import client.MapleCharacter;
import provider.MapleData;
import provider.MapleDataTool;
import server.quest.MapleQuestRequirementType;

public class InfoNumberRequirement extends MapleQuestRequirement {

   private short infoNumber;

   public InfoNumberRequirement(int questId, MapleData data) {
      super(questId, MapleQuestRequirementType.INFO_NUMBER);
      processData(data);
   }

   @Override
   public void processData(MapleData data) {
      infoNumber = (short) MapleDataTool.getIntConvert(data, 0);
   }

   @Override
   public boolean check(MapleCharacter chr, Integer npcId) {
      return true;
   }

   public short getInfoNumber() {
      return infoNumber;
   }
}