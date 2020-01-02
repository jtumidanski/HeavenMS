package net.server.coordinator.partysearch;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import client.MapleCharacter;
import net.server.audit.locks.MonitoredLockType;
import net.server.audit.locks.MonitoredReadLock;
import net.server.audit.locks.MonitoredReentrantReadWriteLock;
import net.server.audit.locks.MonitoredWriteLock;
import net.server.audit.locks.factory.MonitoredReadLockFactory;
import net.server.audit.locks.factory.MonitoredWriteLockFactory;
import tools.IntervalBuilder;

public class PartySearchStorage {

   private final MonitoredReentrantReadWriteLock psLock = new MonitoredReentrantReadWriteLock(MonitoredLockType.WORLD_PARTY_SEARCH_STORAGE, true);
   private final MonitoredReadLock psRLock = MonitoredReadLockFactory.createLock(psLock);
   private final MonitoredWriteLock psWLock = MonitoredWriteLockFactory.createLock(psLock);
   private List<PartySearchCharacter> storage = new ArrayList<>(20);
   private IntervalBuilder emptyIntervals = new IntervalBuilder();

   private static int bsearchStorage(List<PartySearchCharacter> storage, int level) {
      int st = 0, en = storage.size() - 1;

      int mid, idx;
      while (en >= st) {
         idx = (st + en) / 2;
         mid = storage.get(idx).getLevel();

         if (mid == level) {
            return idx;
         } else if (mid < level) {
            st = idx + 1;
         } else {
            en = idx - 1;
         }
      }

      return en;
   }

   public List<PartySearchCharacter> getStorageList() {
      psRLock.lock();
      try {
         return new ArrayList<>(storage);
      } finally {
         psRLock.unlock();
      }
   }

   private Map<Integer, MapleCharacter> fetchRemainingPlayers() {
      List<PartySearchCharacter> players = getStorageList();
      Map<Integer, MapleCharacter> remainingPlayers = new HashMap<>(players.size());

      for (PartySearchCharacter psc : players) {
         if (psc.isQueued()) {
            MapleCharacter chr = psc.getPlayer();
            if (chr != null) {
               remainingPlayers.put(chr.getId(), chr);
            }
         }
      }

      return remainingPlayers;
   }

   public void updateStorage(Collection<MapleCharacter> echelon) {
      Map<Integer, MapleCharacter> newcomers = new HashMap<>();
      for (MapleCharacter chr : echelon) {
         newcomers.put(chr.getId(), chr);
      }

      Map<Integer, MapleCharacter> curStorage = fetchRemainingPlayers();
      curStorage.putAll(newcomers);

      List<PartySearchCharacter> pscList = new ArrayList<>(curStorage.size());
      for (MapleCharacter chr : curStorage.values()) {
         pscList.add(new PartySearchCharacter(chr));
      }

      pscList.sort((c1, c2) -> {
         int levelP1 = c1.getLevel(), levelP2 = c2.getLevel();
         return Integer.compare(levelP1, levelP2);
      });

      psWLock.lock();
      try {
         storage.clear();
         storage.addAll(pscList);
      } finally {
         psWLock.unlock();
      }

      emptyIntervals.clear();
   }

   public MapleCharacter callPlayer(int callerCid, int callerMapId, int minLevel, int maxLevel) {
      if (emptyIntervals.inInterval(minLevel, maxLevel)) {
         return null;
      }

      List<PartySearchCharacter> pscList = getStorageList();

      int idx = bsearchStorage(pscList, maxLevel);
      for (int i = idx; i >= 0; i--) {
         PartySearchCharacter psc = pscList.get(i);
         if (!psc.isQueued()) {
            continue;
         }

         if (psc.getLevel() < minLevel) {
            break;
         }

         MapleCharacter chr = psc.callPlayer(callerCid, callerMapId);
         if (chr != null) {
            return chr;
         }
      }

      emptyIntervals.addInterval(minLevel, maxLevel);
      return null;
   }

   public void detachPlayer(MapleCharacter chr) {
      PartySearchCharacter toRemove = null;
      for (PartySearchCharacter psc : getStorageList()) {
         MapleCharacter player = psc.getPlayer();

         if (player != null && player.getId() == chr.getId()) {
            toRemove = psc;
            break;
         }
      }

      if (toRemove != null) {
         psWLock.lock();
         try {
            storage.remove(toRemove);
         } finally {
            psWLock.unlock();
         }
      }
   }

}
