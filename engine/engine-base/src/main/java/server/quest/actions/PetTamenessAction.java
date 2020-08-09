package server.quest.actions;

import client.MapleCharacter;
import client.MapleClient;
import client.inventory.MaplePet;
import client.processor.PetProcessor;
import provider.MapleData;
import provider.MapleDataTool;
import server.quest.MapleQuest;
import server.quest.MapleQuestActionType;

public class PetTamenessAction extends MapleQuestAction {
   int tameness;

   public PetTamenessAction(MapleQuest quest, MapleData data) {
      super(MapleQuestActionType.PET_TAMENESS, quest);
      questID = quest.getId();
      processData(data);
   }


   @Override
   public void processData(MapleData data) {
      tameness = MapleDataTool.getInt(data);
   }

   @Override
   public void run(MapleCharacter chr, Integer extSelection) {
      MapleClient c = chr.getClient();

      MaplePet pet = chr.getPet(0);   // assuming here only the pet leader will gain tameness
      if (pet == null) {
         return;
      }

      c.lockClient();
      try {
         PetProcessor.getInstance().gainClosenessFullness(chr, (byte) 0, tameness, 0, 0);
      } finally {
         c.unlockClient();
      }
   }
} 
