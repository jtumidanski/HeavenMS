package server.processor;

import java.awt.*;
import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;

import client.MapleCharacter;
import client.MapleQuestStatus;
import client.MapleQuestStatusBuilder;
import client.QuestStatus;
import client.database.data.CharacterData;
import client.database.data.QuestData;
import config.YamlConfig;
import database.DatabaseConnection;
import database.administrator.MedalMapAdministrator;
import database.administrator.QuestProgressAdministrator;
import database.administrator.QuestStatusAdministrator;
import database.provider.MedalMapProvider;
import database.provider.QuestProgressProvider;
import database.provider.QuestStatusProvider;
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
import tools.Pair;
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

            infoNumber = quest.getInfoNumber(QuestStatus.STARTED);
            if (infoNumber > 0) {
               infoNumberQuests.put(infoNumber, questId);
            }

            infoNumber = quest.getInfoNumber(QuestStatus.COMPLETED);
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
         default -> null;
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
         default -> null;
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

   public MapleQuestStatus getQuestStatus(MapleCharacter character, int questId) {
      return getQuestStatus(character, getQuest(questId));
   }

   public MapleQuestStatus getQuestStatus(MapleCharacter character, MapleQuest quest) {
      return character.getQuestStatus(quest.id(), () -> new MapleQuestStatusBuilder(quest, QuestStatus.NOT_STARTED).build());
   }

   public boolean questIsStatus(MapleCharacter character, int questId, QuestStatus status) {
      return questIsStatus(character, getQuest(questId), status);
   }

   public boolean questIsStatus(MapleCharacter character, MapleQuest quest, QuestStatus status) {
      return getQuestStatus(character, quest).status().equals(status);
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
      return getQuest(questStatus.questId()).getInfoNumber(questStatus.status());
   }

   protected Optional<MapleQuestStatus> progress(MapleQuest quest, MapleQuestStatus questStatus, int mobId) {
      String currentStr = questStatus.progress().get(mobId);
      if (currentStr == null) {
         return Optional.empty();
      }
      int current = Integer.parseInt(currentStr);
      if (current >= quest.getMobAmountNeeded(mobId)) {
         return Optional.empty();
      }

      String str = StringUtil.getLeftPaddedStr(Integer.toString(++current), '0', 3);
      MapleQuestStatus newStatus = questStatus.setProgress(mobId, str);
      return Optional.of(newStatus);
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
         for (MapleQuestStatus questStatus : character.getQuests()) {
            MapleQuest quest = getQuest(questStatus.questId());
            lastQuestProcessed = quest.id();
            if (questStatus.status() == QuestStatus.COMPLETED || canComplete(character, quest, null)) {
               continue;
            }
            Optional<MapleQuestStatus> progressedStatus = progress(quest, questStatus, mobId);
            progressedStatus.ifPresent(newStatus -> updateQuestStatus(character, newStatus));
         }
      } catch (Exception e) {
         LoggerUtil.printError(LoggerOriginator.ENGINE, LogType.EXCEPTION_CAUGHT, e,
               "MapleCharacter.mobKilled. CID: " + mobId + " last Quest Processed: " + lastQuestProcessed);
      }
   }

   public void setQuestProgress(MapleCharacter character, int questId, int infoNumber, String progress) {
      MapleQuestStatus qs = getQuestStatus(character, questId);

      if (getInfoNumber(qs) == infoNumber && infoNumber > 0) {
         qs = getQuestStatus(character, infoNumber).setProgress(0, progress);
      } else {
         // quest progress is thoroughly a string match, infoNumber is actually another quest id
         qs = qs.setProgress(infoNumber, progress);
      }

      updateQuestStatus(character, qs);
   }

   public void loadQuests(EntityManager entityManager, CharacterData characterData, MapleCharacter mapleCharacter) {
      Map<Integer, MapleQuestStatusBuilder> loadedQuestStatus = new LinkedHashMap<>();
      QuestStatusProvider.getInstance().getQuestData(entityManager, characterData.id()).forEach(questData -> {
         MapleQuest q = QuestProcessor.getInstance().getQuest(questData.questId());
         MapleQuestStatusBuilder builder = new MapleQuestStatusBuilder(q, QuestStatus.getById(questData.status()));
         if (questData.time() > -1) {
            builder.setCompletionTime(questData.time() * 1000);
         }

         if (questData.expires() > 0) {
            builder.setExpirationTime(questData.expires());
         }

         builder.setForfeited(questData.forfeited());
         builder.setCompleted(questData.completed());
         loadedQuestStatus.put(questData.questStatusId(), builder);
      });
      QuestProgressProvider.getInstance().getProgress(entityManager, characterData.id()).forEach(questProgress -> {
         MapleQuestStatusBuilder status = loadedQuestStatus.get(questProgress.questStatusId());
         if (status != null) {
            status.setProgress(questProgress.progressId(), questProgress.progress());
         }
      });
      MedalMapProvider.getInstance().get(entityManager, characterData.id()).forEach(medalMap -> {
         MapleQuestStatusBuilder status = loadedQuestStatus.get(medalMap.getLeft());
         if (status != null) {
            status.addMedalMap(medalMap.getRight());
         }
      });
      loadedQuestStatus.forEach((i, builder) -> {
         MapleQuestStatus status = builder.build();
         mapleCharacter.addQuest(status.questId(), status);
      });
      loadedQuestStatus.clear();
   }

   public void updateQuestStatus(MapleCharacter character, MapleQuestStatus questStatus) {
      if (questStatus.status().equals(QuestStatus.STARTED)) {
         character.announceUpdateQuest(MapleCharacter.DelayedQuestUpdate.UPDATE, questStatus, false);
         if (getInfoNumber(questStatus) > 0) {
            character.announceUpdateQuest(MapleCharacter.DelayedQuestUpdate.UPDATE, questStatus, true);
         }
         character.announceUpdateQuest(MapleCharacter.DelayedQuestUpdate.INFO, questStatus);
      } else if (questStatus.status().equals(QuestStatus.COMPLETED)) {
         MapleQuest quest = getQuest(questStatus.questId());
         short questId = quest.id();
         if (!quest.isSameDayRepeatable() && !isExploitableQuest(questId)) {
            character.awardQuestPoint(YamlConfig.config.server.QUEST_POINT_PER_QUEST_COMPLETE);
         }
         questStatus = questStatus.setCompleted(questStatus.completed() + 1);
         character.announceUpdateQuest(MapleCharacter.DelayedQuestUpdate.COMPLETE, questId, questStatus.completionTime());
      } else if (questStatus.status().equals(QuestStatus.NOT_STARTED)) {
         character.announceUpdateQuest(MapleCharacter.DelayedQuestUpdate.UPDATE, questStatus, false);
         if (getInfoNumber(questStatus) > 0) {
            character.announceUpdateQuest(MapleCharacter.DelayedQuestUpdate.UPDATE, questStatus, true);
         }
      }

      // Update local and database tracking.
      character.addQuest(questStatus.questId(), questStatus);
      persistQuestStatusUpdate(character, questStatus);
   }

   protected boolean canStartQuestByStatus(MapleCharacter character, MapleQuest quest) {
      MapleQuestStatus mqs = getQuestStatus(character, quest);
      return !(!mqs.status().equals(QuestStatus.NOT_STARTED) && !(
            mqs.status().equals(QuestStatus.COMPLETED) && quest.repeatable()));
   }

   protected boolean canQuestByInfoProgress(MapleCharacter character, MapleQuest quest) {
      MapleQuestStatus mqs = getQuestStatus(character, quest);
      List<String> ix = quest.getInfoEx(mqs.status());
      if (!ix.isEmpty()) {
         short questId = mqs.questId();
         short infoNumber = quest.getInfoNumber(mqs.status());
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
      MapleQuestStatus mqs = getQuestStatus(character, quest);
      if (!mqs.status().equals(QuestStatus.STARTED)) {
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
            character.announceUpdateQuest(MapleCharacter.DelayedQuestUpdate.INFO, getQuestStatus(character, quest));
         }
      }
   }

   public void reset(MapleCharacter character, int questId) {
      reset(character, getQuest(questId));
   }

   public void reset(MapleCharacter character, MapleQuest quest) {
      MapleQuestStatus newStatus = new MapleQuestStatusBuilder(quest, QuestStatus.NOT_STARTED).build();
      updateQuestStatus(character, newStatus);
   }

   public boolean forfeit(MapleCharacter character, MapleQuest quest) {
      if (!getQuestStatus(character, quest).status().equals(QuestStatus.STARTED)) {
         return false;
      }
      if (quest.timeLimit() > 0) {
         PacketCreator.announce(character, new RemoveQuestTimeLimit(quest.id()));
      }
      MapleQuestStatusBuilder newStatusBuilder = new MapleQuestStatusBuilder(quest, QuestStatus.NOT_STARTED);
      newStatusBuilder.setForfeited(getQuestStatus(character, quest).forfeited() + 1);
      updateQuestStatus(character, newStatusBuilder.build());
      return true;
   }

   public boolean forceStart(MapleCharacter character, int questId, int npc) {
      return forceStart(character, getQuest(questId), npc);
   }

   public boolean forceStart(MapleCharacter character, MapleQuest quest, int npc) {
      MapleQuestStatusBuilder newStatusBuilder = new MapleQuestStatusBuilder(quest, QuestStatus.STARTED, npc);

      MapleQuestStatus oldStatus = getQuestStatus(character, quest);
      for (Map.Entry<Integer, String> e : oldStatus.progress().entrySet()) {
         newStatusBuilder.setProgress(e.getKey(), e.getValue());
      }

      if (quest.id() / 100 == 35 && YamlConfig.config.server.TOT_MOB_QUEST_REQUIREMENT > 0) {
         int setProgress = 999 - Math.min(999, YamlConfig.config.server.TOT_MOB_QUEST_REQUIREMENT);

         for (Integer pid : newStatusBuilder.getProgress().keySet()) {
            if (pid >= 8200000 && pid <= 8200012) {
               String pr = StringUtil.getLeftPaddedStr(Integer.toString(setProgress), '0', 3);
               newStatusBuilder.setProgress(pid, pr);
            }
         }
      }

      newStatusBuilder.setForfeited(getQuestStatus(character, quest).forfeited());
      newStatusBuilder.setCompleted(getQuestStatus(character, quest).completed());

      if (quest.timeLimit() > 0) {
         newStatusBuilder.setExpirationTime(System.currentTimeMillis() + (quest.timeLimit() * 1000));
         character.questTimeLimit(quest, quest.timeLimit());
      }
      if (quest.timeLimit2() > 0) {
         long expirationTime = System.currentTimeMillis() + quest.timeLimit2();
         newStatusBuilder.setExpirationTime(expirationTime);
         character.questTimeLimit2(quest, expirationTime);
      }

      updateQuestStatus(character, newStatusBuilder.build());

      return true;
   }

   public boolean forceComplete(MapleCharacter character, int questId, int npc) {
      return forceComplete(character, getQuest(questId), npc);
   }

   protected boolean forceComplete(MapleCharacter character, MapleQuest quest, int npc) {
      if (quest.timeLimit() > 0) {
         PacketCreator.announce(character, new RemoveQuestTimeLimit(quest.id()));
      }

      MapleQuestStatusBuilder newStatusBuilder = new MapleQuestStatusBuilder(quest, QuestStatus.COMPLETED, npc);
      newStatusBuilder.setForfeited(getQuestStatus(character, quest).forfeited());
      newStatusBuilder.setCompleted(getQuestStatus(character, quest).completed());
      newStatusBuilder.setCompletionTime(System.currentTimeMillis());
      updateQuestStatus(character, newStatusBuilder.build());

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
      if (getQuestStatus(character, quest).status().equals(QuestStatus.STARTED)) {
         ItemAction itemAct = (ItemAction) quest.startActs().get(MapleQuestActionType.ITEM);
         if (itemAct != null) {
            itemAct.restoreLostItem(character, itemId);
         }
      }
   }

   public void persistQuestStatusUpdate(MapleCharacter character, MapleQuestStatus questStatus) {
      DatabaseConnection.getInstance().withConnection(entityManager -> {
         Optional<QuestData> existingStatus =
               QuestStatusProvider.getInstance().getQuestStatus(entityManager, character.getId(), questStatus.questId());
         QuestData questData;
         if (existingStatus.isPresent()) {
            questData = existingStatus.get();
            QuestStatusAdministrator.getInstance().update(entityManager, questData.questStatusId(), questStatus.status(),
                  questStatus.completionTime(), questStatus.expirationTime(), questStatus.forfeited(),
                  questStatus.completed());
         } else {
            questData = QuestStatusAdministrator.getInstance().create(entityManager, character.getId(), questStatus);
         }

         QuestProgressAdministrator.getInstance()
               .deleteForQuest(entityManager, character.getId(), questData.questStatusId());
         List<Pair<Integer, String>> progressData = questStatus.progress()
               .keySet()
               .stream()
               .map(key -> new Pair<>(key, questStatus.getProgress(key)))
               .collect(Collectors.toList());
         QuestProgressAdministrator.getInstance()
               .create(entityManager, character.getId(), questData.questStatusId(), progressData);
         MedalMapAdministrator.getInstance().deleteForQuest(entityManager, character.getId(),
               questData.questStatusId());
         MedalMapAdministrator.getInstance().create(entityManager, character.getId(), questData.questStatusId(),
               questStatus.medalMaps());
      });
   }

   public void deleteQuestProgressWhereCharacterId(EntityManager entityManager, int cid) {
      MedalMapAdministrator.getInstance().deleteForCharacter(entityManager, cid);
      QuestProgressAdministrator.getInstance().deleteForCharacter(entityManager, cid);
      QuestStatusAdministrator.getInstance().deleteForCharacter(entityManager, cid);
   }
}
