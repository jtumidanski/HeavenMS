/*
This file is part of the OdinMS Maple Story Server
Copyright (C) 2008 Patrick Huy <patrick.huy@frz.cc>
Matthias Butz <matze@odinms.de>
Jan Christian Meyer <vimes@odinms.de>

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as
published by the Free Software Foundation version 3 as published by
the Free Software Foundation. You may not use, modify or distribute
this program under any other version of the GNU Affero General Public
License.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.server.world;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;

import client.BuddyList;
import client.BuddyListAddResult;
import client.BuddyListEntry;
import client.BuddyListOperation;
import client.MapleCharacter;
import client.MapleFamily;
import client.database.administrator.CharacterAdministrator;
import client.database.administrator.MarriageAdministrator;
import client.database.administrator.PlayerNpcAdministrator;
import client.database.data.MarriageData;
import client.database.provider.MarriageProvider;
import client.processor.BuddyListProcessor;
import constants.GameConstants;
import constants.ServerConstants;
import net.server.PlayerStorage;
import net.server.Server;
import net.server.audit.LockCollector;
import net.server.audit.locks.MonitoredLockType;
import net.server.audit.locks.MonitoredReentrantLock;
import net.server.audit.locks.MonitoredReentrantReadWriteLock;
import net.server.audit.locks.factory.MonitoredReentrantLockFactory;
import net.server.channel.Channel;
import net.server.channel.CharacterIdChannelPair;
import net.server.coordinator.MapleInviteCoordinator;
import net.server.coordinator.MapleInviteCoordinator.InviteResult;
import net.server.coordinator.MapleInviteCoordinator.InviteType;
import net.server.coordinator.MapleMatchCheckerCoordinator;
import net.server.coordinator.MaplePartySearchCoordinator;
import net.server.guild.MapleGuild;
import net.server.guild.MapleGuildCharacter;
import net.server.guild.MapleGuildSummary;
import net.server.worker.CharacterAutosaverWorker;
import net.server.worker.FamilyDailyResetWorker;
import net.server.worker.FishingWorker;
import net.server.worker.HiredMerchantWorker;
import net.server.worker.MapOwnershipWorker;
import net.server.worker.MountTirednessWorker;
import net.server.worker.PartySearchWorker;
import net.server.worker.PetFullnessWorker;
import net.server.worker.ServerMessageWorker;
import net.server.worker.TimedMapObjectWorker;
import net.server.worker.TimeoutWorker;
import net.server.worker.WeddingReservationWorker;
import scripting.event.EventInstanceManager;
import server.MapleStorage;
import server.TimerManager;
import server.maps.AbstractMapleMapObject;
import server.maps.MapleHiredMerchant;
import server.maps.MapleMap;
import server.maps.MapleMiniDungeon;
import server.maps.MapleMiniDungeonInfo;
import server.maps.MaplePlayerShop;
import server.maps.MaplePlayerShopItem;
import tools.DatabaseConnection;
import tools.MasterBroadcaster;
import tools.PacketCreator;
import tools.Pair;
import tools.packet.buddy.UpdateBuddyChannel;
import tools.packet.guild.GuildEmblemChange;
import tools.packet.guild.GuildMarkChanged;
import tools.packet.guild.GuildNameChange;
import tools.packet.message.MultiChat;
import tools.packet.message.ServerMessage;
import tools.packet.message.Whisper;
import tools.packet.messenger.MessengerAddCharacter;
import tools.packet.messenger.MessengerChat;
import tools.packet.messenger.MessengerInvite;
import tools.packet.messenger.MessengerJoin;
import tools.packet.messenger.MessengerNote;
import tools.packet.messenger.MessengerRemoveCharacter;
import tools.packet.messenger.MessengerUpdateCharacter;
import tools.packet.partyoperation.UpdateParty;
import tools.packets.Fishing;

/**
 * @author kevintjuh93
 * @author Ronan - thread-oriented world schedules, guild queue, marriages & party chars
 */
public class World {

   private final ReentrantReadWriteLock chnLock = new MonitoredReentrantReadWriteLock(MonitoredLockType.WORLD_CHANNELS, true);
   private final ReentrantReadWriteLock suggestLock = new MonitoredReentrantReadWriteLock(MonitoredLockType.WORLD_SUGGEST, true);
   private int id, flag, exprate, droprate, bossdroprate, mesorate, questrate, travelrate, fishingrate;
   private String eventmsg;
   private List<Channel> channels = new ArrayList<>();
   private Map<Integer, Byte> pnpcStep = new HashMap<>();
   private Map<Integer, Short> pnpcPodium = new HashMap<>();
   private Map<Integer, MapleMessenger> messengers = new HashMap<>();
   private AtomicInteger runningMessengerId = new AtomicInteger();
   private Map<Integer, MapleFamily> families = new LinkedHashMap<>();
   private Map<Integer, Integer> relationships = new HashMap<>();
   private Map<Integer, Pair<Integer, Integer>> relationshipCouples = new HashMap<>();
   private Map<Integer, MapleGuildSummary> gsStore = new HashMap<>();
   private PlayerStorage players = new PlayerStorage();
   private MapleMatchCheckerCoordinator matchChecker = new MapleMatchCheckerCoordinator();
   private MaplePartySearchCoordinator partySearch = new MaplePartySearchCoordinator();

   private ReadLock chnRLock = chnLock.readLock();
   private WriteLock chnWLock = chnLock.writeLock();
   private Map<Integer, SortedMap<Integer, MapleCharacter>> accountChars = new HashMap<>();
   private Map<Integer, MapleStorage> accountStorages = new HashMap<>();

   private MonitoredReentrantLock accountCharsLock = MonitoredReentrantLockFactory.createLock(MonitoredLockType.WORLD_CHARS, true);
   private Set<Integer> queuedGuilds = new HashSet<>();
   private Map<Integer, Pair<Pair<Boolean, Boolean>, Pair<Integer, Integer>>> queuedMarriages = new HashMap<>();
   private Map<Integer, Set<Integer>> marriageGuests = new ConcurrentHashMap<>();
   private Map<Integer, Integer> partyChars = new HashMap<>();
   private Map<Integer, MapleParty> parties = new HashMap<>();
   private AtomicInteger runningPartyId = new AtomicInteger();
   private MonitoredReentrantLock partyLock = MonitoredReentrantLockFactory.createLock(MonitoredLockType.WORLD_PARTY, true);
   private Map<Integer, Integer> owlSearched = new LinkedHashMap<>();
   private List<Map<Integer, Integer>> cashItemBought = new ArrayList<>(9);
   private ReadLock suggestRLock = suggestLock.readLock();
   private WriteLock suggestWLock = suggestLock.writeLock();

   private Map<Integer, Integer> disabledServerMessages = new HashMap<>();    // reuse owl lock
   private MonitoredReentrantLock srvMessagesLock = MonitoredReentrantLockFactory.createLock(MonitoredLockType.WORLD_SRVMESSAGES);
   private ScheduledFuture<?> srvMessagesSchedule;

   private MonitoredReentrantLock activePetsLock = MonitoredReentrantLockFactory.createLock(MonitoredLockType.WORLD_PETS, true);
   private Map<Integer, Integer> activePets = new LinkedHashMap<>();
   private ScheduledFuture<?> petsSchedule;
   private long petUpdate;

   private MonitoredReentrantLock activeMountsLock = MonitoredReentrantLockFactory.createLock(MonitoredLockType.WORLD_MOUNTS, true);
   private Map<Integer, Integer> activeMounts = new LinkedHashMap<>();
   private ScheduledFuture<?> mountsSchedule;
   private long mountUpdate;

   private MonitoredReentrantLock activePlayerShopsLock = MonitoredReentrantLockFactory.createLock(MonitoredLockType.WORLD_PSHOPS, true);
   private Map<Integer, MaplePlayerShop> activePlayerShops = new LinkedHashMap<>();

   private MonitoredReentrantLock activeMerchantsLock = MonitoredReentrantLockFactory.createLock(MonitoredLockType.WORLD_MERCHS, true);
   private Map<Integer, Pair<MapleHiredMerchant, Integer>> activeMerchants = new LinkedHashMap<>();
   private ScheduledFuture<?> merchantSchedule;
   private long merchantUpdate;

   private Map<Runnable, Long> registeredTimedMapObjects = new LinkedHashMap<>();
   private ScheduledFuture<?> timedMapObjectsSchedule;
   private MonitoredReentrantLock timedMapObjectLock = MonitoredReentrantLockFactory.createLock(MonitoredLockType.WORLD_MAPOBJS, true);

