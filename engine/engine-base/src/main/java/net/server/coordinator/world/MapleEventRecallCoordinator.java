package net.server.coordinator.world;

import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import config.YamlConfig;
import scripting.event.EventInstanceManager;

public class MapleEventRecallCoordinator {

   private final static MapleEventRecallCoordinator instance = new MapleEventRecallCoordinator();
   private ConcurrentHashMap<Integer, EventInstanceManager> eventHistory = new ConcurrentHashMap<>();

   public static MapleEventRecallCoordinator getInstance() {
      return instance;
   }

   private static boolean isRevocableEvent(EventInstanceManager eim) {
      return eim != null && !eim.isEventDisposed() && !eim.isEventCleared();
   }

   public EventInstanceManager recallEventInstance(int characterId) {
      EventInstanceManager eim = eventHistory.remove(characterId);
      return isRevocableEvent(eim) ? eim : null;
   }

   public void storeEventInstance(int characterId, EventInstanceManager eim) {
      if (YamlConfig.config.server.USE_ENABLE_RECALL_EVENT && isRevocableEvent(eim)) {
         eventHistory.put(characterId, eim);
      }
   }

   public void manageEventInstances() {
      if (!eventHistory.isEmpty()) {
         List<Integer> toRemove = new LinkedList<>();

         for (Entry<Integer, EventInstanceManager> eh : eventHistory.entrySet()) {
            if (!isRevocableEvent(eh.getValue())) {
               toRemove.add(eh.getKey());
            }
         }

         for (Integer r : toRemove) {
            eventHistory.remove(r);
         }
      }
   }
}
