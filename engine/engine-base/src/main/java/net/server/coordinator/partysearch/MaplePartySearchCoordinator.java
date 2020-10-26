package net.server.coordinator.partysearch;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;

import client.MapleCharacter;
import config.YamlConfig;
import constants.MapleJob;
import net.server.audit.locks.MonitoredLockType;
import net.server.audit.locks.MonitoredReadLock;
import net.server.audit.locks.MonitoredReentrantReadWriteLock;
import net.server.audit.locks.MonitoredWriteLock;
import net.server.audit.locks.factory.MonitoredReadLockFactory;
import net.server.audit.locks.factory.MonitoredWriteLockFactory;
import net.server.coordinator.world.MapleInviteCoordinator;
import net.server.coordinator.world.MapleInviteCoordinator.InviteType;
import provider.MapleData;
import provider.MapleDataProviderFactory;
import provider.MapleDataTool;
import tools.I18nMessage;
import tools.MessageBroadcaster;
import tools.PacketCreator;
import tools.Pair;
import tools.ServerNoticeType;
import tools.packet.party.PartySearchInvite;

public class MaplePartySearchCoordinator {

   private static Map<Integer, Set<Integer>> mapNeighbors = fetchNeighbouringMaps();
   private static Map<Integer, MapleJob> jobTable = instantiateJobTable();
   private final MonitoredReentrantReadWriteLock leaderQueueLock = new MonitoredReentrantReadWriteLock(MonitoredLockType.WORLD_PARTY_SEARCH_QUEUE, true);
   private final MonitoredReadLock leaderQueueRLock = MonitoredReadLockFactory.createLock(leaderQueueLock);
   private final MonitoredWriteLock leaderQueueWLock = MonitoredWriteLockFactory.createLock(leaderQueueLock);
   private Map<MapleJob, PartySearchStorage> storage = new HashMap<>();
   private Map<MapleJob, PartySearchEchelon> upcomers = new HashMap<>();
   private List<MapleCharacter> leaderQueue = new LinkedList<>();
   private Map<Integer, MapleCharacter> searchLeaders = new HashMap<>();
   private Map<Integer, LeaderSearchMetadata> searchSettings = new HashMap<>();
   private Map<MapleCharacter, LeaderSearchMetadata> timeoutLeaders = new HashMap<>();
   private int updateCount = 0;

   public MaplePartySearchCoordinator() {
      for (MapleJob job : jobTable.values()) {
         storage.put(job, new PartySearchStorage());
         upcomers.put(job, new PartySearchEchelon());
      }
   }

   private static Map<Integer, Set<Integer>> fetchNeighbouringMaps() {
      Map<Integer, Set<Integer>> mapLinks = new HashMap<>();

      MapleData data = MapleDataProviderFactory.getDataProvider(new File(System.getProperty("wzpath") + "/" + "Etc.wz")).getData("MapNeighbors.img");
      if (data != null) {
         for (MapleData mapleData : data.getChildren()) {
            int mapId = Integer.parseInt(mapleData.getName());

            Set<Integer> neighborMaps = new HashSet<>();
            mapLinks.put(mapId, neighborMaps);

            for (MapleData neighborData : mapleData.getChildren()) {
               int neighborId = MapleDataTool.getInt(neighborData, 999999999);

               if (neighborId != 999999999) {
                  neighborMaps.add(neighborId);
               }
            }
         }
      }

      return mapLinks;
   }

   public static boolean isInVicinity(int callerMapId, int calleeMapId) {
      Set<Integer> vicinityMapIds = mapNeighbors.get(calleeMapId);

      if (vicinityMapIds != null) {
         return vicinityMapIds.contains(calleeMapId);
      } else {
         int callerRange = callerMapId / 10000000;
         if (callerRange >= 90) {
            return callerRange == (calleeMapId / 1000000);
         } else {
            return callerRange == (calleeMapId / 10000000);
         }
      }
   }

   private static Map<Integer, MapleJob> instantiateJobTable() {
      Map<Integer, MapleJob> table = new HashMap<>();

      List<Pair<Integer, Integer>> jobSearchTypes = new LinkedList<>() {{
         add(new Pair<>(MapleJob.MAPLE_LEAF_BRIGADIER.getId(), 0));
         add(new Pair<>(0, 0));
         add(new Pair<>(MapleJob.ARAN1.getId(), 0));
         add(new Pair<>(100, 3));
         add(new Pair<>(MapleJob.DAWN_WARRIOR_1.getId(), 0));
         add(new Pair<>(200, 3));
         add(new Pair<>(MapleJob.BLAZE_WIZARD_1.getId(), 0));
         add(new Pair<>(500, 2));
         add(new Pair<>(MapleJob.THUNDER_BREAKER_1.getId(), 0));
         add(new Pair<>(400, 2));
         add(new Pair<>(MapleJob.NIGHT_WALKER_1.getId(), 0));
         add(new Pair<>(300, 2));
         add(new Pair<>(MapleJob.WIND_ARCHER_1.getId(), 0));
         add(new Pair<>(MapleJob.EVAN1.getId(), 0));
      }};

      int i = 0;
      for (Pair<Integer, Integer> p : jobSearchTypes) {
         table.put(i, MapleJob.getById(p.getLeft()));
         i++;

         for (int j = 1; j <= p.getRight(); j++) {
            table.put(i, MapleJob.getById(p.getLeft() + 10 * j));
            i++;
         }
      }

      return table;
   }