   private Map<MapleCharacter, Integer> fishingAttempters = Collections.synchronizedMap(new WeakHashMap<>());

   private ScheduledFuture<?> charactersSchedule;
   private ScheduledFuture<?> marriagesSchedule;
   private ScheduledFuture<?> mapOwnershipSchedule;
   private ScheduledFuture<?> fishingSchedule;
   private ScheduledFuture<?> partySearchSchedule;
   private ScheduledFuture<?> timeoutSchedule;

   public World(int world, int flag, String eventmsg, int exprate, int droprate, int bossdroprate, int mesorate, int questrate, int travelrate, int fishingrate) {
      this.id = world;
      this.flag = flag;
      this.eventmsg = eventmsg;
      this.exprate = exprate;
      this.droprate = droprate;
      this.bossdroprate = bossdroprate;
      this.mesorate = mesorate;
      this.questrate = questrate;
      this.travelrate = travelrate;
      this.fishingrate = fishingrate;
      runningPartyId.set(1000000001); // partyid must not clash with charid to solve update item looting issues, found thanks to Vcoc
      runningMessengerId.set(1);

      petUpdate = Server.getInstance().getCurrentTime();
      mountUpdate = petUpdate;

      for (int i = 0; i < 9; i++) {
         cashItemBought.add(new LinkedHashMap<>());
      }

      TimerManager tman = TimerManager.getInstance();
      petsSchedule = tman.register(new PetFullnessWorker(this), 60 * 1000, 60 * 1000);
      srvMessagesSchedule = tman.register(new ServerMessageWorker(this), 10 * 1000, 10 * 1000);
      mountsSchedule = tman.register(new MountTirednessWorker(this), 60 * 1000, 60 * 1000);
      merchantSchedule = tman.register(new HiredMerchantWorker(this), 10 * 60 * 1000, 10 * 60 * 1000);
      timedMapObjectsSchedule = tman.register(new TimedMapObjectWorker(this), 60 * 1000, 60 * 1000);
      charactersSchedule = tman.register(new CharacterAutosaverWorker(this), 60 * 60 * 1000, 60 * 60 * 1000);
      marriagesSchedule = tman.register(new WeddingReservationWorker(this), ServerConstants.WEDDING_RESERVATION_INTERVAL * 60 * 1000, ServerConstants.WEDDING_RESERVATION_INTERVAL * 60 * 1000);
      mapOwnershipSchedule = tman.register(new MapOwnershipWorker(this), 20 * 1000, 20 * 1000);
      fishingSchedule = tman.register(new FishingWorker(this), 10 * 1000, 10 * 1000);
      partySearchSchedule = tman.register(new PartySearchWorker(this), 10 * 1000, 10 * 1000);
      timeoutSchedule = tman.register(new TimeoutWorker(this), 10 * 1000, 10 * 1000);

      if (ServerConstants.USE_FAMILY_SYSTEM) {
         long timeLeft = Server.getTimeLeftForNextDay();
         FamilyDailyResetWorker.resetEntitlementUsage(this);
         tman.register(new FamilyDailyResetWorker(this), 24 * 60 * 60 * 1000, timeLeft);
      }
   }

   private static List<Entry<Integer, SortedMap<Integer, MapleCharacter>>> getSortedAccountCharacterView(Map<Integer, SortedMap<Integer, MapleCharacter>> map) {
      List<Entry<Integer, SortedMap<Integer, MapleCharacter>>> list = new ArrayList<>(map.size());
      list.addAll(map.entrySet());

      list.sort(new Comparator<>() {
         @Override
         public int compare(Entry<Integer, SortedMap<Integer, MapleCharacter>> o1, Entry<Integer, SortedMap<Integer, MapleCharacter>> o2) {
            return o1.getKey() - o2.getKey();
         }
      });

      return list;
   }

   private static Integer getPetKey(MapleCharacter chr, byte petSlot) {    // assuming max 3 pets
      return (chr.getId() << 2) + petSlot;
   }

   private static void executePlayerNpcMapDataUpdate(Connection con, boolean isPodium, Map<Integer, ?> playerNpcData, int value, int worldId, int mapId) {
      if (playerNpcData.containsKey(mapId)) {
         if (isPodium) {
            PlayerNpcAdministrator.getInstance().setPodium(con, value, worldId, mapId);
         } else {
            PlayerNpcAdministrator.getInstance().setStep(con, value, worldId, mapId);
         }
      } else {
         if (isPodium) {
            PlayerNpcAdministrator.getInstance().addPodium(con, value, worldId, mapId);
         } else {
            PlayerNpcAdministrator.getInstance().addStep(con, value, worldId, mapId);
         }
      }
   }

   private static Pair<Integer, Pair<Integer, Integer>> getRelationshipCoupleFromDb(int id, boolean usingMarriageId) {
      Optional<MarriageData> marriageData;
      if (usingMarriageId) {
         marriageData = DatabaseConnection.getInstance().withConnectionResult(connection -> MarriageProvider.getInstance().getById(connection, id).orElseThrow());
      } else {
         marriageData = DatabaseConnection.getInstance().withConnectionResult(connection -> MarriageProvider.getInstance().getBySpouses(connection, id, id).orElseThrow());
      }

      return marriageData.map(data -> new Pair<>(data.id(), new Pair<>(data.spouse1(), data.spouse2()))).orElse(null);
   }

   private static int addRelationshipToDb(int groomId, int brideId) {
      return DatabaseConnection.getInstance().withConnectionResult(connection -> MarriageAdministrator.getInstance().createMarriage(connection, groomId, brideId)).orElse(-1);
   }

   private static void deleteRelationshipFromDb(int playerId) {
      DatabaseConnection.getInstance().withConnection(connection -> MarriageAdministrator.getInstance().endMarriage(connection, playerId));
   }

   public int getChannelsSize() {
      chnRLock.lock();
      try {
         return channels.size();
      } finally {
         chnRLock.unlock();
      }
   }

   public List<Channel> getChannels() {
      chnRLock.lock();
      try {
         return new ArrayList<>(channels);
      } finally {
         chnRLock.unlock();
      }
   }

   public Channel getChannel(int channel) {
      chnRLock.lock();
      try {
         try {
            return channels.get(channel - 1);
         } catch (IndexOutOfBoundsException e) {
            return null;
         }
      } finally {
         chnRLock.unlock();
      }
   }

   public void addChannel(Channel channel) {
      chnWLock.lock();
      try {
         channels.add(channel);
      } finally {
         chnWLock.unlock();
      }
   }

   public int removeChannel() {
      Channel ch;
      int chIdx;

      chnRLock.lock();
      try {
         chIdx = channels.size() - 1;
         if (chIdx < 0) {
            return -1;
         }

         ch = channels.get(chIdx);
      } finally {
         chnRLock.unlock();
      }

      if (ch == null || !ch.canUninstall()) {
         return -1;
      }

      chnWLock.lock();
      try {
         if (chIdx == channels.size() - 1) {
            channels.remove(chIdx);
         } else {
            return -1;
         }
      } finally {
         chnWLock.unlock();
      }

      ch.shutdown();
      return ch.getId();
   }

   public boolean canUninstall() {
      if (players.getSize() > 0) {
         return false;
      }

      for (Channel ch : this.getChannels()) {
         if (!ch.canUninstall()) {
            return false;
         }
      }

      return true;
   }

   public int getFlag() {
      return flag;
   }

   public void setFlag(byte b) {
      this.flag = b;
   }

   public String getEventMessage() {
      return eventmsg;
   }

   public int getExpRate() {
      return exprate;
   }

   public void setExpRate(int exp) {
      Collection<MapleCharacter> list = getPlayerStorage().getAllCharacters();

      for (MapleCharacter chr : list) {
         if (!chr.isLoggedin()) {
            continue;
         }
         chr.revertWorldRates();
      }
      this.exprate = exp;
      for (MapleCharacter chr : list) {
         if (!chr.isLoggedin()) {
            continue;
         }
         chr.setWorldRates();
      }
   }

   public int getDropRate() {
      return droprate;
   }

   public void setDropRate(int drop) {
      Collection<MapleCharacter> list = getPlayerStorage().getAllCharacters();

      for (MapleCharacter chr : list) {
         if (!chr.isLoggedin()) {
            continue;
         }
         chr.revertWorldRates();
      }
      this.droprate = drop;
      for (MapleCharacter chr : list) {
         if (!chr.isLoggedin()) {
            continue;
         }
         chr.setWorldRates();
      }
   }

   public int getBossDropRate() {  // boss rate concept thanks to Lapeiro
      return bossdroprate;
   }

