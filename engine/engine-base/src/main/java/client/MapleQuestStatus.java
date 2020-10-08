package client;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public record MapleQuestStatus(short questId, QuestStatus status, int npcId, long completionTime, long expirationTime,
                               int forfeited, int completed, String customData, Map<Integer, String> progress,
                               List<Integer> medalMaps) {
   public boolean hasMedalMap(int mapId) {
      return medalMaps.contains(mapId);
   }

   public MapleQuestStatus addMedalMap(int mapId) {
      if (medalMaps.contains(mapId)) {
         return this;
      }
      return new MapleQuestStatusBuilder(this).addMedalMap(mapId).build();
   }

   public int getMedalProgress() {
      return medalMaps.size();
   }

   public MapleQuestStatus setProgress(int id, String pr) {
      return new MapleQuestStatusBuilder(this).setProgress(id, pr).build();
   }

   public boolean madeProgress() {
      return progress.size() > 0;
   }

   public String getProgress(int id) {
      String ret = progress.get(id);
      return Objects.requireNonNullElse(ret, "");
   }

   public MapleQuestStatus resetProgress(int id) {
      return new MapleQuestStatusBuilder(this).setProgress(id, "000").build();
   }

   public MapleQuestStatus resetAllProgress() {
      return new MapleQuestStatusBuilder(this).resetAllProgress().build();
   }

   public MapleQuestStatus setCompletionTime(long completionTime) {
      return new MapleQuestStatusBuilder(this).setCompletionTime(completionTime).build();
   }

   public MapleQuestStatus setExpirationTime(long expirationTime) {
      return new MapleQuestStatusBuilder(this).setExpirationTime(expirationTime).build();
   }

   public MapleQuestStatus setForfeited(int forfeited) {
      if (forfeited >= this.forfeited) {
         return new MapleQuestStatusBuilder(this).setForfeited(forfeited).build();
      } else {
         throw new IllegalArgumentException("Can't set forfeits to something lower than before.");
      }
   }

   public MapleQuestStatus setCompleted(int completed) {
      if (completed >= this.completed) {
         return new MapleQuestStatusBuilder(this).setCompleted(completed).build();
      } else {
         throw new IllegalArgumentException("Can't set completes to something lower than before.");
      }
   }

   public MapleQuestStatus setCustomData(final String customData) {
      return new MapleQuestStatusBuilder(this).setCustomData(customData).build();
   }

   public String getProgressData() {
      StringBuilder str = new StringBuilder();
      progress.values().forEach(str::append);
      return str.toString();
   }
}