   private static MapleJob getPartySearchJob(MapleJob job) {
      if (job.getJobNiche() == 0) {
         return MapleJob.BEGINNER;
      } else if (job.getId() < 600) { // explorers
         return MapleJob.getById((job.getId() / 10) * 10);
      } else if (job.getId() >= 1000) {
         return MapleJob.getById((job.getId() / 100) * 100);
      } else {
         return MapleJob.MAPLE_LEAF_BRIGADIER;
      }
   }

   public void attachPlayer(MapleCharacter chr) {
      upcomers.get(getPartySearchJob(chr.getJob())).attachPlayer(chr);
   }

   public void detachPlayer(MapleCharacter chr) {
      MapleJob psJob = getPartySearchJob(chr.getJob());

      if (!upcomers.get(psJob).detachPlayer(chr)) {
         storage.get(psJob).detachPlayer(chr);
      }
   }

   public void updatePartySearchStorage() {
      for (Entry<MapleJob, PartySearchEchelon> psUpdate : upcomers.entrySet()) {
         storage.get(psUpdate.getKey()).updateStorage(psUpdate.getValue().exportEchelon());
      }
   }

   private MapleCharacter fetchPlayer(int callerCid, int callerMapId, MapleJob job, int minLevel, int maxLevel) {
      return storage.get(getPartySearchJob(job)).callPlayer(callerCid, callerMapId, minLevel, maxLevel);
   }

   private void addQueueLeader(MapleCharacter leader) {
      leaderQueueRLock.lock();
      try {
         leaderQueue.add(leader);
      } finally {
         leaderQueueRLock.unlock();
      }
   }

   private void removeQueueLeader(MapleCharacter leader) {
      leaderQueueRLock.lock();
      try {
         leaderQueue.remove(leader);
      } finally {
         leaderQueueRLock.unlock();
      }
   }

   public void registerPartyLeader(MapleCharacter leader, int minLevel, int maxLevel, int jobs) {
      if (searchLeaders.containsKey(leader.getId())) {
         return;
      }

      searchSettings.put(leader.getId(), new LeaderSearchMetadata(minLevel, maxLevel, jobs, jobTable));
      searchLeaders.put(leader.getId(), leader);
      addQueueLeader(leader);
   }

   private void registerPartyLeader(MapleCharacter leader, LeaderSearchMetadata settings) {
      if (searchLeaders.containsKey(leader.getId())) {
         return;
      }

      searchSettings.put(leader.getId(), settings);
      searchLeaders.put(leader.getId(), leader);
      addQueueLeader(leader);
   }

   public void unregisterPartyLeader(MapleCharacter leader) {
      MapleCharacter toRemove = searchLeaders.remove(leader.getId());
      if (toRemove != null) {
         removeQueueLeader(toRemove);
         searchSettings.remove(leader.getId());
      } else {
         unregisterLongTermPartyLeader(leader);
      }
   }

   private MapleCharacter searchPlayer(MapleCharacter leader) {
      LeaderSearchMetadata settings = searchSettings.get(leader.getId());
      if (settings != null) {
         int minLevel = settings.minLevel(), maxLevel = settings.maxLevel();
         List<MapleJob> jobs = new ArrayList<>(settings.searchedJobs());
         Collections.shuffle(jobs);

         int leaderCid = leader.getId();
         int leaderMapId = leader.getMapId();

         return jobs.stream()
               .map(job -> fetchPlayer(leaderCid, leaderMapId, job, minLevel, maxLevel))
               .filter(Objects::nonNull)
               .findFirst()
               .orElse(null);
      }

      return null;
   }

   private boolean sendPartyInviteFromSearch(MapleCharacter chr, MapleCharacter leader) {
      if (chr == null) {
         return false;
      }

      int partyId = leader.getPartyId();
      if (partyId < 0) {
         return false;
      }

      if (MapleInviteCoordinator.createInvite(InviteType.PARTY, leader, partyId, chr.getId())) {
         chr.disablePartySearchInvite(leader.getId());
         PacketCreator.announce(chr, new PartySearchInvite(leader.getPartyId(), leader.getName()));
         return true;
      } else {
         return false;
      }
   }

