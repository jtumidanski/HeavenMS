package server.processor;

import java.awt.*;
import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import client.MapleCharacter;
import client.MapleQuestStatus;
import config.YamlConfig;
import provider.MapleData;
import provider.MapleDataProvider;
import provider.MapleDataProviderFactory;
import provider.MapleDataTool;
import scripting.quest.QuestScriptManager;
import server.life.MapleNPC;
import server.quest.MapleQuest;
import server.quest.MapleQuestActionType;
import server.quest.MapleQuestRequirementType;
import server.quest.QuestBuilder;
import server.quest.actions.BuffAction;
import server.quest.actions.ExpAction;
import server.quest.actions.FameAction;
import server.quest.actions.InfoAction;
import server.quest.actions.ItemAction;
import server.quest.actions.MapleQuestAction;
import server.quest.actions.MesoAction;
import server.quest.actions.NextQuestAction;
import server.quest.actions.PetSkillAction;
import server.quest.actions.PetSpeedAction;
import server.quest.actions.PetTamenessAction;
import server.quest.actions.QuestAction;
import server.quest.actions.SkillAction;
import server.quest.requirements.BuffExceptRequirement;
import server.quest.requirements.BuffRequirement;
import server.quest.requirements.CompletedQuestRequirement;
import server.quest.requirements.EndDateRequirement;
import server.quest.requirements.FieldEnterRequirement;
import server.quest.requirements.InfoExRequirement;
import server.quest.requirements.InfoNumberRequirement;
import server.quest.requirements.IntervalRequirement;
import server.quest.requirements.ItemRequirement;
import server.quest.requirements.JobRequirement;
import server.quest.requirements.MapleQuestRequirement;
import server.quest.requirements.MaxLevelRequirement;
import server.quest.requirements.MesoRequirement;
import server.quest.requirements.MinLevelRequirement;
import server.quest.requirements.MinTamenessRequirement;
import server.quest.requirements.MobRequirement;
import server.quest.requirements.MonsterBookCountRequirement;
import server.quest.requirements.NpcRequirement;
import server.quest.requirements.PetRequirement;
import server.quest.requirements.QuestRequirement;
import server.quest.requirements.ScriptRequirement;
import tools.I18nMessage;
import tools.LogType;
import tools.LoggerOriginator;
import tools.LoggerUtil;
import tools.MessageBroadcaster;
import tools.PacketCreator;
import tools.ServerNoticeType;
import tools.StringUtil;
import tools.packet.foreigneffect.ShowForeignEffect;
import tools.packet.quest.info.QuestExpire;
import tools.packet.quest.info.RemoveQuestTimeLimit;
import tools.packet.showitemgaininchat.ShowSpecialEffect;

public class QuestProcessor {
   private static final Object lock = new Object();

   private static volatile QuestProcessor instance;

   private final Set<Short> exploitableQuests = new HashSet<>();

   private final MapleData questInfo;

   private final MapleData questAct;

   private final MapleData questReq;

   private final Map<Integer, MapleQuest> quests = new HashMap<>();

   private final Map<Integer, Integer> infoNumberQuests = new HashMap<>();

   public static QuestProcessor getInstance() {
      QuestProcessor result = instance;
      if (result == null) {
         synchronized (lock) {
            result = instance;
            if (result == null) {
               result = new QuestProcessor();
               instance = result;
            }
         }
      }
      return result;
   }

   private QuestProcessor() {
      exploitableQuests.add((short) 2338);    // there are a lot more exploitable quests, they need to be nit-picked
      exploitableQuests.add((short) 3637);
      exploitableQuests.add((short) 3714);
      exploitableQuests.add((short) 21752);

      MapleDataProvider questData = MapleDataProviderFactory.getDataProvider(new File(System.getProperty("wzpath") + "/Quest.wz"));
      questInfo = questData.getData("QuestInfo.img");
      questAct = questData.getData("Act.img");
      questReq = questData.getData("Check.img");

      long timeToTake = System.currentTimeMillis();
      loadAllQuest();
      LoggerUtil.printInfo(LoggerOriginator.ENGINE, LogType.PROCESS,
            "Quest loaded in " + ((System.currentTimeMillis() - timeToTake) / 1000.0) + " seconds\r\n");
   }

