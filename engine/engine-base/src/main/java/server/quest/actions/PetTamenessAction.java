package server.quest.actions;

import client.MapleCharacter;
import client.MapleClient;
import client.inventory.MaplePet;
import client.processor.PetProcessor;
import provider.MapleData;
import provider.MapleDataTool;
import server.quest.MapleQuestActionType;

public class PetTamenessAction extends MapleQuestAction {
   int tameness;

   public PetTamenessAction(int questId, MapleData data) {
      super(questId, MapleQuestActionType.PET_TAMENESS);
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