   public void setBossDropRate(int bossdrop) {
      bossdroprate = bossdrop;
   }

   public int getMesoRate() {
      return mesorate;
   }

   public void setMesoRate(int meso) {
      Collection<MapleCharacter> list = getPlayerStorage().getAllCharacters();

      for (MapleCharacter chr : list) {
         if (!chr.isLoggedin()) {
            continue;
         }
         chr.revertWorldRates();
      }
      this.mesorate = meso;
      for (MapleCharacter chr : list) {
         if (!chr.isLoggedin()) {
            continue;
         }
         chr.setWorldRates();
      }
   }

   public int getQuestRate() {
      return questrate;
   }

   public void setQuestRate(int quest) {
      this.questrate = quest;
   }

   public int getTravelRate() {
      return travelrate;
   }

   public void setTravelRate(int travel) {
      this.travelrate = travel;
   }

   public int getTransportationTime(int travelTime) {
      return (int) Math.ceil(travelTime / travelrate);
   }

   public int getFishingRate() {
      return fishingrate;
   }

   public void setFishingRate(int quest) {
      this.fishingrate = quest;
   }

   public void loadAccountCharactersView(Integer accountId, List<MapleCharacter> chars) {
      SortedMap<Integer, MapleCharacter> charsMap = new TreeMap<>();
      for (MapleCharacter chr : chars) {
         charsMap.put(chr.getId(), chr);
      }

      accountCharsLock.lock();    // accountCharsLock should be used after server's lgnWLock for compliance
      try {
         accountChars.put(accountId, charsMap);
      } finally {
         accountCharsLock.unlock();
      }
   }

   public void registerAccountCharacterView(Integer accountId, MapleCharacter chr) {
      accountCharsLock.lock();
      try {
         accountChars.get(accountId).put(chr.getId(), chr);
      } finally {
         accountCharsLock.unlock();
      }
   }

   public void unregisterAccountCharacterView(Integer accountId, Integer chrId) {
      accountCharsLock.lock();
      try {
         accountChars.get(accountId).remove(chrId);
      } finally {
         accountCharsLock.unlock();
      }
   }

   public void clearAccountCharacterView(Integer accountId) {
      accountCharsLock.lock();
      try {
         SortedMap<Integer, MapleCharacter> accChars = accountChars.remove(accountId);
         if (accChars != null) {
            accChars.clear();
         }
      } finally {
         accountCharsLock.unlock();
      }
   }

   public void registerAccountStorage(Integer accountId) {
      MapleStorage storage = MapleStorage.loadOrCreateFromDB(accountId, this.id);
      accountCharsLock.lock();
      try {
         accountStorages.put(accountId, storage);
      } finally {
         accountCharsLock.unlock();
      }
   }

   public void unregisterAccountStorage(Integer accountId) {
      accountCharsLock.lock();
      try {
         accountStorages.remove(accountId);
      } finally {
         accountCharsLock.unlock();
      }
   }

   public MapleStorage getAccountStorage(Integer accountId) {
      return accountStorages.get(accountId);
   }

   public List<MapleCharacter> loadAndGetAllCharactersView() {
      Server.getInstance().loadAllAccountsCharactersView();
      return getAllCharactersView();
   }

   public List<MapleCharacter> getAllCharactersView() {    // sorted by accountid, charid
      List<MapleCharacter> chrList = new LinkedList<>();
      Map<Integer, SortedMap<Integer, MapleCharacter>> accChars;

      accountCharsLock.lock();
      try {
         accChars = new HashMap<>(accountChars);
      } finally {
         accountCharsLock.unlock();
      }

      for (Entry<Integer, SortedMap<Integer, MapleCharacter>> e : getSortedAccountCharacterView(accChars)) {
         chrList.addAll(e.getValue().values());
      }

      return chrList;
   }

   public List<MapleCharacter> getAccountCharactersView(Integer accountId) {
      List<MapleCharacter> chrList;

      accountCharsLock.lock();
      try {
         SortedMap<Integer, MapleCharacter> accChars = accountChars.get(accountId);

         if (accChars != null) {
            chrList = new LinkedList<>(accChars.values());
         } else {
            accountChars.put(accountId, new TreeMap<>());
            chrList = null;
         }
      } finally {
         accountCharsLock.unlock();
      }

      return chrList;
   }

   public PlayerStorage getPlayerStorage() {
      return players;
   }

   public MapleMatchCheckerCoordinator getMatchCheckerCoordinator() {
      return matchChecker;
   }

   public MaplePartySearchCoordinator getPartySearchCoordinator() {
      return partySearch;
   }

   public void addPlayer(MapleCharacter chr) {
      players.addPlayer(chr);
   }

   public void removePlayer(MapleCharacter chr) {
      Channel cserv = chr.getClient().getChannelServer();

      if (cserv != null) {
         if (!cserv.removePlayer(chr)) {
            // oy the player is not where it should be, find this mf

            for (Channel ch : getChannels()) {
               if (ch.removePlayer(chr)) {
                  break;
               }
            }
         }
      }

      players.removePlayer(chr.getId());
   }

   public int getId() {
      return id;
   }

   public void addFamily(int id, MapleFamily f) {
      synchronized (families) {
         if (!families.containsKey(id)) {
            families.put(id, f);
         }
      }
   }

   public void removeFamily(int id) {
      synchronized (families) {
         families.remove(id);
      }
   }

   public MapleFamily getFamily(int id) {
      synchronized (families) {
         if (families.containsKey(id)) {
            return families.get(id);
         }
         return null;
      }
   }

   public Collection<MapleFamily> getFamilies() {
      synchronized (families) {
         return Collections.unmodifiableCollection(families.values());
      }
   }

   public Optional<MapleGuild> getGuild(MapleGuildCharacter mgc) {
      if (mgc == null) {
         return Optional.empty();
      }

      int guildId = mgc.getGuildId();

      return Server.getInstance().getGuild(guildId, mgc.getWorld(), mgc.getCharacter()).map(guild -> {
         if (gsStore.get(guildId) == null) {
            gsStore.put(guildId, new MapleGuildSummary(guild));
         }
         return guild;
      });
   }

   public boolean isWorldCapacityFull() {
      return getWorldCapacityStatus() == 2;
   }

   public int getWorldCapacityStatus() {
      int worldCap = getChannelsSize() * ServerConstants.CHANNEL_LOAD;
      int num = players.getSize();

      int status;
      if (num >= worldCap) {
         status = 2;
      } else if (num >= worldCap * .8) { // More than 80 percent o___o
         status = 1;
      } else {
         status = 0;
      }

      return status;
   }

   public MapleGuildSummary getGuildSummary(int gid, int wid) {
      if (gsStore.containsKey(gid)) {
         return gsStore.get(gid);
      } else {
         Server.getInstance().getGuild(gid, wid, null)
               .ifPresent(guild -> gsStore.put(gid, new MapleGuildSummary(guild)));
         return gsStore.get(gid);
      }
   }

   public void updateGuildSummary(int gid, MapleGuildSummary mgs) {
      gsStore.put(gid, mgs);
   }

   public void reloadGuildSummary() {
      Server server = Server.getInstance();
      for (int i : gsStore.keySet()) {
         server.getGuild(i, getId(), null)
               .ifPresentOrElse(guild -> gsStore.put(i, new MapleGuildSummary(guild)), () -> gsStore.remove(i));
      }
   }

   public void setGuildAndRank(List<Integer> cids, int guildid, int rank, int exception) {
      for (int cid : cids) {
         if (cid != exception) {
            setGuildAndRank(cid, guildid, rank);
         }
      }
   }

   public void setOfflineGuildStatus(int guildid, int guildrank, int cid) {
      DatabaseConnection.getInstance().withConnection(connection -> CharacterAdministrator.getInstance().updateGuildStatus(connection, guildid, guildrank, cid));
   }

   public void setGuildAndRank(int cid, int guildid, int rank) {
      getPlayerStorage().getCharacterById(cid).ifPresent(character -> {
         boolean bDifferentGuild;
         if (guildid == -1 && rank == -1) {
            bDifferentGuild = true;
         } else {
            bDifferentGuild = guildid != character.getGuildId();
            character.getMGC().setGuildId(guildid);
            character.getMGC().setGuildRank(rank);

            if (bDifferentGuild) {
               character.getMGC().setAllianceRank(5);
            }

            character.saveGuildStatus();
         }
         if (bDifferentGuild) {
            if (character.isLoggedinWorld()) {
               Server.getInstance().getGuild(guildid).ifPresentOrElse(guild -> {
                  character.getMap().broadcastMessage(character, PacketCreator.create(new GuildNameChange(cid, guild.getName())));
                  character.getMap().broadcastMessage(character, PacketCreator.create(new GuildMarkChanged(cid, guild.getLogoBG(), guild.getLogoBGColor(), guild.getLogo(), guild.getLogoColor())));
               }, () -> character.getMap().broadcastMessage(character, PacketCreator.create(new GuildNameChange(cid, ""))));
            }
         }
      });
   }