   private Pair<List<MapleCharacter>, List<MapleCharacter>> fetchQueuedLeaders() {
      List<MapleCharacter> queuedLeaders, nextLeaders;

      leaderQueueWLock.lock();
      try {
         int splitIdx = Math.min(leaderQueue.size(), 100);

         queuedLeaders = new LinkedList<>(leaderQueue.subList(0, splitIdx));
         nextLeaders = new LinkedList<>(leaderQueue.subList(splitIdx, leaderQueue.size()));
      } finally {
         leaderQueueWLock.unlock();
      }

      return new Pair<>(queuedLeaders, nextLeaders);
   }

   private void registerLongTermPartyLeaders(List<Pair<MapleCharacter, LeaderSearchMetadata>> recycledLeaders) {
      leaderQueueRLock.lock();
      try {
         for (Pair<MapleCharacter, LeaderSearchMetadata> p : recycledLeaders) {
            timeoutLeaders.put(p.getLeft(), p.getRight());
         }
      } finally {
         leaderQueueRLock.unlock();
      }
   }

   private void unregisterLongTermPartyLeader(MapleCharacter leader) {
      leaderQueueRLock.lock();
      try {
         timeoutLeaders.remove(leader);
      } finally {
         leaderQueueRLock.unlock();
      }
   }

   private void reinstateLongTermPartyLeaders() {
      Map<MapleCharacter, LeaderSearchMetadata> timeoutLeadersCopy;
      leaderQueueWLock.lock();
      try {
         timeoutLeadersCopy = new HashMap<>(timeoutLeaders);
         timeoutLeaders.clear();
      } finally {
         leaderQueueWLock.unlock();
      }

      for (Entry<MapleCharacter, LeaderSearchMetadata> e : timeoutLeadersCopy.entrySet()) {
         registerPartyLeader(e.getKey(), e.getValue());
      }
   }

   public void runPartySearch() {
      Pair<List<MapleCharacter>, List<MapleCharacter>> queuedLeaders = fetchQueuedLeaders();

      List<MapleCharacter> searchedLeaders = new LinkedList<>();
      List<MapleCharacter> recalledLeaders = new LinkedList<>();
      List<MapleCharacter> expiredLeaders = new LinkedList<>();

      for (MapleCharacter leader : queuedLeaders.getLeft()) {
         MapleCharacter chr = searchPlayer(leader);
         if (sendPartyInviteFromSearch(chr, leader)) {
            searchedLeaders.add(leader);
         } else {
            LeaderSearchMetadata settings = searchSettings.get(leader.getId());
            if (settings != null) {
               if (settings.reentryCount() < YamlConfig.config.server.PARTY_SEARCH_REENTRY_LIMIT) {
                  settings.incrementReentryCount();
                  recalledLeaders.add(leader);
               } else {
                  expiredLeaders.add(leader);
               }
            }
         }
      }

      leaderQueueRLock.lock();
      try {
         leaderQueue.clear();
         leaderQueue.addAll(queuedLeaders.getRight());

//         try {
//            leaderQueue.addAll(25, recalledLeaders);
//         } catch (IndexOutOfBoundsException e) {
//         }
         leaderQueue.addAll(recalledLeaders);
      } finally {
         leaderQueueRLock.unlock();
      }

      for (MapleCharacter leader : searchedLeaders) {
         if (leader.getParty().map(party -> party.getMembers().size() < 6).orElseThrow()) {
            addQueueLeader(leader);
         } else {
            if (leader.isLoggedInWorld()) {
               MessageBroadcaster.getInstance().sendServerNotice(leader, ServerNoticeType.PINK_TEXT, I18nMessage.from("PARTY_SEARCH_SUCCESS"));
            }
            searchLeaders.remove(leader.getId());
            searchSettings.remove(leader.getId());
         }
      }

      List<Pair<MapleCharacter, LeaderSearchMetadata>> recycledLeaders = new LinkedList<>();
      for (MapleCharacter leader : expiredLeaders) {
         searchLeaders.remove(leader.getId());
         LeaderSearchMetadata settings = searchSettings.remove(leader.getId());

         if (leader.isLoggedInWorld()) {
            if (settings != null) {
               recycledLeaders.add(new Pair<>(leader, settings));
               if (YamlConfig.config.server.USE_DEBUG && leader.isGM()) {
                  MessageBroadcaster.getInstance().sendServerNotice(leader, ServerNoticeType.PINK_TEXT, I18nMessage.from("PARTY_SEARCH_TRY_AGAIN_LATER"));
               }
            } else {
               MessageBroadcaster.getInstance().sendServerNotice(leader, ServerNoticeType.PINK_TEXT, I18nMessage.from("PARTY_SEARCH_SESSION_EXPIRED"));
            }
         }
      }

      if (!recycledLeaders.isEmpty()) {
         registerLongTermPartyLeaders(recycledLeaders);
      }

      updateCount++;
      if (updateCount % 77 == 0) {
         reinstateLongTermPartyLeaders();
      }
   }
}
