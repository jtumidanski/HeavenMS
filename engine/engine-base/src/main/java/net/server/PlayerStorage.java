package net.server;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import client.MapleCharacter;
import client.MapleClient;
import net.server.audit.locks.MonitoredLockType;
import net.server.audit.locks.MonitoredReadLock;
import net.server.audit.locks.MonitoredReentrantReadWriteLock;
import net.server.audit.locks.MonitoredWriteLock;
import net.server.audit.locks.factory.MonitoredReadLockFactory;
import net.server.audit.locks.factory.MonitoredWriteLockFactory;

public class PlayerStorage {
   private final MonitoredReentrantReadWriteLock locks = new MonitoredReentrantReadWriteLock(MonitoredLockType.PLAYER_STORAGE, true);
   private final Map<Integer, MapleCharacter> storage = new LinkedHashMap<>();
   private final Map<String, MapleCharacter> nameStorage = new LinkedHashMap<>();
   private MonitoredReadLock readLock = MonitoredReadLockFactory.createLock(locks);
   private MonitoredWriteLock writeLock = MonitoredWriteLockFactory.createLock(locks);

   public void addPlayer(MapleCharacter chr) {
      writeLock.lock();
      try {
         storage.put(chr.getId(), chr);
         nameStorage.put(chr.getName().toLowerCase(), chr);
      } finally {
         writeLock.unlock();
      }
   }

   public MapleCharacter removePlayer(int chr) {
      writeLock.lock();
      try {
         MapleCharacter mc = storage.remove(chr);
         if (mc != null) {
            nameStorage.remove(mc.getName().toLowerCase());
         }

         return mc;
      } finally {
         writeLock.unlock();
      }
   }

   public Optional<MapleCharacter> getCharacterByName(String name) {
      readLock.lock();
      try {
         return Optional.ofNullable(nameStorage.get(name.toLowerCase()));
      } finally {
         readLock.unlock();
      }
   }

   public Optional<MapleCharacter> getCharacterById(int id) {
      readLock.lock();
      try {
         return Optional.ofNullable(storage.get(id));
      } finally {
         readLock.unlock();
      }
   }

   public Collection<MapleCharacter> getAllCharacters() {
      readLock.lock();
      try {
         return new ArrayList<>(storage.values());
      } finally {
         readLock.unlock();
      }
   }

   public final void disconnectAll() {
      List<MapleCharacter> chrList;
      readLock.lock();
      try {
         chrList = new ArrayList<>(storage.values());
      } finally {
         readLock.unlock();
      }

      for (MapleCharacter mc : chrList) {
         MapleClient client = mc.getClient();
         if (client != null) {
            client.forceDisconnect();
         }
      }

      writeLock.lock();
      try {
         storage.clear();
      } finally {
         writeLock.unlock();
      }
   }

   public int getSize() {
      readLock.lock();
      try {
         return storage.size();
      } finally {
         readLock.unlock();
      }
   }
}