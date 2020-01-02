package server.quest.requirements;

import client.MapleCharacter;
import client.inventory.MaplePet;
import provider.MapleData;
import provider.MapleDataTool;
import server.quest.MapleQuest;
import server.quest.MapleQuestRequirementType;

public class MinTamenessRequirement extends MapleQuestRequirement {
   private int minTameness;

   public MinTamenessRequirement(MapleQuest quest, MapleData data) {
      super(MapleQuestRequirementType.MIN_PET_TAMENESS);
      processData(data);
   }

   @Override
   public void processData(MapleData data) {
      minTameness = MapleDataTool.getInt(data);
   }

   @Override
   public boolean check(MapleCharacter chr, Integer npcId) {
      int curCloseness = 0;

      for (MaplePet pet : chr.getPets()) {
         if (pet == null) continue;

         if (pet.closeness() > curCloseness)
            curCloseness = pet.closeness();
      }

      return curCloseness >= minTameness;
   }
}
