package server.quest;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import client.MapleCharacter;
import client.MapleQuestStatus;
import client.MapleQuestStatus.Status;
import config.YamlConfig;
import server.quest.actions.ItemAction;
import server.quest.actions.MapleQuestAction;
import server.quest.requirements.InfoExRequirement;
import server.quest.requirements.InfoNumberRequirement;
import server.quest.requirements.IntervalRequirement;
import server.quest.requirements.ItemRequirement;
import server.quest.requirements.MapleQuestRequirement;
import server.quest.requirements.MobRequirement;
import server.quest.requirements.NpcRequirement;
import server.quest.requirements.ScriptRequirement;
import tools.PacketCreator;
import tools.StringUtil;
import tools.packet.foreigneffect.ShowForeignEffect;
import tools.packet.quest.info.RemoveQuestTimeLimit;
import tools.packet.showitemgaininchat.ShowSpecialEffect;

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

   public boolean canStartQuestByStatus(MapleCharacter c) {
      MapleQuestStatus mqs = c.getQuest(this);
      return !(!mqs.getStatus().equals(Status.NOT_STARTED) && !(mqs.getStatus().equals(Status.COMPLETED) && repeatable));
   }

   public boolean canQuestByInfoProgress(MapleCharacter chr) {
      MapleQuestStatus mqs = chr.getQuest(this);
      List<String> ix = mqs.getInfoEx();
      if (!ix.isEmpty()) {
         short questId = mqs.getQuestID();
         short infoNumber = mqs.getInfoNumber();
         if (infoNumber <= 0) {
            infoNumber = questId;  // on default infoNumber mimics quest id
         }

         int ixSize = ix.size();
         for (int i = 0; i < ixSize; i++) {
            String progress = chr.getAbstractPlayerInteraction().getQuestProgress(infoNumber, i);
            String ixProgress = ix.get(i);

            if (!progress.contentEquals(ixProgress)) {
               return false;
            }
         }
      }

      return true;
   }

   public boolean canStart(MapleCharacter chr, int npcId) {
      if (!canStartQuestByStatus(chr)) {
         return false;
      }

      for (MapleQuestRequirement r : startReqs.values()) {
         if (!r.check(chr, npcId)) {
            return false;
         }
      }

      return canQuestByInfoProgress(chr);
   }

   public boolean canComplete(MapleCharacter chr, Integer npcId) {
      MapleQuestStatus mqs = chr.getQuest(this);
      if (!mqs.getStatus().equals(Status.STARTED)) {
         return false;
      }

      for (MapleQuestRequirement r : completeReqs.values()) {
         if (!r.check(chr, npcId)) {
            return false;
         }
      }

      return canQuestByInfoProgress(chr);
   }

   public void start(MapleCharacter chr, int npc) {
      if (autoStart || canStart(chr, npc)) {
         Collection<MapleQuestAction> acts = startActs.values();
         for (MapleQuestAction a : acts) {
            if (!a.check(chr, null)) { // would null be good ?
               return;
            }
         }
         for (MapleQuestAction a : acts) {
            a.run(chr, null);
         }
         forceStart(chr, npc);
      }
   }

   public void complete(MapleCharacter chr, int npc) {
      complete(chr, npc, null);
   }

   public void complete(MapleCharacter chr, int npc, Integer selection) {
      if (autoPreComplete || canComplete(chr, npc)) {
         Collection<MapleQuestAction> acts = completeActs.values();
         for (MapleQuestAction a : acts) {
            if (!a.check(chr, selection)) {
               return;
            }
         }
         forceComplete(chr, npc);
         for (MapleQuestAction a : acts) {
            a.run(chr, selection);
         }
         if (!this.hasNextQuestAction()) {
            chr.announceUpdateQuest(MapleCharacter.DelayedQuestUpdate.INFO, chr.getQuest(this));
         }
      }
   }

   public void reset(MapleCharacter chr) {
      MapleQuestStatus newStatus = new MapleQuestStatus(this, MapleQuestStatus.Status.NOT_STARTED);
      chr.updateQuestStatus(newStatus);
   }

   public boolean forfeit(MapleCharacter chr) {
      if (!chr.getQuest(this).getStatus().equals(Status.STARTED)) {
         return false;
      }
      if (timeLimit > 0) {
         PacketCreator.announce(chr, new RemoveQuestTimeLimit(id));
      }
      MapleQuestStatus newStatus = new MapleQuestStatus(this, MapleQuestStatus.Status.NOT_STARTED);
      newStatus.setForfeited(chr.getQuest(this).getForfeited() + 1);
      chr.updateQuestStatus(newStatus);
      return true;
   }

   public boolean forceStart(MapleCharacter chr, int npc) {
      MapleQuestStatus newStatus = new MapleQuestStatus(this, MapleQuestStatus.Status.STARTED, npc);

      MapleQuestStatus oldStatus = chr.getQuest(id());
      for (Map.Entry<Integer, String> e : oldStatus.getProgress().entrySet()) {
         newStatus.setProgress(e.getKey(), e.getValue());
      }

      if (id / 100 == 35 && YamlConfig.config.server.TOT_MOB_QUEST_REQUIREMENT > 0) {
         int setProgress = 999 - Math.min(999, YamlConfig.config.server.TOT_MOB_QUEST_REQUIREMENT);

         for (Integer pid : newStatus.getProgress().keySet()) {
            if (pid >= 8200000 && pid <= 8200012) {
               String pr = StringUtil.getLeftPaddedStr(Integer.toString(setProgress), '0', 3);
               newStatus.setProgress(pid, pr);
            }
         }
      }

      newStatus.setForfeited(chr.getQuest(this).getForfeited());
      newStatus.setCompleted(chr.getQuest(this).getCompleted());

      if (timeLimit > 0) {
         newStatus.setExpirationTime(System.currentTimeMillis() + (timeLimit * 1000));
         chr.questTimeLimit(this, timeLimit);
      }
      if (timeLimit2 > 0) {
         newStatus.setExpirationTime(System.currentTimeMillis() + timeLimit2);
         chr.questTimeLimit2(this, newStatus.getExpirationTime());
      }

      chr.updateQuestStatus(newStatus);

      return true;
   }

   public boolean forceComplete(MapleCharacter chr, int npc) {
      if (timeLimit > 0) {
         PacketCreator.announce(chr, new RemoveQuestTimeLimit(id));
      }

      MapleQuestStatus newStatus = new MapleQuestStatus(this, MapleQuestStatus.Status.COMPLETED, npc);
      newStatus.setForfeited(chr.getQuest(this).getForfeited());
      newStatus.setCompleted(chr.getQuest(this).getCompleted());
      newStatus.setCompletionTime(System.currentTimeMillis());
      chr.updateQuestStatus(newStatus);

      PacketCreator.announce(chr, new ShowSpecialEffect(9)); // Quest completion
      chr.getMap().broadcastMessage(chr, new ShowForeignEffect(chr.getId(), 9)); //use 9 instead of 12 for both
      return true;
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

   public boolean restoreLostItem(MapleCharacter chr, int itemId) {
      if (chr.getQuest(this).getStatus().equals(MapleQuestStatus.Status.STARTED)) {
         ItemAction itemAct = (ItemAction) startActs.get(MapleQuestActionType.ITEM);
         if (itemAct != null) {
            return itemAct.restoreLostItem(chr, itemId);
         }
      }

      return false;
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
      Map<MapleQuestActionType, MapleQuestAction> acts = completeActs;
      MapleQuestAction mqa = acts.get(MapleQuestActionType.NEXT_QUEST);

      return mqa != null;
   }
}