   protected void loadAllQuest() {
      try {
         for (MapleData questData : questInfo.getChildren()) {
            int questId = Integer.parseInt(questData.getName());

            MapleQuest quest = createQuest((short) questId);
            quests.put(questId, quest);

            int infoNumber;

            infoNumber = quest.getInfoNumber(MapleQuestStatus.Status.STARTED);
            if (infoNumber > 0) {
               infoNumberQuests.put(infoNumber, questId);
            }

            infoNumber = quest.getInfoNumber(MapleQuestStatus.Status.COMPLETED);
            if (infoNumber > 0) {
               infoNumberQuests.put(infoNumber, questId);
            }
         }
      } catch (Exception ex) {
         LoggerUtil.printError(LoggerOriginator.ENGINE, LogType.EXCEPTION, ex);
      }
   }

   protected MapleQuest createQuest(short questId) {
      QuestBuilder questBuilder = new QuestBuilder(questId);

      MapleData reqData = questReq.getChildByPath(String.valueOf(questId));
      if (reqData == null) { //most likely infoEx
         return questBuilder.build();
      }

      if (questInfo != null) {
         MapleData reqInfo = questInfo.getChildByPath(String.valueOf(questId));
         if (reqInfo != null) {
            questBuilder.setName(MapleDataTool.getString("name", reqInfo, ""))
                  .setParent(MapleDataTool.getString("parent", reqInfo, ""))
                  .setTimeLimit(MapleDataTool.getInt("timeLimit", reqInfo, 0))
                  .setTimeLimit2(MapleDataTool.getInt("timeLimit2", reqInfo, 0))
                  .setAutoStart(MapleDataTool.getInt("autoStart", reqInfo, 0) == 1)
                  .setAutoPreComplete(MapleDataTool.getInt("autoPreComplete", reqInfo, 0) == 1)
                  .setAutoComplete(MapleDataTool.getInt("autoComplete", reqInfo, 0) == 1)
                  .setMedalId(MapleDataTool.getInt("viewMedalItem", reqInfo, -1));
         } else {
            LoggerUtil.printError(LoggerOriginator.ENGINE, LogType.QUEST, "no data " + questId);
         }
      }

      MapleData startReqData = reqData.getChildByPath("0");
      if (startReqData != null) {
         for (MapleData startReq : startReqData.getChildren()) {
            MapleQuestRequirementType type = MapleQuestRequirementType.getByWZName(startReq.getName());
            if (type.equals(MapleQuestRequirementType.INTERVAL)) {
               questBuilder.setRepeatable(true);
            } else if (type.equals(MapleQuestRequirementType.MOB)) {
               startReq.getChildren().forEach(mob -> questBuilder.addRelevantMob(MapleDataTool.getInt(mob.getChildByPath("id"))));
            }

            MapleQuestRequirement req = this.getRequirement(questId, type, startReq);
            if (req == null) {
               continue;
            }
            questBuilder.addStartingRequirement(type, req);
         }
      }

      MapleData completeReqData = reqData.getChildByPath("1");
      if (completeReqData != null) {
         for (MapleData completeReq : completeReqData.getChildren()) {
            MapleQuestRequirementType type = MapleQuestRequirementType.getByWZName(completeReq.getName());
            MapleQuestRequirement req = this.getRequirement(questId, type, completeReq);

            if (req == null) {
               continue;
            }

            if (type.equals(MapleQuestRequirementType.MOB)) {
               completeReq.getChildren()
                     .forEach(mob -> questBuilder.addRelevantMob(MapleDataTool.getInt(mob.getChildByPath("id"))));
            }
            questBuilder.addCompletionRequirement(type, req);
         }
      }
      MapleData actData = questAct.getChildByPath(String.valueOf(questId));
      if (actData == null) {
         return questBuilder.build();
      }
      final MapleData startActData = actData.getChildByPath("0");
      if (startActData != null) {
         for (MapleData startAct : startActData.getChildren()) {
            MapleQuestActionType questActionType = MapleQuestActionType.getByWZName(startAct.getName());
            MapleQuestAction act = this.getAction(questId, questActionType, startAct);

            if (act == null) {
               continue;
            }
            questBuilder.addStartingAction(questActionType, act);
         }
      }
      MapleData completeActData = actData.getChildByPath("1");
      if (completeActData != null) {
         for (MapleData completeAct : completeActData.getChildren()) {
            MapleQuestActionType questActionType = MapleQuestActionType.getByWZName(completeAct.getName());
            MapleQuestAction act = this.getAction(questId, questActionType, completeAct);

            if (act == null) {
               continue;
            }
            questBuilder.addCompletionAction(questActionType, act);
         }
      }

      return questBuilder.build();
   }

