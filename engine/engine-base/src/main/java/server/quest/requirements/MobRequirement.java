package server.quest.requirements;

import java.util.HashMap;
import java.util.Map;

import client.MapleCharacter;
import client.MapleQuestStatus;
import provider.MapleData;
import provider.MapleDataTool;
import server.quest.MapleQuest;
import server.quest.MapleQuestRequirementType;
import tools.FilePrinter;

public class MobRequirement extends MapleQuestRequirement {
   Map<Integer, Integer> mobs = new HashMap<>();
   private int questID;

   public MobRequirement(MapleQuest quest, MapleData data) {
      super(MapleQuestRequirementType.MOB);
      questID = quest.getId();
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
      MapleQuestStatus status = chr.getQuest(MapleQuest.getInstance(questID));
      for (Integer mobID : mobs.keySet()) {
         int countReq = mobs.get(mobID);
         int progress;

         try {
            progress = Integer.parseInt(status.getProgress(mobID));
         } catch (NumberFormatException ex) {
            FilePrinter.printError(FilePrinter.EXCEPTION_CAUGHT, ex, "Mob: " + mobID + " Quest: " + questID + "CID: " + chr.getId() + " Progress: " + status.getProgress(mobID));
            return false;
         }

         if (progress < countReq)
            return false;
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