   public void changeEmblem(int gid, List<Integer> affectedPlayers, MapleGuildSummary mgs) {
      updateGuildSummary(gid, mgs);
      MasterBroadcaster.getInstance().sendToWorld(this, affectedPlayers, character ->
            PacketCreator.create(new GuildEmblemChange(gid, mgs.getLogoBG(), mgs.getLogoBGColor(), mgs.getLogo(), mgs.getLogoColor())), true, -1);
      setGuildAndRank(affectedPlayers, -1, -1, -1);   //respawn player
   }

   public boolean isGuildQueued(int guildId) {
      return queuedGuilds.contains(guildId);
   }

   public void putGuildQueued(int guildId) {
      queuedGuilds.add(guildId);
   }

   public void removeGuildQueued(int guildId) {
      queuedGuilds.remove(guildId);
   }

   public boolean isMarriageQueued(int marriageId) {
      return queuedMarriages.containsKey(marriageId);
   }

   public Pair<Boolean, Boolean> getMarriageQueuedLocation(int marriageId) {
      Pair<Pair<Boolean, Boolean>, Pair<Integer, Integer>> qm = queuedMarriages.get(marriageId);
      return (qm != null) ? qm.getLeft() : null;
   }

   public Pair<Integer, Integer> getMarriageQueuedCouple(int marriageId) {
      Pair<Pair<Boolean, Boolean>, Pair<Integer, Integer>> qm = queuedMarriages.get(marriageId);
      return (qm != null) ? qm.getRight() : null;
   }

   public void putMarriageQueued(int marriageId, boolean cathedral, boolean premium, int groomId, int brideId) {
      queuedMarriages.put(marriageId, new Pair<>(new Pair<>(cathedral, premium), new Pair<>(groomId, brideId)));
      marriageGuests.put(marriageId, new HashSet<>());
   }

   public Pair<Boolean, Set<Integer>> removeMarriageQueued(int marriageId) {
      Boolean type = queuedMarriages.remove(marriageId).getLeft().getRight();
      Set<Integer> guests = marriageGuests.remove(marriageId);

      return new Pair<>(type, guests);
   }

   public boolean addMarriageGuest(int marriageId, int playerId) {
      Set<Integer> guests = marriageGuests.get(marriageId);
      if (guests != null) {
         if (guests.contains(playerId)) {
            return false;
         }

         guests.add(playerId);
         return true;
      }

      return false;
   }

   public Pair<Integer, Integer> getWeddingCoupleForGuest(int guestId, Boolean cathedral) {
      for (Channel ch : getChannels()) {
         Pair<Integer, Integer> p = ch.getWeddingCoupleForGuest(guestId, cathedral);
         if (p != null) {
            return p;
         }
      }

      List<Integer> possibleWeddings = new LinkedList<>();
      for (Entry<Integer, Set<Integer>> mg : new HashSet<>(marriageGuests.entrySet())) {
         if (mg.getValue().contains(guestId)) {
            Pair<Boolean, Boolean> loc = getMarriageQueuedLocation(mg.getKey());
            if (loc != null && cathedral.equals(loc.getLeft())) {
               possibleWeddings.add(mg.getKey());
            }
         }
      }

      int pwSize = possibleWeddings.size();
      if (pwSize == 0) {
         return null;
      } else if (pwSize > 1) {
         int selectedPw = -1;
         int selectedPos = Integer.MAX_VALUE;

         for (Integer pw : possibleWeddings) {
            for (Channel ch : getChannels()) {
               int pos = ch.getWeddingReservationStatus(pw, cathedral);
               if (pos != -1) {
                  if (pos < selectedPos) {
                     selectedPos = pos;
                     selectedPw = pw;
                     break;
                  }
               }
            }
         }

         if (selectedPw == -1) {
            return null;
         }

         possibleWeddings.clear();
         possibleWeddings.add(selectedPw);
      }

      return getMarriageQueuedCouple(possibleWeddings.get(0));
   }

   public void debugMarriageStatus() {
      System.out.println("Queued marriages: " + queuedMarriages);
      System.out.println("Guest list: " + marriageGuests);
   }

   private void registerCharacterParty(Integer chrid, Integer partyid) {
      partyLock.lock();
      try {
         partyChars.put(chrid, partyid);
      } finally {
         partyLock.unlock();
      }
   }

   private void unregisterCharacterPartyInternal(Integer chrid) {
      partyChars.remove(chrid);
   }

   private void unregisterCharacterParty(Integer chrid) {
      partyLock.lock();
      try {
         unregisterCharacterPartyInternal(chrid);
      } finally {
         partyLock.unlock();
      }
   }

   public Integer getCharacterPartyid(Integer chrid) {
      partyLock.lock();
      try {
         return partyChars.get(chrid);
      } finally {
         partyLock.unlock();
      }
   }

   public MapleParty createParty(MaplePartyCharacter chrfor) {
      int partyid = runningPartyId.getAndIncrement();
      MapleParty party = new MapleParty(partyid, chrfor);

      partyLock.lock();
      try {
         parties.put(party.getId(), party);
         registerCharacterParty(chrfor.getId(), partyid);
      } finally {
         partyLock.unlock();
      }

      party.addMember(chrfor);
      return party;
   }

   public MapleParty getParty(int partyid) {
      partyLock.lock();
      try {
         return parties.get(partyid);
      } finally {
         partyLock.unlock();
      }
   }

   private MapleParty disbandParty(int partyid) {
      partyLock.lock();
      try {
         return parties.remove(partyid);
      } finally {
         partyLock.unlock();
      }
   }

   private void updateCharacterParty(MapleParty party, PartyOperation operation, MaplePartyCharacter target, Collection<MaplePartyCharacter> partyMembers) {
      switch (operation) {
         case JOIN:
            registerCharacterParty(target.getId(), party.getId());
            break;

         case LEAVE:
         case EXPEL:
            unregisterCharacterParty(target.getId());
            break;

         case DISBAND:
            partyLock.lock();
            try {
               for (MaplePartyCharacter partychar : partyMembers) {
                  unregisterCharacterPartyInternal(partychar.getId());
               }
            } finally {
               partyLock.unlock();
            }
            break;

         default:
            break;
      }
   }

   private void updateParty(MapleParty party, PartyOperation operation, MaplePartyCharacter target) {
      Collection<MaplePartyCharacter> partyMembers = party.getMembers();
      updateCharacterParty(party, operation, target, partyMembers);

      for (MaplePartyCharacter partychar : partyMembers) {
         getPlayerStorage().getCharacterById(partychar.getId()).ifPresent(character -> {
            if (operation == PartyOperation.DISBAND) {
               character.setParty(null);
               character.setMPC(null);
            } else {
               character.setParty(party);
               character.setMPC(partychar);
            }
            PacketCreator.announce(character, new UpdateParty(character.getClient().getChannel(), party, operation, target));
         });
      }
      switch (operation) {
         case LEAVE:
         case EXPEL:
            getPlayerStorage().getCharacterById(target.getId()).ifPresent(character -> {
               PacketCreator.announce(character, new UpdateParty(character.getClient().getChannel(), party, operation, target));
               character.setParty(null);
               character.setMPC(null);
            });
         default:
            break;
      }
   }

   public void updateParty(int partyid, PartyOperation operation, MaplePartyCharacter target) {
      MapleParty party = getParty(partyid);
      if (party == null) {
         throw new IllegalArgumentException("no party with the specified partyid exists");
      }
      switch (operation) {
         case JOIN:
            party.addMember(target);
            break;
         case EXPEL:
         case LEAVE:
            party.removeMember(target);
            break;
         case DISBAND:
            disbandParty(partyid);
            break;
         case SILENT_UPDATE:
         case LOG_ONOFF:
            party.updateMember(target);
            break;
         case CHANGE_LEADER:
            MapleCharacter mc = party.getLeader().getPlayer();
            if (mc != null) {
               EventInstanceManager eim = mc.getEventInstance();

               if (eim != null && eim.isEventLeader(mc)) {
                  eim.changedLeader(target);
               } else {
                  int oldLeaderMapid = mc.getMapId();

                  if (MapleMiniDungeonInfo.isDungeonMap(oldLeaderMapid)) {
                     if (oldLeaderMapid != target.getMapId()) {
                        MapleMiniDungeon mmd = mc.getClient().getChannelServer().getMiniDungeon(oldLeaderMapid);
                        if (mmd != null) {
                           mmd.close();
                        }
                     }
                  }
               }
               party.setLeader(target);
            }
            break;
         default:
            System.out.println("Unhandled updateParty operation " + operation.name());
      }
      updateParty(party, operation, target);
   }

