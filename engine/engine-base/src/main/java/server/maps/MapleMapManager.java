package server.maps;

import java.util.HashMap;
import java.util.Map;

import net.server.audit.locks.MonitoredLockType;
import net.server.audit.locks.MonitoredReadLock;
import net.server.audit.locks.MonitoredReentrantReadWriteLock;
import net.server.audit.locks.MonitoredWriteLock;
import net.server.audit.locks.factory.MonitoredReadLockFactory;
import net.server.audit.locks.factory.MonitoredWriteLockFactory;
import scripting.event.EventInstanceManager;

public class MapleMapManager {

   private int channel, world;
   private EventInstanceManager event;

   private Map<Integer, MapleMap> maps = new HashMap<>();

   private MonitoredReadLock mapsRLock;
   private MonitoredWriteLock mapsWLock;

   public MapleMapManager(EventInstanceManager eim, int world, int channel) {
      this.world = world;
      this.channel = channel;
      this.event = eim;

      MonitoredReentrantReadWriteLock readWriteLock = new MonitoredReentrantReadWriteLock(MonitoredLockType.MAP_MANAGER);
      this.mapsRLock = MonitoredReadLockFactory.createLock(readWriteLock);
      this.mapsWLock = MonitoredWriteLockFactory.createLock(readWriteLock);
   }

   public MapleMap resetMap(int mapId) {
      mapsWLock.lock();
      try {
         maps.remove(mapId);
      } finally {
         mapsWLock.unlock();
      }

      return getMap(mapId);
   }

   private synchronized MapleMap loadMapFromWz(int mapId, boolean cache) {
      MapleMap map;

      if (cache) {
         mapsRLock.lock();
         try {
            map = maps.get(mapId);
         } finally {
            mapsRLock.unlock();
         }

         if (map != null) {
            return map;
         }
      }

      map = MapleMapFactory.loadMapFromWz(mapId, world, channel, event);

      if (cache) {
         mapsWLock.lock();
         try {
            maps.put(mapId, map);
         } finally {
            mapsWLock.unlock();
         }
      }

      return map;
   }

   public MapleMap getMap(int mapId) {
      MapleMap map;

      mapsRLock.lock();
      try {
         map = maps.get(mapId);
      } finally {
         mapsRLock.unlock();
      }

      return (map != null) ? map : loadMapFromWz(mapId, true);
   }

   public MapleMap getDisposableMap(int mapId) {
      return loadMapFromWz(mapId, false);
   }

   public boolean isMapLoaded(int mapId) {
      mapsRLock.lock();
      try {
         return maps.containsKey(mapId);
      } finally {
         mapsRLock.unlock();
      }
   }

   public Map<Integer, MapleMap> getMaps() {
      mapsRLock.lock();
      try {
         return new HashMap<>(maps);
      } finally {
         mapsRLock.unlock();
      }
   }

   public void updateMaps() {
      for (MapleMap map : getMaps().values()) {
         map.respawn();
         map.mobMpRecovery();
      }
   }

   public void dispose() {
      for (MapleMap map : getMaps().values()) {
         map.dispose();
      }

      this.event = null;
   }
}
