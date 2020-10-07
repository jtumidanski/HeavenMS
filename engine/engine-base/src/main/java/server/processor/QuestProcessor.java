package server.processor;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import client.MapleQuestStatus;
import provider.MapleData;
import provider.MapleDataProvider;
import provider.MapleDataProviderFactory;
import provider.MapleDataTool;
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
import tools.LogType;
import tools.LoggerOriginator;
import tools.LoggerUtil;

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

   protected MapleQuest createQuest(short id) {
      QuestBuilder questBuilder = new QuestBuilder(id);

      MapleData reqData = questReq.getChildByPath(String.valueOf(id));
      if (reqData == null) { //most likely infoEx
         return questBuilder.build();
      }

      if (questInfo != null) {
         MapleData reqInfo = questInfo.getChildByPath(String.valueOf(id));
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
            LoggerUtil.printError(LoggerOriginator.ENGINE, LogType.QUEST, "no data " + id);
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

            MapleQuestRequirement req = this.getRequirement(id, type, startReq);
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
            MapleQuestRequirement req = this.getRequirement(id, type, completeReq);

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
      MapleData actData = questAct.getChildByPath(String.valueOf(id));
      if (actData == null) {
         return questBuilder.build();
      }
      final MapleData startActData = actData.getChildByPath("0");
      if (startActData != null) {
         for (MapleData startAct : startActData.getChildren()) {
            MapleQuestActionType questActionType = MapleQuestActionType.getByWZName(startAct.getName());
            MapleQuestAction act = this.getAction(id, questActionType, startAct);

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
            MapleQuestAction act = this.getAction(id, questActionType, completeAct);

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

   public MapleQuest getQuest(int id) {
      MapleQuest result = quests.get(id);
      if (result == null) {
         result = createQuest((short) id);
         quests.put(id, result);
      }
      return result;
   }

   public void clearCache() {
      quests.clear();
   }

   public void clearCache(int quest) {
      quests.remove(quest);
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
}