   public void removeMapPartyMembers(int partyid) {
      MapleParty party = getParty(partyid);
      if (party == null) {
         return;
      }

      for (MaplePartyCharacter mpc : party.getMembers()) {
         MapleCharacter mc = mpc.getPlayer();
         if (mc != null) {
            MapleMap map = mc.getMap();
            if (map != null) {
               map.removeParty(partyid);
            }
         }
      }
   }

   public int find(String name) {
      return getPlayerStorage()
            .getCharacterByName(name)
            .map(character -> character.getClient().getChannel())
            .orElse(-1);
   }

   public int find(int id) {
      return getPlayerStorage().getCharacterById(id).map(character -> character.getClient().getChannel()).orElse(-1);
   }

   public void partyChat(MapleParty party, String chattext, String namefrom) {
      for (MaplePartyCharacter partychar : party.getMembers()) {
         if (!(partychar.getName().equals(namefrom))) {
            getPlayerStorage().getCharacterByName(partychar.getName())
                  .ifPresent(character -> PacketCreator.announce(character, new MultiChat(namefrom, chattext, 1)));
         }
      }
   }

   public void buddyChat(int[] recipientCharacterIds, int cidFrom, String nameFrom, String chattext) {
      for (int characterId : recipientCharacterIds) {
         getPlayerStorage().getCharacterById(characterId).ifPresent(character -> {
            if (character.getBuddylist().containsVisible(cidFrom)) {
               PacketCreator.announce(character, new MultiChat(nameFrom, chattext, 0));
            }

         });
      }
   }

   public CharacterIdChannelPair[] multiBuddyFind(int charIdFrom, int[] characterIds) {
      List<CharacterIdChannelPair> foundsChars = new ArrayList<>(characterIds.length);
      for (Channel ch : getChannels()) {
         for (int charid : ch.multiBuddyFind(charIdFrom, characterIds)) {
            foundsChars.add(new CharacterIdChannelPair(charid, ch.getId()));
         }
      }
      return foundsChars.toArray(new CharacterIdChannelPair[foundsChars.size()]);
   }

   public Optional<MapleMessenger> getMessenger(int messengerId) {
      return Optional.ofNullable(messengers.get(messengerId));
   }

   public void leaveMessenger(int messengerId, MapleMessengerCharacter target) {
      getMessenger(messengerId).ifPresent(messenger -> {
         int position = messenger.getPositionByName(target.getName());
         messenger.removeMember(target);
         removeMessengerPlayer(messenger, position);
      });
   }

   public void messengerInvite(String sender, int messengerid, String targetName, int fromchannel) {
      if (isConnected(targetName)) {
         getPlayerStorage().getCharacterByName(targetName).ifPresent(target -> target.getMessenger()
               .ifPresentOrElse(messenger -> {
                  getChannel(fromchannel).getPlayerStorage().getCharacterByName(sender).ifPresent(from -> PacketCreator.announce(from, new MessengerChat(sender + " : " + target + " is already using Maple Messenger")));
               }, () -> {
                  getChannel(fromchannel).getPlayerStorage().getCharacterByName(sender).ifPresent(from -> {
                     if (MapleInviteCoordinator.createInvite(InviteType.MESSENGER, from, messengerid, target.getId())) {
                        PacketCreator.announce(target, new MessengerInvite(sender, messengerid));
                        PacketCreator.announce(from, new MessengerNote(targetName, 4, 1));
                     } else {
                        PacketCreator.announce(from, new MessengerChat(sender + " : " + target + " is already managing a Maple Messenger invitation"));
                     }
                  });
               }));
      }
   }

   public void addMessengerPlayer(MapleMessenger messenger, String namefrom, int fromchannel, int position) {
      for (MapleMessengerCharacter messengerchar : messenger.getMembers()) {
         getPlayerStorage().getCharacterByName(messengerchar.getName()).ifPresent(character -> {
            if (!messengerchar.getName().equals(namefrom)) {
               getChannel(fromchannel).getPlayerStorage().getCharacterByName(namefrom).ifPresent(from -> {
                  PacketCreator.announce(character, new MessengerAddCharacter(namefrom, from, position, (byte) (fromchannel - 1)));
                  PacketCreator.announce(from, new MessengerAddCharacter(character.getName(), character, messengerchar.getPosition(), (byte) (messengerchar.getChannel() - 1)));
               });
            } else {
               PacketCreator.announce(character, new MessengerJoin(messengerchar.getPosition()));
            }
         });
      }
   }

   public void removeMessengerPlayer(MapleMessenger messenger, int position) {
      for (MapleMessengerCharacter messengerchar : messenger.getMembers()) {
         getPlayerStorage().getCharacterByName(messengerchar.getName())
               .ifPresent(character -> PacketCreator.announce(character, new MessengerRemoveCharacter(position)));
      }
   }

   public void messengerChat(MapleMessenger messenger, String chattext, String namefrom) {
      for (MapleMessengerCharacter messengerchar : messenger.getMembers()) {
         if (!(messengerchar.getName().equals(namefrom))) {
            getPlayerStorage().getCharacterByName(messengerchar.getName())
                  .ifPresent(character -> PacketCreator.announce(character, new MessengerChat(chattext)));
         }
      }
   }

   public void declineChat(String sender, MapleCharacter player) {
      if (isConnected(sender)) {
         getPlayerStorage().getCharacterByName(sender).ifPresent(character -> character.getMessenger()
               .ifPresent(messenger -> {
                  if (MapleInviteCoordinator.answerInvite(InviteType.MESSENGER, player.getId(), messenger.getId(), false).result == InviteResult.DENIED) {
                     PacketCreator.announce(character, new MessengerNote(player.getName(), 5, 0));
                  }
               }));
      }
   }

   public void updateMessenger(int messengerId, String nameFrom, int channelFrom) {
      getMessenger(messengerId).ifPresent(messenger -> {
         int position = messenger.getPositionByName(nameFrom);
         updateMessenger(messenger, nameFrom, position, channelFrom);
      });
   }

   public void updateMessenger(MapleMessenger messenger, String namefrom, int position, int fromchannel) {
      for (MapleMessengerCharacter messengerchar : messenger.getMembers()) {
         Channel ch = getChannel(fromchannel);
         if (!(messengerchar.getName().equals(namefrom))) {
            ch.getPlayerStorage().getCharacterByName(messengerchar.getName())
                  .ifPresent(character -> getChannel(fromchannel).getPlayerStorage().getCharacterByName(namefrom)
                        .ifPresent(from -> PacketCreator.announce(character, new MessengerUpdateCharacter(namefrom, from, position, (byte) (fromchannel - 1)))));
         }
      }
   }

   //TODO - seems like a bug
   public void silentLeaveMessenger(int messengerId, MapleMessengerCharacter target) {
      getMessenger(messengerId).ifPresent(messenger -> messenger.addMember(target, target.getPosition()));
   }

   public void joinMessenger(int messengerId, MapleMessengerCharacter target, String nameFrom, int channelFrom) {
      getMessenger(messengerId).ifPresent(messenger -> {
         messenger.addMember(target, target.getPosition());
         addMessengerPlayer(messenger, nameFrom, channelFrom, target.getPosition());
      });
   }

   public void silentJoinMessenger(int messengerId, MapleMessengerCharacter target, int position) {
      getMessenger(messengerId).ifPresent(messenger -> messenger.addMember(target, position));
   }

   public MapleMessenger createMessenger(MapleMessengerCharacter chrfor) {
      int messengerid = runningMessengerId.getAndIncrement();
      MapleMessenger messenger = new MapleMessenger(messengerid, chrfor);
      messengers.put(messenger.getId(), messenger);
      return messenger;
   }

   public boolean isConnected(String charName) {
      return getPlayerStorage().getCharacterByName(charName).isPresent();
   }

   public void whisper(String sender, String target, int channel, String message) {
      if (isConnected(target)) {
         getPlayerStorage().getCharacterByName(target).ifPresent(character -> PacketCreator.announce(character, new Whisper(sender, channel, message)));
      }
   }

