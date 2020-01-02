package net.server.coordinator.partysearch;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
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

public class PartySearchEchelon {

   private final MonitoredReentrantReadWriteLock psLock = new MonitoredReentrantReadWriteLock(MonitoredLockType.WORLD_PARTY_SEARCH_ECHELON, true);
   private final MonitoredReadLock psRLock = MonitoredReadLockFactory.createLock(psLock);
   private final MonitoredWriteLock psWLock = MonitoredWriteLockFactory.createLock(psLock);

   private Map<Integer, WeakReference<MapleCharacter>> echelon = new HashMap<>(20);

   public List<MapleCharacter> exportEchelon() {
      psWLock.lock();     // reversing read/write actually could provide a lax yet sure performance/precision trade-off here
      try {
         List<MapleCharacter> players = new ArrayList<>(echelon.size());

         for (WeakReference<MapleCharacter> chrRef : echelon.values()) {
            MapleCharacter chr = chrRef.get();
            if (chr != null) {
               players.add(chr);
            }
         }

         echelon.clear();
         return players;
      } finally {
         psWLock.unlock();
      }
   }

   public void attachPlayer(MapleCharacter chr) {
      psRLock.lock();
      try {
         echelon.put(chr.getId(), new WeakReference<>(chr));
      } finally {
         psRLock.unlock();
      }
   }

   public boolean detachPlayer(MapleCharacter chr) {
      psRLock.lock();
      try {
         return echelon.remove(chr.getId()) != null;
      } finally {
         psRLock.unlock();
      }
   }

}
