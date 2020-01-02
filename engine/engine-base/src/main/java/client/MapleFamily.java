package client;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import client.processor.MapleFamilyProcessor;

public class MapleFamily {
   private static final AtomicInteger familyIDCounter = new AtomicInteger();
   private final int id, world;
   private final Map<Integer, MapleFamilyEntry> members = new ConcurrentHashMap<>();
   private MapleFamilyEntry leader;
   private String name;
   private String preceptsMessage = "";
   private int totalGenerations;

   public MapleFamily(int id, int world) {
      int newId = id;
      if (id == -1) {
         // get next available family id
         while (MapleFamilyProcessor.getInstance().idInUse(newId = familyIDCounter.incrementAndGet())) {
         }
      }
      this.id = newId;
      this.world = world;
   }

   public int getID() {
      return id;
   }

   public int getWorld() {
      return world;
   }

   public void setLeader(MapleFamilyEntry leader) {
      this.leader = leader;
      setName(leader.getName());
   }

   public MapleFamilyEntry getLeader() {
      return leader;
   }

   private void setName(String name) {
      this.name = name;
   }

   public int getTotalMembers() {
      return members.size();
   }

   public int getTotalGenerations() {
      return totalGenerations;
   }

   public void setTotalGenerations(int generations) {
      this.totalGenerations = generations;
   }

   public String getName() {
      return this.name;
   }

   public void setMessage(String message) {
      this.preceptsMessage = message;
   }

   public String getMessage() {
      return preceptsMessage;
   }

   public void addEntry(MapleFamilyEntry entry) {
      members.put(entry.getChrId(), entry);
   }

   public void removeEntryBranch(MapleFamilyEntry root) {
      members.remove(root.getChrId());
      for (MapleFamilyEntry junior : root.getJuniors()) {
         if (junior != null) {
            removeEntryBranch(junior);
         }
      }
   }

   public void addEntryTree(MapleFamilyEntry root) {
      members.put(root.getChrId(), root);
      for (MapleFamilyEntry junior : root.getJuniors()) {
         if (junior != null) {
            addEntryTree(junior);
         }
      }
   }

   public MapleFamilyEntry getEntryByID(int cid) {
      return members.get(cid);
   }

   public void resetDailyReps() {
      for (MapleFamilyEntry entry : members.values()) {
         entry.setTodaysRep(0);
         entry.setRepsToSenior(0);
         entry.resetEntitlementUsages();
      }
   }

   public Collection<MapleFamilyEntry> getMembers() {
      return members.values();
   }
}