   public BuddyListAddResult requestBuddyAdd(String addName, int channelFrom, int cidFrom, String nameFrom) {
      return getPlayerStorage().getCharacterByName(addName)
            .map(addChar -> {
               BuddyList buddylist = addChar.getBuddylist();
               if (buddylist.isFull()) {
                  return BuddyListAddResult.FULL;
               }
               if (!buddylist.contains(cidFrom)) {
                  BuddyListProcessor.getInstance().addBuddyRequest(addChar, cidFrom, nameFrom, channelFrom);
               } else if (buddylist.containsVisible(cidFrom)) {
                  return BuddyListAddResult.ALREADY_ON_LIST;
               }
               return BuddyListAddResult.OK;
            })
            .orElse(BuddyListAddResult.OK);
   }

   public void buddyChanged(int cid, int cidFrom, String name, int channel, BuddyListOperation operation) {
      getPlayerStorage().getCharacterById(cid).ifPresent(addChar -> {
         BuddyList buddylist = addChar.getBuddylist();
         switch (operation) {
            case ADDED:
               if (buddylist.contains(cidFrom)) {
                  buddylist.put(new BuddyListEntry(name, "Default Group", cidFrom, channel, true));
                  PacketCreator.announce(addChar, new UpdateBuddyChannel(cidFrom, (byte) (channel - 1)));
               }
               break;
            case DELETED:
               if (buddylist.contains(cidFrom)) {
                  buddylist.put(new BuddyListEntry(name, "Default Group", cidFrom, (byte) -1, buddylist.get(cidFrom).visible()));
                  PacketCreator.announce(addChar, new UpdateBuddyChannel(cidFrom, (byte) -1));
               }
               break;
         }
      });
   }

   public void loggedOff(String name, int characterId, int channel, int[] buddies) {
      updateBuddies(characterId, channel, buddies, true);
   }

   public void loggedOn(String name, int characterId, int channel, int[] buddies) {
      updateBuddies(characterId, channel, buddies, false);
   }

   private void updateBuddies(int characterId, int channel, int[] buddies, boolean offline) {
      PlayerStorage playerStorage = getPlayerStorage();
      for (int buddy : buddies) {
         playerStorage.getCharacterById(buddy).ifPresent(character -> {
            BuddyListEntry ble = character.getBuddylist().get(characterId);
            if (ble != null && ble.visible()) {
               int mcChannel;
               if (offline) {
                  ble.channel_$eq((byte) -1);
                  mcChannel = -1;
               } else {
                  ble.channel_$eq(channel);
                  mcChannel = (byte) (channel - 1);
               }
               character.getBuddylist().put(ble);
               PacketCreator.announce(character, new UpdateBuddyChannel(ble.characterId(), mcChannel));
            }
         });
      }
   }

   public void addOwlItemSearch(Integer itemid) {
      suggestWLock.lock();
      try {
         owlSearched.merge(itemid, 1, Integer::sum);
      } finally {
         suggestWLock.unlock();
      }
   }

   public List<Pair<Integer, Integer>> getOwlSearchedItems() {
      if (ServerConstants.USE_ENFORCE_ITEM_SUGGESTION) {
         return new ArrayList<>(0);
      }

      suggestRLock.lock();
      try {
         List<Pair<Integer, Integer>> searchCounts = new ArrayList<>(owlSearched.size());

         for (Entry<Integer, Integer> e : owlSearched.entrySet()) {
            searchCounts.add(new Pair<>(e.getKey(), e.getValue()));
         }

         return searchCounts;
      } finally {
         suggestRLock.unlock();
      }
   }

   public void addCashItemBought(Integer snid) {
      suggestWLock.lock();
      try {
         Map<Integer, Integer> tabItemBought = cashItemBought.get(snid / 10000000);

         tabItemBought.merge(snid, 1, Integer::sum);
      } finally {
         suggestWLock.unlock();
      }
   }

   private List<List<Pair<Integer, Integer>>> getBoughtCashItems() {
      if (ServerConstants.USE_ENFORCE_ITEM_SUGGESTION) {
         List<List<Pair<Integer, Integer>>> boughtCounts = new ArrayList<>(9);

         // thanks GabrielSin for pointing out an issue here
         for (int i = 0; i < 9; i++) {
            List<Pair<Integer, Integer>> tabCounts = new ArrayList<>(0);
            boughtCounts.add(tabCounts);
         }

         return boughtCounts;
      }

      suggestRLock.lock();
      try {
         List<List<Pair<Integer, Integer>>> boughtCounts = new ArrayList<>(cashItemBought.size());

         for (Map<Integer, Integer> tab : cashItemBought) {
            List<Pair<Integer, Integer>> tabItems = new LinkedList<>();
            boughtCounts.add(tabItems);

            for (Entry<Integer, Integer> e : tab.entrySet()) {
               tabItems.add(new Pair<>(e.getKey(), e.getValue()));
            }
         }

         return boughtCounts;
      } finally {
         suggestRLock.unlock();
      }
   }

   private List<Integer> getMostSellerOnTab(List<Pair<Integer, Integer>> tabSellers) {
      List<Integer> tabLeaderboards;

      Comparator<Pair<Integer, Integer>> comparator = new Comparator<>() {  // descending order
         @Override
         public int compare(Pair<Integer, Integer> p1, Pair<Integer, Integer> p2) {
            return p2.getRight().compareTo(p1.getRight());
         }
      };

      PriorityQueue<Pair<Integer, Integer>> queue = new PriorityQueue<>(Math.max(1, tabSellers.size()), comparator);
      queue.addAll(tabSellers);

      tabLeaderboards = new LinkedList<>();
      for (int i = 0; i < Math.min(tabSellers.size(), 5); i++) {
         tabLeaderboards.add(queue.remove().getLeft());
      }

      return tabLeaderboards;
   }

   public List<List<Integer>> getMostSellerCashItems() {
      List<List<Pair<Integer, Integer>>> mostSellers = this.getBoughtCashItems();
      List<List<Integer>> cashLeaderboards = new ArrayList<>(9);
      List<Integer> tabLeaderboards;
      List<Integer> allLeaderboards = null;

      for (List<Pair<Integer, Integer>> tabSellers : mostSellers) {
         if (tabSellers.size() < 5) {
            if (allLeaderboards == null) {
               List<Pair<Integer, Integer>> allSellers = new LinkedList<>();
               for (List<Pair<Integer, Integer>> tabItems : mostSellers) {
                  allSellers.addAll(tabItems);
               }

               allLeaderboards = getMostSellerOnTab(allSellers);
            }

            tabLeaderboards = new LinkedList<>();
            if (allLeaderboards.size() < 5) {
               for (int i : GameConstants.CASH_DATA) {
                  tabLeaderboards.add(i);
               }
            } else {
               tabLeaderboards.addAll(allLeaderboards);
            }
         } else {
            tabLeaderboards = getMostSellerOnTab(tabSellers);
         }

         cashLeaderboards.add(tabLeaderboards);
      }

      return cashLeaderboards;
   }

   public void registerPetHunger(MapleCharacter chr, byte petSlot) {
      if (chr.isGM() && ServerConstants.GM_PETS_NEVER_HUNGRY || ServerConstants.PETS_NEVER_HUNGRY) {
         return;
      }

      Integer key = getPetKey(chr, petSlot);

      activePetsLock.lock();
      try {
         int initProc;
         if (Server.getInstance().getCurrentTime() - petUpdate > 55000) {
            initProc = ServerConstants.PET_EXHAUST_COUNT - 2;
         } else {
            initProc = ServerConstants.PET_EXHAUST_COUNT - 1;
         }

         activePets.put(key, initProc);
      } finally {
         activePetsLock.unlock();
      }
   }

   public void unregisterPetHunger(MapleCharacter chr, byte petSlot) {
      Integer key = getPetKey(chr, petSlot);

      activePetsLock.lock();
      try {
         activePets.remove(key);
      } finally {
         activePetsLock.unlock();
      }
   }

   public void runPetSchedule() {
      Map<Integer, Integer> deployedPets;

      activePetsLock.lock();
      try {
         petUpdate = Server.getInstance().getCurrentTime();
         deployedPets = new HashMap<>(activePets);   // exception here found thanks to MedicOP
      } finally {
         activePetsLock.unlock();
      }

      for (Map.Entry<Integer, Integer> dp : deployedPets.entrySet()) {
         int characterId = dp.getKey() / 4;
         getPlayerStorage().getCharacterById(characterId)
               .filter(MapleCharacter::isLoggedinWorld)
               .ifPresent(character -> {
                  int dpVal = dp.getValue() + 1;
                  if (dpVal == ServerConstants.PET_EXHAUST_COUNT) {
                     character.runFullnessSchedule(dp.getKey() % 4);
                     dpVal = 0;
                  }

                  activePetsLock.lock();
                  try {
                     activePets.put(dp.getKey(), dpVal);
                  } finally {
                     activePetsLock.unlock();
                  }
               });

      }
   }

