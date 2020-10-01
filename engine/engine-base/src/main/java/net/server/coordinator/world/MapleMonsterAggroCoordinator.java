package net.server.coordinator.world;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ScheduledFuture;

import client.MapleCharacter;
import config.YamlConfig;
import net.server.Server;
import net.server.audit.LockCollector;
import net.server.audit.locks.MonitoredLockType;
import net.server.audit.locks.MonitoredReentrantLock;
import net.server.audit.locks.factory.MonitoredReentrantLockFactory;
import server.TimerManager;
import server.life.MapleMonster;
import server.maps.MapleMap;
import tools.Pair;

public class MapleMonsterAggroCoordinator {

   private MonitoredReentrantLock lock = MonitoredReentrantLockFactory.createLock(MonitoredLockType.MAP_AGGRO);
   private MonitoredReentrantLock idleLock = MonitoredReentrantLockFactory.createLock(MonitoredLockType.MAP_AGGRO_IDLE, true);
   private long lastStopTime = Server.getInstance().getCurrentTime();

   private ScheduledFuture<?> aggroMonitor = null;

   private Map<MapleMonster, Map<Integer, PlayerAggroEntry>> mobAggroEntries = new HashMap<>();
   private Map<MapleMonster, List<PlayerAggroEntry>> mobSortedAggros = new HashMap<>();

   private Set<Integer> mapPuppetEntries = new HashSet<>();

   private static int updateEntryExpiration(PlayerAggroEntry pae) {
      return (int) Math.ceil((120000L / YamlConfig.config.server.MOB_STATUS_AGGRO_INTERVAL) / Math.pow(2, pae.expireStreak() + pae.currentDamageInstances()));
   }

   private static PlayerAggroEntry insertEntryDamage(PlayerAggroEntry pae, int damage) {
      synchronized (pae) {
         long totalDamage = pae.averageDamage();
         totalDamage *= pae.currentDamageInstances();
         totalDamage += damage;

         return new PlayerAggroEntryBuilder(pae)
               .setExpireStreak(0)
               .setUpdateStreak(0)
               .setToNextUpdate(updateEntryExpiration(pae))
               .setCurrentDamageInstances(pae.currentDamageInstances() + 1)
               .setAverageDamage((int) (totalDamage / (pae.currentDamageInstances() + 1)))
               .setAccumulatedDamage(totalDamage)
               .build();
      }
   }

   private static Pair<PlayerAggroEntry, Boolean> expiredAfterUpdateEntryDamage(PlayerAggroEntry pae, int deltaTime) {
      synchronized (pae) {
         PlayerAggroEntryBuilder builder = new PlayerAggroEntryBuilder(pae)
               .setUpdateStreak(pae.updateStreak() + 1)
               .setToNextUpdate(pae.toNextUpdate() - deltaTime);

         if (pae.toNextUpdate() <= 0) {    // reached dmg instance expire time
            builder.setExpireStreak(pae.expireStreak() + 1)
                  .setToNextUpdate(updateEntryExpiration(pae))
                  .setCurrentDamageInstances(pae.currentDamageInstances() - 1);

            if (pae.currentDamageInstances() < 1) {   // expired aggro for this player
               return new Pair<>(builder.build(), true);
            }
            builder.setAccumulatedDamage(pae.averageDamage() + pae.currentDamageInstances());
         }

         return new Pair<>(builder.build(), false);
      }
   }

   private static List<PlayerAggroEntry> insertionSortAggroList(List<PlayerAggroEntry> paeList) {
      for (int i = 1; i < paeList.size(); i++) {
         PlayerAggroEntry pae = paeList.get(i);
         long curAccDmg = pae.accumulatedDamage();

         int j = i - 1;
         while (j >= 0 && curAccDmg > paeList.get(j).accumulatedDamage()) {
            j -= 1;
         }

         j += 1;
         if (j != i) {
            paeList.remove(i);
            paeList.add(j, pae);
         }
      }

      List<PlayerAggroEntry> resultList = new ArrayList<>();
      int i = 0;
      for (PlayerAggroEntry pae : paeList) {
         resultList.add(new PlayerAggroEntryBuilder(pae).setEntryRank(i).build());
         i += 1;
      }
      return resultList;
   }

