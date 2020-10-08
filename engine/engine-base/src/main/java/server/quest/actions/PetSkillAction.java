package server.quest.actions;

import client.MapleCharacter;
import client.MapleQuestStatus;
import client.QuestStatus;
import client.processor.ItemProcessor;
import constants.inventory.ItemConstants;
import provider.MapleData;
import provider.MapleDataTool;
import server.processor.QuestProcessor;
import server.quest.MapleQuestActionType;

public class PetSkillAction extends MapleQuestAction {
   private int flag;

   public PetSkillAction(int questId, MapleData data) {
      super(questId, MapleQuestActionType.PET_SKILL);
      processData(data);
   }

   @Override
   public void processData(MapleData data) {
      flag = MapleDataTool.getInt("petskill", data);
   }

   @Override
   public boolean check(MapleCharacter chr, Integer extSelection) {
      MapleQuestStatus status = QuestProcessor.getInstance().getQuestStatus(chr, questId);
      if (!(status.status() == QuestStatus.NOT_STARTED && status.forfeited() > 0)) {
         return false;
      }

      return chr.getPet(0) != null;
   }

   @Override
   public void run(MapleCharacter chr, Integer extSelection) {
      ItemProcessor.getInstance().setFlag(0, (byte) ItemConstants.getFlagByInt(flag));
   }
} 