   public void registerMountHunger(MapleCharacter chr) {
      if (chr.isGM() && ServerConstants.GM_PETS_NEVER_HUNGRY || ServerConstants.PETS_NEVER_HUNGRY) {
         return;
      }

      Integer key = chr.getId();
      activeMountsLock.lock();
      try {
         int initProc;
         if (Server.getInstance().getCurrentTime() - mountUpdate > 45000) {
            initProc = ServerConstants.MOUNT_EXHAUST_COUNT - 2;
         } else {
            initProc = ServerConstants.MOUNT_EXHAUST_COUNT - 1;
         }

         activeMounts.put(key, initProc);
      } finally {
         activeMountsLock.unlock();
      }
   }

   public void unregisterMountHunger(MapleCharacter chr) {
      Integer key = chr.getId();

      activeMountsLock.lock();
      try {
         activeMounts.remove(key);
      } finally {
         activeMountsLock.unlock();
      }
   }

   public void runMountSchedule() {
      Map<Integer, Integer> deployedMounts;
      activeMountsLock.lock();
      try {
         mountUpdate = Server.getInstance().getCurrentTime();
         deployedMounts = new HashMap<>(activeMounts);
      } finally {
         activeMountsLock.unlock();
      }

      for (Map.Entry<Integer, Integer> dp : deployedMounts.entrySet()) {
         getPlayerStorage().getCharacterById(dp.getKey())
               .filter(MapleCharacter::isLoggedinWorld)
               .ifPresent(character -> {
                  int dpVal = dp.getValue() + 1;
                  if (dpVal == ServerConstants.MOUNT_EXHAUST_COUNT) {
                     if (!character.runTirednessSchedule()) {
                        return;
                     }
                     dpVal = 0;
                  }

                  activeMountsLock.lock();
                  try {
                     activeMounts.put(dp.getKey(), dpVal);
                  } finally {
                     activeMountsLock.unlock();
                  }
               });
      }
   }

   public void registerPlayerShop(MaplePlayerShop ps) {
      activePlayerShopsLock.lock();
      try {
         activePlayerShops.put(ps.getOwner().getId(), ps);
      } finally {
         activePlayerShopsLock.unlock();
      }
   }

   public void unregisterPlayerShop(MaplePlayerShop ps) {
      activePlayerShopsLock.lock();
      try {
         activePlayerShops.remove(ps.getOwner().getId());
      } finally {
         activePlayerShopsLock.unlock();
      }
   }

   public List<MaplePlayerShop> getActivePlayerShops() {
      activePlayerShopsLock.lock();
      try {
         return new ArrayList<>(activePlayerShops.values());
      } finally {
         activePlayerShopsLock.unlock();
      }
   }

   public MaplePlayerShop getPlayerShop(int ownerid) {
      activePlayerShopsLock.lock();
      try {
         return activePlayerShops.get(ownerid);
      } finally {
         activePlayerShopsLock.unlock();
      }
   }

   public void registerHiredMerchant(MapleHiredMerchant hm) {
      activeMerchantsLock.lock();
      try {
         int initProc;
         if (Server.getInstance().getCurrentTime() - merchantUpdate > 5 * 60 * 1000) {
            initProc = 1;
         } else {
            initProc = 0;
         }

         activeMerchants.put(hm.getOwnerId(), new Pair<>(hm, initProc));
      } finally {
         activeMerchantsLock.unlock();
      }
   }

   public void unregisterHiredMerchant(MapleHiredMerchant hm) {
      activeMerchantsLock.lock();
      try {
         activeMerchants.remove(hm.getOwnerId());
      } finally {
         activeMerchantsLock.unlock();
      }
   }

   public void runHiredMerchantSchedule() {
      Map<Integer, Pair<MapleHiredMerchant, Integer>> deployedMerchants;
      activeMerchantsLock.lock();
      try {
         merchantUpdate = Server.getInstance().getCurrentTime();
         deployedMerchants = new LinkedHashMap<>(activeMerchants);

         for (Map.Entry<Integer, Pair<MapleHiredMerchant, Integer>> dm : deployedMerchants.entrySet()) {
            int timeOn = dm.getValue().getRight();
            MapleHiredMerchant hm = dm.getValue().getLeft();

            if (timeOn <= 144) {   // 1440 minutes == 24hrs
               activeMerchants.put(hm.getOwnerId(), new Pair<>(dm.getValue().getLeft(), timeOn + 1));
            } else {
               hm.forceClose();
               this.getChannel(hm.getChannel()).removeHiredMerchant(hm.getOwnerId());

               activeMerchants.remove(dm.getKey());
            }
         }
      } finally {
         activeMerchantsLock.unlock();
      }
   }

   public List<MapleHiredMerchant> getActiveMerchants() {
      List<MapleHiredMerchant> hmList = new ArrayList<>();
      activeMerchantsLock.lock();
      try {
         for (Pair<MapleHiredMerchant, Integer> hmp : activeMerchants.values()) {
            MapleHiredMerchant hm = hmp.getLeft();
            if (hm.isOpen()) {
               hmList.add(hm);
            }
         }

         return hmList;
      } finally {
         activeMerchantsLock.unlock();
      }
   }

   public MapleHiredMerchant getHiredMerchant(int ownerid) {
      activeMerchantsLock.lock();
      try {
         if (activeMerchants.containsKey(ownerid)) {
            return activeMerchants.get(ownerid).getLeft();
         }

         return null;
      } finally {
         activeMerchantsLock.unlock();
      }
   }

   public void registerTimedMapObject(Runnable r, long duration) {
      timedMapObjectLock.lock();
      try {
         long expirationTime = Server.getInstance().getCurrentTime() + duration;
         registeredTimedMapObjects.put(r, expirationTime);
      } finally {
         timedMapObjectLock.unlock();
      }
   }

   public void runTimedMapObjectSchedule() {
      List<Runnable> toRemove = new LinkedList<>();

      timedMapObjectLock.lock();
      try {
         long timeNow = Server.getInstance().getCurrentTime();

         for (Entry<Runnable, Long> rtmo : registeredTimedMapObjects.entrySet()) {
            if (rtmo.getValue() <= timeNow) {
               toRemove.add(rtmo.getKey());
            }
         }

         for (Runnable r : toRemove) {
            registeredTimedMapObjects.remove(r);
         }
      } finally {
         timedMapObjectLock.unlock();
      }

      for (Runnable r : toRemove) {
         r.run();
      }
   }

   public void resetDisabledServerMessages() {
      srvMessagesLock.lock();
      try {
         disabledServerMessages.clear();
      } finally {
         srvMessagesLock.unlock();
      }
   }

   public boolean registerDisabledServerMessage(int chrid) {
      srvMessagesLock.lock();
      try {
         boolean alreadyDisabled = disabledServerMessages.containsKey(chrid);
         disabledServerMessages.put(chrid, 0);

         return alreadyDisabled;
      } finally {
         srvMessagesLock.unlock();
      }
   }

   public boolean unregisterDisabledServerMessage(int chrid) {
      srvMessagesLock.lock();
      try {
         return disabledServerMessages.remove(chrid) != null;
      } finally {
         srvMessagesLock.unlock();
      }
   }

   public void runDisabledServerMessagesSchedule() {
      List<Integer> toRemove = new LinkedList<>();

      srvMessagesLock.lock();
      try {
         for (Entry<Integer, Integer> dsm : disabledServerMessages.entrySet()) {
            int b = dsm.getValue();
            if (b >= 4) {   // ~35sec duration, 10sec update
               toRemove.add(dsm.getKey());
            } else {
               disabledServerMessages.put(dsm.getKey(), ++b);
            }
         }

         for (Integer chrid : toRemove) {
            disabledServerMessages.remove(chrid);
         }
      } finally {
         srvMessagesLock.unlock();
      }

      if (!toRemove.isEmpty()) {
         for (Integer chrid : toRemove) {
            players.getCharacterById(chrid)
                  .filter(MapleCharacter::isLoggedinWorld)
                  .ifPresent(character -> PacketCreator.announce(character, new ServerMessage(character.getClient().getChannelServer().getServerMessage())));
         }
      }
   }

