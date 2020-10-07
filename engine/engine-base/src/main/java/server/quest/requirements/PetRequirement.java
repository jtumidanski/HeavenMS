package server.quest.requirements;

import java.util.ArrayList;
import java.util.List;

import client.MapleCharacter;
import client.inventory.MaplePet;
import provider.MapleData;
import provider.MapleDataTool;
import server.quest.MapleQuestRequirementType;

public class PetRequirement extends MapleQuestRequirement {
   List<Integer> petIDs = new ArrayList<>();

   public PetRequirement(int questId, MapleData data) {
      super(questId, MapleQuestRequirementType.PET);
      processData(data);
   }

   @Override
   public void processData(MapleData data) {
      for (MapleData petData : data.getChildren()) {
         petIDs.add(MapleDataTool.getInt(petData.getChildByPath("id")));
      }
   }

   @Override
   public boolean check(MapleCharacter chr, Integer npcId) {
      for (MaplePet pet : chr.getPets()) {
         if (pet == null) {
            continue;
         }

         if (petIDs.contains(pet.id())) {
            return true;
         }
      }
      return false;
   }
}