   protected MapleQuestRequirement getRequirement(int questId, MapleQuestRequirementType type, MapleData data) {
      return switch (type) {
         case END_DATE -> new EndDateRequirement(questId, data);
         case JOB -> new JobRequirement(questId, data);
         case QUEST -> new QuestRequirement(questId, data);
         case FIELD_ENTER -> new FieldEnterRequirement(questId, data);
         case INFO_NUMBER -> new InfoNumberRequirement(questId, data);
         case INFO_EX -> new InfoExRequirement(questId, data);
         case INTERVAL -> new IntervalRequirement(questId, data);
         case COMPLETED_QUEST -> new CompletedQuestRequirement(questId, data);
         case ITEM -> new ItemRequirement(questId, data);
         case MAX_LEVEL -> new MaxLevelRequirement(questId, data);
         case MESO -> new MesoRequirement(questId, data);
         case MIN_LEVEL -> new MinLevelRequirement(questId, data);
         case MIN_PET_TAMENESS -> new MinTamenessRequirement(questId, data);
         case MOB -> new MobRequirement(questId, data);
         case MONSTER_BOOK -> new MonsterBookCountRequirement(questId, data);
         case NPC -> new NpcRequirement(questId, data);
         case PET -> new PetRequirement(questId, data);
         case BUFF -> new BuffRequirement(questId, data);
         case EXCEPT_BUFF -> new BuffExceptRequirement(questId, data);
         case SCRIPT -> new ScriptRequirement(questId, data);
         default -> {
            LoggerUtil.printError(LoggerOriginator.ENGINE, LogType.EXCEPTION_CAUGHT,
                  "Unhandled Requirement Type: " + type.toString() + " QuestID: " + questId);
            yield null;
         }
      };
   }

   protected MapleQuestAction getAction(int questId, MapleQuestActionType type, MapleData data) {
      return switch (type) {
         case BUFF -> new BuffAction(questId, data);
         case EXP -> new ExpAction(questId, data);
         case FAME -> new FameAction(questId, data);
         case ITEM -> new ItemAction(questId, data);
         case MESO -> new MesoAction(questId, data);
         case NEXT_QUEST -> new NextQuestAction(questId, data);
         case PET_SKILL -> new PetSkillAction(questId, data);
         case QUEST -> new QuestAction(questId, data);
         case SKILL -> new SkillAction(questId, data);
         case PET_TAMENESS -> new PetTamenessAction(questId, data);
         case PET_SPEED -> new PetSpeedAction(questId, data);
         case INFO -> new InfoAction(questId, data);
         default -> {
            LoggerUtil.printError(LoggerOriginator.ENGINE, LogType.EXCEPTION_CAUGHT,
                  "Unhandled Action Type: " + type.toString() + " QuestID: " + questId);
            yield null;
         }
      };
   }

   public MapleQuest getQuest(int questId) {
      MapleQuest result = quests.get(questId);
      if (result == null) {
         result = createQuest((short) questId);
         quests.put(questId, result);
      }
      return result;
   }

