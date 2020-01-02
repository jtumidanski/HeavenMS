package client;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import server.quest.MapleQuest;
import tools.StringUtil;

public class MapleQuestStatus {
   //private boolean updated;   //maybe this can be of use for someone?
   private final Map<Integer, String> progress = new LinkedHashMap<>();
   private final List<Integer> medalProgress = new LinkedList<>();
   private short questID;
   private Status status;
   private int npc;
   private long completionTime, expirationTime;
   private int forfeited = 0, completed = 0;
   private String customData;
   public MapleQuestStatus(MapleQuest quest, Status status) {
      this.questID = quest.getId();
      this.setStatus(status);
      this.completionTime = System.currentTimeMillis();
      this.expirationTime = 0;
      //this.updated = true;
      if (status == Status.STARTED)
         registerMobs();
   }

   public MapleQuestStatus(MapleQuest quest, Status status, int npc) {
      this.questID = quest.getId();
      this.setStatus(status);
      this.setNpc(npc);
      this.completionTime = System.currentTimeMillis();
      this.expirationTime = 0;
      //this.updated = true;
      if (status == Status.STARTED) {
         registerMobs();
      }
   }

   public MapleQuest getQuest() {
      return MapleQuest.getInstance(questID);
   }

   public short getQuestID() {
      return questID;
   }

   public Status getStatus() {
      return status;
   }

   public final void setStatus(Status status) {
      this.status = status;
   }

   public int getNpc() {
      return npc;
   }

   public final void setNpc(int npc) {
      this.npc = npc;
   }

   private void registerMobs() {
      for (int i : MapleQuest.getInstance(questID).getRelevantMobs()) {
         progress.put(i, "000");
      }
   }

   public boolean addMedalMap(int mapId) {
      if (medalProgress.contains(mapId)) return false;
      medalProgress.add(mapId);
      return true;
   }

   public int getMedalProgress() {
      return medalProgress.size();
   }

   public List<Integer> getMedalMaps() {
      return medalProgress;
   }

   public boolean progress(int id) {
      String currentStr = progress.get(id);
      if (currentStr == null) {
         return false;
      }
      int current = Integer.parseInt(currentStr);
      if (current >= this.getQuest().getMobAmountNeeded(id)) {
         return false;
      }

      String str = StringUtil.getLeftPaddedStr(Integer.toString(++current), '0', 3);
      progress.put(id, str);
      return true;
   }

   public void setProgress(int id, String pr) {
      progress.put(id, pr);
   }

   public boolean madeProgress() {
      return progress.size() > 0;
   }

   public String getProgress(int id) {
      String ret = progress.get(id);
      return Objects.requireNonNullElse(ret, "");
   }

   public void resetProgress(int id) {
      setProgress(id, "000");
   }

   public void resetAllProgress() {
      for (Map.Entry<Integer, String> entry : progress.entrySet()) {
         setProgress(entry.getKey(), "000");
      }
   }

   public Map<Integer, String> getProgress() {
      return Collections.unmodifiableMap(progress);
   }

   public short getInfoNumber() {
      MapleQuest q = this.getQuest();
      Status s = this.getStatus();

      return q.getInfoNumber(s);
   }

   public String getInfoEx(int index) {
      MapleQuest q = this.getQuest();
      Status s = this.getStatus();

      return q.getInfoEx(s, index);
   }

   public List<String> getInfoEx() {
      MapleQuest q = this.getQuest();
      Status s = this.getStatus();

      return q.getInfoEx(s);
   }

   public long getCompletionTime() {
      return completionTime;
   }

   public void setCompletionTime(long completionTime) {
      this.completionTime = completionTime;
   }

   public long getExpirationTime() {
      return expirationTime;
   }

   public void setExpirationTime(long expirationTime) {
      this.expirationTime = expirationTime;
   }

   public int getForfeited() {
      return forfeited;
   }

   public void setForfeited(int forfeited) {
      if (forfeited >= this.forfeited) {
         this.forfeited = forfeited;
      } else {
         throw new IllegalArgumentException("Can't set forfeits to something lower than before.");
      }
   }

   public int getCompleted() {
      return completed;
   }

   public void setCompleted(int completed) {
      if (completed >= this.completed) {
         this.completed = completed;
      } else {
         throw new IllegalArgumentException("Can't set completes to something lower than before.");
      }
   }

   public final String getCustomData() {
      return customData;
   }

   public final void setCustomData(final String customData) {
      this.customData = customData;
   }

   public String getProgressData() {
      StringBuilder str = new StringBuilder();
      for (String ps : progress.values()) {
         str.append(ps);
      }
      return str.toString();
   }

   public enum Status {
      UNDEFINED(-1),
      NOT_STARTED(0),
      STARTED(1),
      COMPLETED(2);
      final int status;

      Status(int id) {
         status = id;
      }

      public static Status getById(int id) {
         for (Status l : Status.values()) {
            if (l.getId() == id) {
               return l;
            }
         }
         return null;
      }

      public int getId() {
         return status;
      }
   }
}