package net.server;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;

import client.MapleAbnormalStatus;
import net.server.audit.locks.MonitoredLockType;
import net.server.audit.locks.factory.MonitoredReentrantLockFactory;
import server.life.MobSkill;
import tools.Pair;

public class PlayerBuffStorage {
   private final Lock lock = MonitoredReentrantLockFactory.createLock(MonitoredLockType.BUFF_STORAGE, true);
   private int id = (int) (Math.random() * 100);
   private Map<Integer, List<PlayerBuffValueHolder>> buffs = new HashMap<>();
   private Map<Integer, Map<MapleAbnormalStatus, Pair<Long, MobSkill>>> diseases = new HashMap<>();

   public void addBuffsToStorage(int characterId, List<PlayerBuffValueHolder> toStore) {
      lock.lock();
      try {
         buffs.put(characterId, toStore);//Old one will be replaced if it's in here.
      } finally {
         lock.unlock();
      }
   }

   public List<PlayerBuffValueHolder> getBuffsFromStorage(int characterId) {
      lock.lock();
      try {
         return buffs.remove(characterId);
      } finally {
         lock.unlock();
      }
   }

   public void addDiseasesToStorage(int characterId, Map<MapleAbnormalStatus, Pair<Long, MobSkill>> toStore) {
      lock.lock();
      try {
         diseases.put(characterId, toStore);
      } finally {
         lock.unlock();
      }
   }

   public Map<MapleAbnormalStatus, Pair<Long, MobSkill>> getDiseasesFromStorage(int characterId) {
      lock.lock();
      try {
         return diseases.remove(characterId);
      } finally {
         lock.unlock();
      }
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + id;
      return result;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      }
      if (obj == null) {
         return false;
      }
      if (getClass() != obj.getClass()) {
         return false;
      }
      final PlayerBuffStorage other = (PlayerBuffStorage) obj;
      return id == other.id;
   }
}