   public void clearCache() {
      quests.clear();
   }

   public void clearCache(int questId) {
      quests.remove(questId);
   }

   public boolean isExploitableQuest(short questId) {
      return exploitableQuests.contains(questId);
   }

   public List<MapleQuest> getMatchedQuests(String search) {
      return quests.values().stream()
            .filter(mq -> mq.name().toLowerCase().contains(search.toLowerCase()) || mq.parent().toLowerCase()
                  .contains(search.toLowerCase()))
            .collect(Collectors.toList());
   }

   public MapleQuest getInstanceFromInfoNumber(int infoNumber) {
      Integer id = infoNumberQuests.get(infoNumber);
      if (id == null) {
         id = infoNumber;
      }
      return getQuest(id);
   }

   public short getInfoNumber(MapleQuestStatus questStatus) {
      return getQuest(questStatus.getQuestId()).getInfoNumber(questStatus.getStatus());
   }

   protected boolean progress(MapleQuest quest, MapleQuestStatus questStatus, int mobId) {
      String currentStr = questStatus.getProgress().get(mobId);
      if (currentStr == null) {
         return false;
      }
      int current = Integer.parseInt(currentStr);
      if (current >= quest.getMobAmountNeeded(mobId)) {
         return false;
      }

      String str = StringUtil.getLeftPaddedStr(Integer.toString(++current), '0', 3);
      questStatus.setProgress(mobId, str);
      return true;
   }

   public void raiseQuestMobCount(MapleCharacter character, int mobId) {
      // It seems nexon uses monsters that don't exist in the WZ (except string) to merge multiple mobs together for these 3 monsters.
      // We also want to run mobKilled for both since there are some quest that don't use the updated ID...
      if (mobId == 1110100 || mobId == 1110130) {
         raiseQuestMobCount(character, 9101000);
      } else if (mobId == 2230101 || mobId == 2230131) {
         raiseQuestMobCount(character, 9101001);
      } else if (mobId == 1140100 || mobId == 1140130) {
         raiseQuestMobCount(character, 9101002);
      }

      int lastQuestProcessed = 0;
      try {
         synchronized (quests) {
            for (MapleQuestStatus qs : character.getQuests()) {
               MapleQuest quest = getQuest(qs.getQuestId());
               lastQuestProcessed = quest.id();
               if (qs.getStatus() == MapleQuestStatus.Status.COMPLETED || canComplete(character, quest, null)) {
                  continue;
               }
               if (progress(quest, qs, mobId)) {
                  character.announceUpdateQuest(MapleCharacter.DelayedQuestUpdate.UPDATE, qs, false);
                  if (getInfoNumber(qs) > 0) {
                     character.announceUpdateQuest(MapleCharacter.DelayedQuestUpdate.UPDATE, qs, true);
                  }
               }
            }
         }
      } catch (Exception e) {
         LoggerUtil.printError(LoggerOriginator.ENGINE, LogType.EXCEPTION_CAUGHT, e,
               "MapleCharacter.mobKilled. CID: " + mobId + " last Quest Processed: " + lastQuestProcessed);
      }
   }

   public void setQuestProgress(MapleCharacter character, int questId, int infoNumber, String progress) {
      MapleQuest q = getQuest(questId);
      MapleQuestStatus qs = character.getQuest(q);

      if (QuestProcessor.getInstance().getInfoNumber(qs) == infoNumber && infoNumber > 0) {
         MapleQuest iq = QuestProcessor.getInstance().getQuest(infoNumber);
         MapleQuestStatus iqs = character.getQuest(iq);
         iqs.setProgress(0, progress);
      } else {
         qs.setProgress(infoNumber,
               progress);   // quest progress is thoroughly a string match, infoNumber is actually another quest id
      }

      character.announceUpdateQuest(MapleCharacter.DelayedQuestUpdate.UPDATE, qs, false);
      if (QuestProcessor.getInstance().getInfoNumber(qs) > 0) {
         character.announceUpdateQuest(MapleCharacter.DelayedQuestUpdate.UPDATE, qs, true);
      }
   }

