package server.quest.actions;

import client.MapleCharacter;
import client.MapleQuestStatus;
import client.processor.ItemProcessor;
import constants.inventory.ItemConstants;
import provider.MapleData;
import provider.MapleDataTool;
import server.quest.MapleQuest;
import server.quest.MapleQuestActionType;

public class PetSkillAction extends MapleQuestAction {
   int flag;

   public PetSkillAction(MapleQuest quest, MapleData data) {
      super(MapleQuestActionType.PET_SKILL, quest);
      questID = quest.getId();
      processData(data);
   }


   @Override
   public void processData(MapleData data) {
      flag = MapleDataTool.getInt("petskill", data);
   }

   @Override
   public boolean check(MapleCharacter chr, Integer extSelection) {
      MapleQuestStatus status = chr.getQuest(MapleQuest.getInstance(questID));
      if (!(status.getStatus() == MapleQuestStatus.Status.NOT_STARTED && status.getForfeited() > 0)) {
         return false;
      }

      return chr.getPet(0) != null;
   }

   @Override
   public void run(MapleCharacter chr, Integer extSelection) {
      ItemProcessor.getInstance().setFlag(0, (byte) ItemConstants.getFlagByInt(flag));
   }
} 
