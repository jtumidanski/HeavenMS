package server.quest.requirements;

import java.util.HashMap;
import java.util.Map;

import client.MapleCharacter;
import client.MapleQuestStatus;
import provider.MapleData;
import provider.MapleDataTool;
import server.processor.QuestProcessor;
import server.quest.MapleQuestRequirementType;
import tools.LogType;
import tools.LoggerOriginator;
import tools.LoggerUtil;

public class MobRequirement extends MapleQuestRequirement {
   Map<Integer, Integer> mobs = new HashMap<>();

   public MobRequirement(int questId, MapleData data) {
      super(questId, MapleQuestRequirementType.MOB);
      processData(data);
   }

   @Override
   public void processData(MapleData data) {
      for (MapleData questEntry : data.getChildren()) {
         int mobID = MapleDataTool.getInt(questEntry.getChildByPath("id"));
         int countReq = MapleDataTool.getInt(questEntry.getChildByPath("count"));
         mobs.put(mobID, countReq);
      }
   }

   @Override
   public boolean check(MapleCharacter chr, Integer npcId) {
      MapleQuestStatus status = QuestProcessor.getInstance().getQuestStatus(chr, getQuestId());
      for (Integer mobID : mobs.keySet()) {
         int countReq = mobs.get(mobID);
         int progress;

         try {
            progress = Integer.parseInt(status.getProgress(mobID));
         } catch (NumberFormatException ex) {
            LoggerUtil.printError(LoggerOriginator.ENGINE, LogType.EXCEPTION_CAUGHT, ex,
                  "Mob: " + mobID + " Quest: " + getQuestId() + "CID:"
                        + " " + chr.getId() + " Progress: " + status.getProgress(mobID));
            return false;
         }

         if (progress < countReq) {
            return false;
         }
      }
      return true;
   }

   public int getRequiredMobCount(int mobId) {
      if (mobs.containsKey(mobId)) {
         return mobs.get(mobId);
      }
      return 0;
   }
}