   public void updateQuestStatus(MapleCharacter character, MapleQuestStatus questStatus) {
      character.addQuest(questStatus.getQuestId(), questStatus);

      if (questStatus.getStatus().equals(MapleQuestStatus.Status.STARTED)) {
         character.announceUpdateQuest(MapleCharacter.DelayedQuestUpdate.UPDATE, questStatus, false);
         if (QuestProcessor.getInstance().getInfoNumber(questStatus) > 0) {
            character.announceUpdateQuest(MapleCharacter.DelayedQuestUpdate.UPDATE, questStatus, true);
         }
         character.announceUpdateQuest(MapleCharacter.DelayedQuestUpdate.INFO, questStatus);
      } else if (questStatus.getStatus().equals(MapleQuestStatus.Status.COMPLETED)) {
         MapleQuest quest = QuestProcessor.getInstance().getQuest(questStatus.getQuestId());
         short questId = quest.id();
         if (!quest.isSameDayRepeatable() && !QuestProcessor.getInstance().isExploitableQuest(questId)) {
            character.awardQuestPoint(YamlConfig.config.server.QUEST_POINT_PER_QUEST_COMPLETE);
         }
         questStatus.setCompleted(questStatus.getCompleted() + 1);

         character.announceUpdateQuest(MapleCharacter.DelayedQuestUpdate.COMPLETE, questId, questStatus.getCompletionTime());
      } else if (questStatus.getStatus().equals(MapleQuestStatus.Status.NOT_STARTED)) {
         character.announceUpdateQuest(MapleCharacter.DelayedQuestUpdate.UPDATE, questStatus, false);
         if (QuestProcessor.getInstance().getInfoNumber(questStatus) > 0) {
            character.announceUpdateQuest(MapleCharacter.DelayedQuestUpdate.UPDATE, questStatus, true);
         }
      }
   }

   protected boolean canStartQuestByStatus(MapleCharacter character, MapleQuest quest) {
      MapleQuestStatus mqs = character.getQuest(quest);
      return !(!mqs.getStatus().equals(MapleQuestStatus.Status.NOT_STARTED) && !(
            mqs.getStatus().equals(MapleQuestStatus.Status.COMPLETED) && quest.repeatable()));
   }

   protected boolean canQuestByInfoProgress(MapleCharacter character, MapleQuest quest) {
      MapleQuestStatus mqs = character.getQuest(quest);
      List<String> ix = quest.getInfoEx(mqs.getStatus());
      if (!ix.isEmpty()) {
         short questId = mqs.getQuestId();
         short infoNumber = quest.getInfoNumber(mqs.getStatus());
         if (infoNumber <= 0) {
            infoNumber = questId;  // on default infoNumber mimics quest id
         }

         int ixSize = ix.size();
         for (int i = 0; i < ixSize; i++) {
            String progress = character.getAbstractPlayerInteraction().getQuestProgress(infoNumber, i);
            String ixProgress = ix.get(i);

            if (!progress.contentEquals(ixProgress)) {
               return false;
            }
         }
      }

      return true;
   }

   public void startQuest(MapleCharacter character, MapleQuest quest, int npcId, int x, int y) {
      if (!isNpcNearby(character, quest, npcId, x, y)) {
         return;
      }

      if (canStart(character, quest, npcId)) {
         start(character, quest, npcId);
      }
   }

   public void startScriptedQuest(MapleCharacter character, MapleQuest quest, int npcId, int x, int y) {
      if (!isNpcNearby(character, quest, npcId, x, y)) {
         return;
      }

      if (canStart(character, quest, npcId)) {
         QuestScriptManager.getInstance().start(character.getClient(), quest.id(), npcId);
      }
   }

   protected boolean canStart(MapleCharacter character, MapleQuest quest, int npcId) {
      if (!canStartQuestByStatus(character, quest)) {
         return false;
      }

      for (MapleQuestRequirement r : quest.startReqs().values()) {
         if (!r.check(character, npcId)) {
            return false;
         }
      }

      return canQuestByInfoProgress(character, quest);
   }

