package net.server.coordinator.world;

import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import client.MapleCharacter;
import tools.Pair;

public class MapleInviteCoordinator {

   // note: referenceFrom is a specific value that represents the "common association" created between the sender/receiver parties
   public static boolean createInvite(InviteType type, MapleCharacter from, Object referenceFrom, int targetCid, Object... params) {
      return type.addRequest(from, referenceFrom, targetCid, params);
   }

   public static boolean hasInvite(InviteType type, int targetCid) {
      return type.hasRequest(targetCid);
   }

   public static MapleInviteResult answerInvite(InviteType type, int targetCid, Object referenceFrom, boolean answer) {
      Map<Integer, Object> table = type.getRequestsTable();

      MapleCharacter from = null;
      InviteResult result = InviteResult.NOT_FOUND;
      Pair<MapleCharacter, Object[]> inviteInfo = null;

      Object reference = table.get(targetCid);
      if (referenceFrom.equals(reference)) {
         inviteInfo = type.removeRequest(targetCid);
         from = inviteInfo.getLeft();
         if (from != null && !from.isLoggedInWorld()) {
            from = null;
         }

         result = answer ? InviteResult.ACCEPTED : InviteResult.DENIED;
      }

      return new MapleInviteResult(result, from, inviteInfo != null ? inviteInfo.getRight() : new Object[0]);
   }

   public static void removeInvite(InviteType type, int targetCid) {
      type.removeRequest(targetCid);
   }

   public static void removePlayerIncomingInvites(int cid) {
      for (InviteType it : InviteType.values()) {
         it.removeRequest(cid);
      }
   }

   public static void runTimeoutSchedule() {
      for (InviteType it : InviteType.values()) {
         Map<Integer, Integer> timeoutTable = it.getRequestsTimeoutTable();

         if (!timeoutTable.isEmpty()) {
            Set<Entry<Integer, Integer>> entrySet = new HashSet<>(timeoutTable.entrySet());
            for (Entry<Integer, Integer> e : entrySet) {
               int eVal = e.getValue();

               if (eVal > 5) { // 3min to expire
                  it.removeRequest(e.getKey());
               } else {
                  timeoutTable.put(e.getKey(), eVal + 1);
               }
            }
         }
      }
   }

   public enum InviteResult {
      ACCEPTED,
      DENIED,
      NOT_FOUND
   }

   public enum InviteType {
      //BUDDY, (not needed)
      FAMILY,
      FAMILY_SUMMON,
      MESSENGER,
      TRADE,
      PARTY,
      GUILD,
      ALLIANCE;

      final ConcurrentHashMap<Integer, Object> invites;
      final ConcurrentHashMap<Integer, MapleCharacter> inviteFrom;
      final ConcurrentHashMap<Integer, Integer> inviteTimeouts;
      final ConcurrentHashMap<Integer, Object[]> inviteParams;

      InviteType() {
         invites = new ConcurrentHashMap<>();
         inviteTimeouts = new ConcurrentHashMap<>();
         inviteFrom = new ConcurrentHashMap<>();
         inviteParams = new ConcurrentHashMap<>();
      }

      private Map<Integer, Object> getRequestsTable() {
         return invites;
      }

      private Map<Integer, Integer> getRequestsTimeoutTable() {
         return inviteTimeouts;
      }

      private Pair<MapleCharacter, Object[]> removeRequest(Integer target) {
         invites.remove(target);
         MapleCharacter from = inviteFrom.remove(target);
         inviteTimeouts.remove(target);

         return new Pair<>(from, inviteParams.remove(target));
      }

      private boolean addRequest(MapleCharacter from, Object referenceFrom, int targetCid, Object[] params) {
         Object v = invites.putIfAbsent(targetCid, referenceFrom);
         if (v != null) {    // there was already an entry
            return false;
         }

         inviteFrom.put(targetCid, from);
         inviteTimeouts.put(targetCid, 0);
         inviteParams.put(targetCid, params);
         return true;
      }

      private boolean hasRequest(int targetCid) {
         return invites.containsKey(targetCid);
      }
   }

   public static class MapleInviteResult {

      public final InviteResult result;
      public final MapleCharacter from;
      public final Object[] params;

      private MapleInviteResult(InviteResult result, MapleCharacter from, Object[] params) {
         this.result = result;
         this.from = from;
         this.params = params;
      }
   }
}
