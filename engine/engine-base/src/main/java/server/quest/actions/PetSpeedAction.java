package server.quest.actions;

import client.MapleCharacter;
import client.MapleClient;
import client.inventory.MaplePet;
import client.inventory.PetFlag;
import client.processor.PetProcessor;
import provider.MapleData;
import server.quest.MapleQuest;
import server.quest.MapleQuestActionType;

public class PetSpeedAction extends MapleQuestAction {

   public PetSpeedAction(MapleQuest quest, MapleData data) {
      super(MapleQuestActionType.PET_TAMENESS, quest);
      questID = quest.getId();
   }


   @Override
   public void processData(MapleData data) {
   }

   @Override
   public void run(MapleCharacter chr, Integer extSelection) {
      MapleClient c = chr.getClient();

      MaplePet pet = chr.getPet(0);   // assuming here only the pet leader will gain owner speed
      if (pet == null) {
         return;
      }

      c.lockClient();
      try {
         PetProcessor.getInstance().addPetFlag(pet, c.getPlayer(), PetFlag.OWNER_SPEED);
      } finally {
         c.unlockClient();
      }

   }
} 