   public void completeQuest(MapleCharacter character, MapleQuest quest, int npcId, int selection, int x, int y) {
      if (!isNpcNearby(character, quest, npcId, x, y)) {
         return;
      }

      if (canComplete(character, quest, npcId)) {
         if (selection > -1) {
            complete(character, quest, npcId, selection);
         } else {
            complete(character, quest, npcId);
         }
      }
   }

   public void completeScriptedQuest(MapleCharacter character, MapleQuest quest, int npcId, int x, int y) {
      if (!isNpcNearby(character, quest, npcId, x, y)) {
         return;
      }

      if (canComplete(character, quest, npcId)) {
         QuestScriptManager.getInstance().end(character.getClient(), quest.id(), npcId);
      }
   }

   protected boolean canComplete(MapleCharacter character, MapleQuest quest, Integer npcId) {
      MapleQuestStatus mqs = character.getQuest(quest);
      if (!mqs.getStatus().equals(MapleQuestStatus.Status.STARTED)) {
         return false;
      }

      for (MapleQuestRequirement r : quest.completeReqs().values()) {
         if (!r.check(character, npcId)) {
            return false;
         }
      }

      return canQuestByInfoProgress(character, quest);
   }

   protected void start(MapleCharacter character, MapleQuest quest, int npc) {
      if (quest.autoStart() || canStart(character, quest, npc)) {
         Collection<MapleQuestAction> acts = quest.startActs().values();
         for (MapleQuestAction a : acts) {
            if (!a.check(character, null)) { // would null be good ?
               return;
            }
         }
         for (MapleQuestAction a : acts) {
            a.run(character, null);
         }
         forceStart(character, quest, npc);
      }
   }

   protected void complete(MapleCharacter character, MapleQuest quest, int npc) {
      complete(character, quest, npc, null);
   }

   protected void complete(MapleCharacter character, MapleQuest quest, int npc, Integer selection) {
      if (quest.autoPreComplete() || canComplete(character, quest, npc)) {
         Collection<MapleQuestAction> acts = quest.completeActs().values();
         for (MapleQuestAction a : acts) {
            if (!a.check(character, selection)) {
               return;
            }
         }
         forceComplete(character, quest, npc);
         for (MapleQuestAction a : acts) {
            a.run(character, selection);
         }
         if (!quest.hasNextQuestAction()) {
            character.announceUpdateQuest(MapleCharacter.DelayedQuestUpdate.INFO, character.getQuest(quest));
         }
      }
   }

   public void reset(MapleCharacter character, int questId) {
      reset(character, getQuest(questId));
   }

   public void reset(MapleCharacter character, MapleQuest quest) {
      MapleQuestStatus newStatus = new MapleQuestStatus(quest, MapleQuestStatus.Status.NOT_STARTED);
      updateQuestStatus(character, newStatus);
   }

   public boolean forfeit(MapleCharacter character, MapleQuest quest) {
      if (!character.getQuest(quest).getStatus().equals(MapleQuestStatus.Status.STARTED)) {
         return false;
      }
      if (quest.timeLimit() > 0) {
         PacketCreator.announce(character, new RemoveQuestTimeLimit(quest.id()));
      }
      MapleQuestStatus newStatus = new MapleQuestStatus(quest, MapleQuestStatus.Status.NOT_STARTED);
      newStatus.setForfeited(character.getQuest(quest).getForfeited() + 1);
      updateQuestStatus(character, newStatus);
      return true;
   }

   public boolean forceStart(MapleCharacter character, int questId, int npc) {
      return forceStart(character, getQuest(questId), npc);
   }