   public void setPlayerNpcMapStep(int mapid, int step) {
      setPlayerNpcMapData(mapid, step, -1, false);
   }

   public void setPlayerNpcMapPodiumData(int mapid, int podium) {
      setPlayerNpcMapData(mapid, -1, podium, false);
   }

   public void setPlayerNpcMapData(int mapid, int step, int podium) {
      setPlayerNpcMapData(mapid, step, podium, true);
   }

   private void setPlayerNpcMapData(int mapId, int step, int podium, boolean silent) {
      if (!silent) {
         DatabaseConnection.getInstance().withConnection(connection -> {
            if (step != -1) {
               executePlayerNpcMapDataUpdate(connection, false, pnpcStep, step, id, mapId);
            }

            if (podium != -1) {
               executePlayerNpcMapDataUpdate(connection, true, pnpcPodium, podium, id, mapId);
            }
         });
      }

      if (step != -1) {
         pnpcStep.put(mapId, (byte) step);
      }
      if (podium != -1) {
         pnpcPodium.put(mapId, (short) podium);
      }
   }

   public int getPlayerNpcMapStep(int mapid) {
      try {
         return pnpcStep.get(mapid);
      } catch (NullPointerException npe) {
         return 0;
      }
   }

   public int getPlayerNpcMapPodiumData(int mapid) {
      try {
         return pnpcPodium.get(mapid);
      } catch (NullPointerException npe) {
         return 1;
      }
   }

   public void resetPlayerNpcMapData() {
      pnpcStep.clear();
      pnpcPodium.clear();
   }

   public void setServerMessage(String msg) {
      for (Channel ch : getChannels()) {
         ch.setServerMessage(msg);
      }
   }

   public void broadcastPacket(final byte[] data) {
      for (MapleCharacter chr : players.getAllCharacters()) {
         chr.announce(data);
      }
   }

   public List<Pair<MaplePlayerShopItem, AbstractMapleMapObject>> getAvailableItemBundles(int itemid) {
      List<Pair<MaplePlayerShopItem, AbstractMapleMapObject>> hmsAvailable = new ArrayList<>();

      for (MapleHiredMerchant hm : getActiveMerchants()) {
         List<MaplePlayerShopItem> itemBundles = hm.sendAvailableBundles(itemid);

         for (MaplePlayerShopItem mpsi : itemBundles) {
            hmsAvailable.add(new Pair<>(mpsi, hm));
         }
      }

      for (MaplePlayerShop ps : getActivePlayerShops()) {
         List<MaplePlayerShopItem> itemBundles = ps.sendAvailableBundles(itemid);

         for (MaplePlayerShopItem mpsi : itemBundles) {
            hmsAvailable.add(new Pair<>(mpsi, ps));
         }
      }

      hmsAvailable.sort(new Comparator<>() {
         @Override
         public int compare(Pair<MaplePlayerShopItem, AbstractMapleMapObject> p1, Pair<MaplePlayerShopItem, AbstractMapleMapObject> p2) {
            return p1.getLeft().price() - p2.getLeft().price();
         }
      });

      hmsAvailable.subList(0, Math.min(hmsAvailable.size(), 200));    //truncates the list to have up to 200 elements
      return hmsAvailable;
   }

   private void pushRelationshipCouple(Pair<Integer, Pair<Integer, Integer>> couple) {
      int mid = couple.getLeft(), hid = couple.getRight().getLeft(), wid = couple.getRight().getRight();
      relationshipCouples.put(mid, couple.getRight());
      relationships.put(hid, mid);
      relationships.put(wid, mid);
   }

   public Pair<Integer, Integer> getRelationshipCouple(int relationshipId) {
      Pair<Integer, Integer> rc = relationshipCouples.get(relationshipId);

      if (rc == null) {
         Pair<Integer, Pair<Integer, Integer>> couple = getRelationshipCoupleFromDb(relationshipId, true);
         if (couple == null) {
            return null;
         }

         pushRelationshipCouple(couple);
         rc = couple.getRight();
      }

      return rc;
   }

   public int getRelationshipId(int playerId) {
      Integer ret = relationships.get(playerId);

      if (ret == null) {
         Pair<Integer, Pair<Integer, Integer>> couple = getRelationshipCoupleFromDb(playerId, false);
         if (couple == null) {
            return -1;
         }

         pushRelationshipCouple(couple);
         ret = couple.getLeft();
      }

      return ret;
   }

   public int createRelationship(int groomId, int brideId) {
      int ret = addRelationshipToDb(groomId, brideId);

      pushRelationshipCouple(new Pair<>(ret, new Pair<>(groomId, brideId)));
      return ret;
   }

   public void deleteRelationship(int playerId, int partnerId) {
      int relationshipId = relationships.get(playerId);
      deleteRelationshipFromDb(relationshipId);

      relationshipCouples.remove(relationshipId);
      relationships.remove(playerId);
      relationships.remove(partnerId);
   }

   public boolean registerFisherPlayer(MapleCharacter chr, int baitLevel) {
      synchronized (fishingAttempters) {
         if (fishingAttempters.containsKey(chr)) {
            return false;
         }

         fishingAttempters.put(chr, baitLevel);
         return true;
      }
   }

   public int unregisterFisherPlayer(MapleCharacter chr) {
      Integer baitLevel = fishingAttempters.remove(chr);
      return Objects.requireNonNullElse(baitLevel, 0);
   }

   public void runCheckFishingSchedule() {
      double[] fishingLikelihoods = Fishing.fetchFishingLikelihood();
      double yearLikelihood = fishingLikelihoods[0], timeLikelihood = fishingLikelihoods[1];

      if (!fishingAttempters.isEmpty()) {
         List<MapleCharacter> fishingAttemptersList;

         synchronized (fishingAttempters) {
            fishingAttemptersList = new ArrayList<>(fishingAttempters.keySet());
         }

         for (MapleCharacter chr : fishingAttemptersList) {
            int baitLevel = unregisterFisherPlayer(chr);
            Fishing.doFishing(chr, baitLevel, yearLikelihood, timeLikelihood);
         }
      }
   }

   public void runPartySearchUpdateSchedule() {
      partySearch.updatePartySearchStorage();
      partySearch.runPartySearch();
   }

   private void clearWorldData() {
      List<MapleParty> pList;
      partyLock.lock();
      try {
         pList = new ArrayList<>(parties.values());
      } finally {
         partyLock.unlock();
      }

      for (MapleParty p : pList) {
         p.disposeLocks();
      }

      disposeLocks();
   }

   private void disposeLocks() {
      LockCollector.getInstance().registerDisposeAction(this::emptyLocks);
   }

   private void emptyLocks() {
      accountCharsLock = accountCharsLock.dispose();
      partyLock = partyLock.dispose();
      srvMessagesLock = srvMessagesLock.dispose();
      activePetsLock = activePetsLock.dispose();
      activeMountsLock = activeMountsLock.dispose();
      activePlayerShopsLock = activePlayerShopsLock.dispose();
      activeMerchantsLock = activeMerchantsLock.dispose();
      timedMapObjectLock = timedMapObjectLock.dispose();
   }

   public final void shutdown() {
      for (Channel ch : getChannels()) {
         ch.shutdown();
      }

      if (petsSchedule != null) {
         petsSchedule.cancel(false);
         petsSchedule = null;
      }

      if (srvMessagesSchedule != null) {
         srvMessagesSchedule.cancel(false);
         srvMessagesSchedule = null;
      }

      if (mountsSchedule != null) {
         mountsSchedule.cancel(false);
         mountsSchedule = null;
      }

      if (merchantSchedule != null) {
         merchantSchedule.cancel(false);
         merchantSchedule = null;
      }

      if (timedMapObjectsSchedule != null) {
         timedMapObjectsSchedule.cancel(false);
         timedMapObjectsSchedule = null;
      }

      if (charactersSchedule != null) {
         charactersSchedule.cancel(false);
         charactersSchedule = null;
      }

      if (marriagesSchedule != null) {
         marriagesSchedule.cancel(false);
         marriagesSchedule = null;
      }

      if (mapOwnershipSchedule != null) {
         mapOwnershipSchedule.cancel(false);
         mapOwnershipSchedule = null;
      }

      if (fishingSchedule != null) {
         fishingSchedule.cancel(false);
         fishingSchedule = null;
      }

      if (partySearchSchedule != null) {
         partySearchSchedule.cancel(false);
         partySearchSchedule = null;
      }

      players.disconnectAll();
      players = null;

      clearWorldData();
      System.out.println("Finished shutting down world " + id + "\r\n");
   }
}
