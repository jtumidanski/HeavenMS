package server.quest;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import client.MapleQuestStatus.Status;
import config.YamlConfig;
import server.quest.actions.MapleQuestAction;
import server.quest.requirements.InfoExRequirement;
import server.quest.requirements.InfoNumberRequirement;
import server.quest.requirements.IntervalRequirement;
import server.quest.requirements.ItemRequirement;
import server.quest.requirements.MapleQuestRequirement;
import server.quest.requirements.MobRequirement;
import server.quest.requirements.NpcRequirement;
import server.quest.requirements.ScriptRequirement;

public record MapleQuest(short id, String name, String parent, int timeLimit, int timeLimit2, boolean autoStart,
                         boolean autoPreComplete, boolean autoComplete, boolean repeatable, int medalId,
                         Map<MapleQuestRequirementType, MapleQuestRequirement> startReqs,
                         Map<MapleQuestRequirementType, MapleQuestRequirement> completeReqs,
                         Map<MapleQuestActionType, MapleQuestAction> startActs,
                         Map<MapleQuestActionType, MapleQuestAction> completeActs, List<Integer> relevantMobs) {

   public boolean isAutoComplete() {
      return autoPreComplete || autoComplete;
   }

   public boolean isSameDayRepeatable() {
      if (!repeatable) {
         return false;
      }

      IntervalRequirement ir = (IntervalRequirement) startReqs.get(MapleQuestRequirementType.INTERVAL);
      return ir.getInterval() < YamlConfig.config.server.QUEST_POINT_REPEATABLE_INTERVAL * 60 * 60 * 1000;
   }

   public List<Integer> getRelevantMobs() {
      return relevantMobs;
   }

   public int getStartItemAmountNeeded(int itemId) {
      MapleQuestRequirement req = startReqs.get(MapleQuestRequirementType.ITEM);
      if (req == null) {
         return Integer.MIN_VALUE;
      }

      ItemRequirement itemRequirement = (ItemRequirement) req;
      return itemRequirement.getItemAmountNeeded(itemId, false);
   }

   public int getCompleteItemAmountNeeded(int itemId) {
      MapleQuestRequirement req = completeReqs.get(MapleQuestRequirementType.ITEM);
      if (req == null) {
         return 0;
      }

      ItemRequirement itemRequirement = (ItemRequirement) req;
      return itemRequirement.getItemAmountNeeded(itemId, false);
   }

   public int getMobAmountNeeded(int mid) {
      MapleQuestRequirement req = completeReqs.get(MapleQuestRequirementType.MOB);
      if (req == null) {
         return 0;
      }

      MobRequirement mobRequirement = (MobRequirement) req;

      return mobRequirement.getRequiredMobCount(mid);
   }

   public short getInfoNumber(Status qs) {
      boolean checkEnd = qs.equals(Status.STARTED);
      Map<MapleQuestRequirementType, MapleQuestRequirement> reqs = !checkEnd ? startReqs : completeReqs;

      MapleQuestRequirement req = reqs.get(MapleQuestRequirementType.INFO_NUMBER);
      if (req != null) {
         InfoNumberRequirement inReq = (InfoNumberRequirement) req;
         return inReq.getInfoNumber();
      } else {
         return 0;
      }
   }

   public String getInfoEx(Status qs, int index) {
      boolean checkEnd = qs.equals(Status.STARTED);
      Map<MapleQuestRequirementType, MapleQuestRequirement> reqs = !checkEnd ? startReqs : completeReqs;
      try {
         MapleQuestRequirement req = reqs.get(MapleQuestRequirementType.INFO_EX);
         InfoExRequirement ixReq = (InfoExRequirement) req;
         return ixReq.getInfo().get(index);
      } catch (Exception e) {
         return "";
      }
   }

   public List<String> getInfoEx(Status qs) {
      boolean checkEnd = qs.equals(Status.STARTED);
      Map<MapleQuestRequirementType, MapleQuestRequirement> reqs = !checkEnd ? startReqs : completeReqs;
      try {
         MapleQuestRequirement req = reqs.get(MapleQuestRequirementType.INFO_EX);
         InfoExRequirement ixReq = (InfoExRequirement) req;
         return ixReq.getInfo();
      } catch (Exception e) {
         return new LinkedList<>();
      }
   }

   public int getNpcRequirement(boolean checkEnd) {
      Map<MapleQuestRequirementType, MapleQuestRequirement> reqs = !checkEnd ? startReqs : completeReqs;

      MapleQuestRequirement mqr = reqs.get(MapleQuestRequirementType.NPC);
      if (mqr != null) {
         return ((NpcRequirement) mqr).get();
      } else {
         return -1;
      }
   }

   public boolean hasScriptRequirement(boolean checkEnd) {
      Map<MapleQuestRequirementType, MapleQuestRequirement> reqs = !checkEnd ? startReqs : completeReqs;
      MapleQuestRequirement mqr = reqs.get(MapleQuestRequirementType.SCRIPT);

      if (mqr != null) {
         return ((ScriptRequirement) mqr).get();
      } else {
         return false;
      }
   }

   public boolean hasNextQuestAction() {
      MapleQuestAction mqa = completeActs.get(MapleQuestActionType.NEXT_QUEST);
      return mqa != null;
   }
}