   public boolean forceStart(MapleCharacter character, MapleQuest quest, int npc) {
      MapleQuestStatus newStatus = new MapleQuestStatus(quest, MapleQuestStatus.Status.STARTED, npc);

      MapleQuestStatus oldStatus = character.getQuest(quest.id());
      for (Map.Entry<Integer, String> e : oldStatus.getProgress().entrySet()) {
         newStatus.setProgress(e.getKey(), e.getValue());
      }

      if (quest.id() / 100 == 35 && YamlConfig.config.server.TOT_MOB_QUEST_REQUIREMENT > 0) {
         int setProgress = 999 - Math.min(999, YamlConfig.config.server.TOT_MOB_QUEST_REQUIREMENT);

         for (Integer pid : newStatus.getProgress().keySet()) {
            if (pid >= 8200000 && pid <= 8200012) {
               String pr = StringUtil.getLeftPaddedStr(Integer.toString(setProgress), '0', 3);
               newStatus.setProgress(pid, pr);
            }
         }
      }

      newStatus.setForfeited(character.getQuest(quest).getForfeited());
      newStatus.setCompleted(character.getQuest(quest).getCompleted());

      if (quest.timeLimit() > 0) {
         newStatus.setExpirationTime(System.currentTimeMillis() + (quest.timeLimit() * 1000));
         character.questTimeLimit(quest, quest.timeLimit());
      }
      if (quest.timeLimit2() > 0) {
         newStatus.setExpirationTime(System.currentTimeMillis() + quest.timeLimit2());
         character.questTimeLimit2(quest, newStatus.getExpirationTime());
      }

      updateQuestStatus(character, newStatus);

      return true;
   }

   public boolean forceComplete(MapleCharacter character, int questId, int npc) {
      return forceComplete(character, getQuest(questId), npc);
   }

   protected boolean forceComplete(MapleCharacter character, MapleQuest quest, int npc) {
      if (quest.timeLimit() > 0) {
         PacketCreator.announce(character, new RemoveQuestTimeLimit(quest.id()));
      }

      MapleQuestStatus newStatus = new MapleQuestStatus(quest, MapleQuestStatus.Status.COMPLETED, npc);
      newStatus.setForfeited(character.getQuest(quest).getForfeited());
      newStatus.setCompleted(character.getQuest(quest).getCompleted());
      newStatus.setCompletionTime(System.currentTimeMillis());
      updateQuestStatus(character, newStatus);

      PacketCreator.announce(character, new ShowSpecialEffect(9)); // Quest completion
      character.getMap().broadcastMessage(character, new ShowForeignEffect(character.getId(), 9)); //use 9 instead of 12 for both
      return true;
   }

   public void expireQuest(MapleCharacter character, MapleQuest quest) {
      if (forfeit(character, quest)) {
         PacketCreator.announce(character, new QuestExpire(quest.id()));
      }
   }

   protected boolean isNpcNearby(MapleCharacter player, MapleQuest quest, int npcId, int x, int y) {
      Point playerP;
      Point pos = player.position();

      if (x != -1 && y != -1) {
         playerP = new Point(x, y);
         if (playerP.distance(pos) > 1000) {
            playerP = pos;
         }
      } else {
         playerP = pos;
      }

      if (!quest.autoStart() && !quest.isAutoComplete()) {
         MapleNPC npc = player.getMap().getNPCById(npcId);
         if (npc == null) {
            return false;
         }

         Point npcP = npc.position();
         if (Math.abs(npcP.getX() - playerP.getX()) > 1200 || Math.abs(npcP.getY() - playerP.getY()) > 800) {
            MessageBroadcaster.getInstance()
                  .sendServerNotice(player, ServerNoticeType.PINK_TEXT, I18nMessage.from("NPC_MOVE_CLOSER_TO_NPC"));
            return false;
         }
      }

      return true;
   }

   public void restoreLostItem(MapleCharacter character, MapleQuest quest, int itemId) {
      if (character.getQuest(quest).getStatus().equals(MapleQuestStatus.Status.STARTED)) {
         ItemAction itemAct = (ItemAction) quest.startActs().get(MapleQuestActionType.ITEM);
         if (itemAct != null) {
            itemAct.restoreLostItem(character, itemId);
         }
      }
   }
}
