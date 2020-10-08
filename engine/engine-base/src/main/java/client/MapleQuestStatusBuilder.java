package client;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import builder.RecordBuilder;
import server.quest.MapleQuest;

public class MapleQuestStatusBuilder extends RecordBuilder<MapleQuestStatus, MapleQuestStatusBuilder> {
   private final Map<Integer, String> progress;

   private final List<Integer> medalMaps;

   public MapleQuestStatusBuilder(short questId, QuestStatus status) {
      setQuestId(questId);
      setStatus(status);
      setNpcId(0);
      setCompletionTime(System.currentTimeMillis());
      setExpirationTime(0);
      setForfeited(0);
      setCompleted(0);
      setCustomData("");
      progress = new HashMap<>();
      medalMaps = new LinkedList<>();
   }

   public MapleQuestStatusBuilder(MapleQuest quest, QuestStatus status) {
      setQuestId(quest.id());
      setStatus(status);
      setNpcId(0);
      setCompletionTime(System.currentTimeMillis());
      setExpirationTime(0);
      setForfeited(0);
      setCompleted(0);
      setCustomData("");
      progress = quest.relevantMobs().stream().collect(Collectors.toMap(id -> id, id -> "000"));
      medalMaps = new LinkedList<>();
   }

   public MapleQuestStatusBuilder(MapleQuest quest, QuestStatus status, int npcId) {
      setQuestId(quest.id());
      setStatus(status);
      setNpcId(npcId);
      setCompletionTime(System.currentTimeMillis());
      setExpirationTime(0);
      setForfeited(0);
      setCompleted(0);
      setCustomData("");
      progress = quest.relevantMobs().stream().collect(Collectors.toMap(id -> id, id -> "000"));
      medalMaps = new LinkedList<>();
   }

   public MapleQuestStatusBuilder(MapleQuestStatus other) {
      setQuestId(other.questId());
      setStatus(other.status());
      setNpcId(other.npcId());
      setCompletionTime(other.completionTime());
      setExpirationTime(other.expirationTime());
      setForfeited(other.forfeited());
      setCompleted(other.completed());
      setCustomData(other.customData());
      progress = other.progress();
      medalMaps = other.medalMaps();
   }

   @Override
   public MapleQuestStatusBuilder getThis() {
      return this;
   }

   @Override
   public MapleQuestStatus construct() {
      return new MapleQuestStatus(get("questId"), get("status"), get("npcId"), get("completionTime"), get("expirationTime"),
            get("forfeited"), get("completed"), get("customData"), progress, medalMaps);
   }

   public MapleQuestStatusBuilder setQuestId(short questId) {
      return set("questId", questId);
   }

   public MapleQuestStatusBuilder setStatus(QuestStatus status) {
      return set("status", status);
   }

   public MapleQuestStatusBuilder setNpcId(int npcId) {
      return set("npcId", npcId);
   }

   public MapleQuestStatusBuilder setCompletionTime(long completionTime) {
      return set("completionTime", completionTime);
   }

   public MapleQuestStatusBuilder setExpirationTime(long expirationTime) {
      return set("expirationTime", expirationTime);
   }

   public MapleQuestStatusBuilder setForfeited(int forfeited) {
      return set("forfeited", forfeited);
   }

   public MapleQuestStatusBuilder setCompleted(int completed) {
      return set("completed", completed);
   }

   public MapleQuestStatusBuilder setCustomData(String customData) {
      return set("customData", customData);
   }

   public MapleQuestStatusBuilder addMedalMap(int mapId) {
      if (!medalMaps.contains(mapId)) {
         medalMaps.add(mapId);
      }
      return getThis();
   }

   public MapleQuestStatusBuilder resetAllProgress() {
      progress.forEach((key, value) -> setProgress(key, "000"));
      return getThis();
   }

   public MapleQuestStatusBuilder setProgress(int id, String p) {
      progress.put(id, p);
      return getThis();
   }

   public Map<Integer, String> getProgress() {
      return Collections.unmodifiableMap(progress);
   }
}