   public void stopAggroCoordinator() {
      idleLock.lock();
      try {
         if (aggroMonitor == null) {
            return;
         }

         aggroMonitor.cancel(false);
         aggroMonitor = null;
      } finally {
         idleLock.unlock();
      }

      lastStopTime = Server.getInstance().getCurrentTime();
   }

   public void startAggroCoordinator() {
      idleLock.lock();
      try {
         if (aggroMonitor != null) {
            return;
         }

         aggroMonitor = TimerManager.getInstance().register(() -> {
            runAggroUpdate(1);
            runSortLeadingCharactersAggro();
         }, YamlConfig.config.server.MOB_STATUS_AGGRO_INTERVAL, YamlConfig.config.server.MOB_STATUS_AGGRO_INTERVAL);
      } finally {
         idleLock.unlock();
      }

      int timeDelta = (int) Math.ceil((Server.getInstance().getCurrentTime() - lastStopTime) / YamlConfig.config.server.MOB_STATUS_AGGRO_INTERVAL);
      if (timeDelta > 0) {
         runAggroUpdate(timeDelta);
      }
   }

   public void addAggroDamage(MapleMonster mob, int cid, int damage) { // assumption: should not trigger after dispose()
      if (!mob.isAlive()) {
         return;
      }

      List<PlayerAggroEntry> sortedAggro = mobSortedAggros.get(mob);
      Map<Integer, PlayerAggroEntry> mobAggro = mobAggroEntries.get(mob);
      if (mobAggro == null) {
         if (lock.tryLock()) {   // can run unreliably, as fast as possible... try lock that is!
            try {
               mobAggro = mobAggroEntries.get(mob);
               if (mobAggro == null) {
                  mobAggro = new HashMap<>();
                  mobAggroEntries.put(mob, mobAggro);

                  sortedAggro = new LinkedList<>();
                  mobSortedAggros.put(mob, sortedAggro);
               } else {
                  sortedAggro = mobSortedAggros.get(mob);
               }
            } finally {
               lock.unlock();
            }
         } else {
            return;
         }
      }

      PlayerAggroEntry aggroEntry = mobAggro.get(cid);
      if (aggroEntry == null) {
         aggroEntry = new PlayerAggroEntry(cid);

         synchronized (mobAggro) {
            synchronized (sortedAggro) {
               PlayerAggroEntry mappedEntry = mobAggro.get(cid);

               if (mappedEntry == null) {
                  mobAggro.put(aggroEntry.cid(), aggroEntry);
                  sortedAggro.add(aggroEntry);
               } else {
                  aggroEntry = mappedEntry;
               }
            }
         }
      } else if (damage < 1) {
         return;
      }

      PlayerAggroEntry result = insertEntryDamage(aggroEntry, damage);
      mobAggro.put(cid, result);
   }

