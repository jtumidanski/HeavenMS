package server.quest;

import java.util.EnumMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import builder.RecordBuilder;
import server.quest.actions.MapleQuestAction;
import server.quest.requirements.MapleQuestRequirement;

public class QuestBuilder extends RecordBuilder<MapleQuest, QuestBuilder> {
   protected Map<MapleQuestRequirementType, MapleQuestRequirement> startReqs = new EnumMap<>(MapleQuestRequirementType.class);

   protected Map<MapleQuestRequirementType, MapleQuestRequirement> completeReqs = new EnumMap<>(MapleQuestRequirementType.class);

   protected Map<MapleQuestActionType, MapleQuestAction> startActs = new EnumMap<>(MapleQuestActionType.class);

   protected Map<MapleQuestActionType, MapleQuestAction> completeActs = new EnumMap<>(MapleQuestActionType.class);

   protected List<Integer> relevantMobs = new LinkedList<>();

   @Override
   public QuestBuilder getThis() {
      return this;
   }

   @Override
   public MapleQuest construct() {
      return new MapleQuest(get("id"), get("name"), get("parent"), get("timeLimit"), get("timeLimit2"), get("autoStart"),
            get("autoPreComplete"), get("autoComplete"), get("repeatable"), get("medalId"), startReqs, completeReqs, startActs,
            completeActs, relevantMobs);
   }

   public QuestBuilder(short id) {
      setId(id);
      setName("");
      setParent("");
      setTimeLimit(0);
      setTimeLimit2(0);
      setAutoStart(false);
      setAutoPreComplete(false);
      setAutoComplete(false);
      setRepeatable(false);
      setMedalId(-1);
   }

   public QuestBuilder setId(short id) {
      return set("id", id);
   }

   public QuestBuilder setName(String name) {
      return set("name", name);
   }

   public QuestBuilder setParent(String parent) {
      return set("parent", parent);
   }

   public QuestBuilder setTimeLimit(int timeLimit) {
      return set("timeLimit", timeLimit);
   }

   public QuestBuilder setTimeLimit2(int timeLimit2) {
      return set("timeLimit2", timeLimit2);
   }

   public QuestBuilder setAutoStart(boolean autoStart) {
      return set("autoStart", autoStart);
   }

   public QuestBuilder setAutoPreComplete(boolean autoPreComplete) {
      return set("autoPreComplete", autoPreComplete);
   }

   public QuestBuilder setAutoComplete(boolean autoComplete) {
      return set("autoComplete", autoComplete);
   }

   public QuestBuilder setRepeatable(boolean repeatable) {
      return set("repeatable", repeatable);
   }

   public QuestBuilder setMedalId(int medalId) {
      return set("medalId", medalId);
   }

   public QuestBuilder addRelevantMob(Integer mobId) {
      this.relevantMobs.add(mobId);
      return getThis();
   }

   public QuestBuilder addStartingRequirement(MapleQuestRequirementType type, MapleQuestRequirement requirement) {
      this.startReqs.put(type, requirement);
      return getThis();
   }

   public QuestBuilder addCompletionRequirement(MapleQuestRequirementType type, MapleQuestRequirement requirement) {
      this.completeReqs.put(type, requirement);
      return getThis();
   }

   public QuestBuilder addStartingAction(MapleQuestActionType type, MapleQuestAction action) {
      this.startActs.put(type, action);
      return getThis();
   }

   public QuestBuilder addCompletionAction(MapleQuestActionType type, MapleQuestAction action) {
      this.completeActs.put(type, action);
      return getThis();
   }
}