   private void runAggroUpdate(int deltaTime) {
      List<Pair<MapleMonster, Map<Integer, PlayerAggroEntry>>> aggroMobs = new LinkedList<>();
      lock.lock();
      try {
         for (Entry<MapleMonster, Map<Integer, PlayerAggroEntry>> e : mobAggroEntries.entrySet()) {
            aggroMobs.add(new Pair<>(e.getKey(), e.getValue()));
         }
      } finally {
         lock.unlock();
      }

      for (Pair<MapleMonster, Map<Integer, PlayerAggroEntry>> am : aggroMobs) {
         Map<Integer, PlayerAggroEntry> mobAggro = am.getRight();
         List<PlayerAggroEntry> sortedAggro = mobSortedAggros.get(am.getLeft());

         if (sortedAggro != null) {
            List<Integer> toRemove = new LinkedList<>();
            List<Integer> toRemoveIdx = new ArrayList<>(mobAggro.size());
            List<Integer> toRemoveByFetch = new LinkedList<>();

            synchronized (mobAggro) {
               synchronized (sortedAggro) {
                  for (PlayerAggroEntry pae : mobAggro.values()) {
                     Pair<PlayerAggroEntry, Boolean> result = expiredAfterUpdateEntryDamage(pae, deltaTime);
                     PlayerAggroEntry updatePae = result.getLeft();

                     if (result.getRight()) {
                        toRemove.add(updatePae.cid());
                        if (updatePae.entryRank() > -1) {
                           toRemoveIdx.add(updatePae.entryRank());
                        } else {
                           toRemoveByFetch.add(updatePae.cid());
                        }
                     }
                  }

                  if (!toRemove.isEmpty()) {
                     for (Integer cid : toRemove) {
                        mobAggro.remove(cid);
                     }

                     if (mobAggro.isEmpty()) {   // all aggro on this mob expired
                        if (!am.getLeft().isBoss()) {
                           am.getLeft().aggroResetAggro();
                        }
                     }
                  }

                  if (!toRemoveIdx.isEmpty()) {
                     // last to first indexes
                     toRemoveIdx.sort((p1, p2) -> p1 < p2 ? 1 : p1.equals(p2) ? 0 : -1);

                     for (int idx : toRemoveIdx) {
                        sortedAggro.remove(idx);
                     }
                  }

                  if (!toRemoveByFetch.isEmpty()) {
                     for (Integer cid : toRemoveByFetch) {
                        for (int i = 0; i < sortedAggro.size(); i++) {
                           if (cid.equals(sortedAggro.get(i).cid())) {
                              sortedAggro.remove(i);
                              break;
                           }
                        }
                     }
                  }
               }
            }
         }
      }
   }

   public boolean isLeadingCharacterAggro(MapleMonster mob, MapleCharacter player) {
      if (mob.isLeadingPuppetInVicinity()) {
         return false;
      } else if (mob.isCharacterPuppetInVicinity(player)) {
         return true;
      }

      // by assuming the quasi-sorted nature of "mobAggroList", this method
      // returns whether the player given as parameter can be elected as next aggro leader

      List<PlayerAggroEntry> mobAggroList = mobSortedAggros.get(mob);
      if (mobAggroList != null) {
         synchronized (mobAggroList) {
            mobAggroList = new ArrayList<>(mobAggroList.subList(0, Math.min(mobAggroList.size(), 5)));
         }

         MapleMap map = mob.getMap();
         for (PlayerAggroEntry pae : mobAggroList) {
            MapleCharacter chr = map.getCharacterById(pae.cid());
            if (chr != null) {
               if (player.getId() == pae.cid()) {
                  return true;
               } else if (pae.updateStreak() < YamlConfig.config.server.MOB_STATUS_AGGRO_PERSISTENCE && chr.isAlive()) {  // verifies currently leading players activity
                  return false;
               }
            }
         }
      }

      return false;
   }

   public void runSortLeadingCharactersAggro() {
      lock.lock();
      try {
         for (MapleMonster monster : mobSortedAggros.keySet()) {
            synchronized (monster) {
               mobSortedAggros.put(monster, insertionSortAggroList(mobSortedAggros.get(monster)));
            }
         }
      } finally {
         lock.unlock();
      }
   }

   public void removeAggroEntries(MapleMonster mob) {
      lock.lock();
      try {
         mobAggroEntries.remove(mob);
         mobSortedAggros.remove(mob);
      } finally {
         lock.unlock();
      }
   }

   public void addPuppetAggro(MapleCharacter player) {
      synchronized (mapPuppetEntries) {
         mapPuppetEntries.add(player.getId());
      }
   }

   public void removePuppetAggro(Integer cid) {
      synchronized (mapPuppetEntries) {
         mapPuppetEntries.remove(cid);
      }
   }

   public List<Integer> getPuppetAggroList() {
      synchronized (mapPuppetEntries) {
         return new ArrayList<>(mapPuppetEntries);
      }
   }

   public void dispose() {
      stopAggroCoordinator();

      lock.lock();
      try {
         mobAggroEntries.clear();
         mobSortedAggros.clear();
      } finally {
         lock.unlock();
      }

      disposeLocks();
   }

   private void disposeLocks() {
      LockCollector.getInstance().registerDisposeAction(this::emptyLocks);
   }

   private void emptyLocks() {
      lock = lock.dispose();
   }
}
