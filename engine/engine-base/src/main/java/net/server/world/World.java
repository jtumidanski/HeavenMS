package net.server.world;

import java.util.ArrayList;
import java.util.Arrays;
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
import java.util.function.Consumer;
import javax.persistence.EntityManager;

import client.AbstractMapleCharacterObject;
import client.MapleCharacter;
import client.MapleFamily;
import client.database.data.MarriageData;
import config.YamlConfig;
import constants.game.GameConstants;
import database.DatabaseConnection;
import database.administrator.MarriageAdministrator;
import database.administrator.PlayerNpcAdministrator;
import database.provider.MarriageProvider;
import net.server.PlayerStorage;
import net.server.Server;
import net.server.audit.LockCollector;
import net.server.audit.locks.MonitoredLockType;
import net.server.audit.locks.MonitoredReadLock;
import net.server.audit.locks.MonitoredReentrantLock;
import net.server.audit.locks.MonitoredReentrantReadWriteLock;
import net.server.audit.locks.MonitoredWriteLock;
import net.server.audit.locks.factory.MonitoredReadLockFactory;
import net.server.audit.locks.factory.MonitoredReentrantLockFactory;
import net.server.audit.locks.factory.MonitoredWriteLockFactory;
import net.server.channel.Channel;
import net.server.coordinator.matchchecker.MapleMatchCheckerCoordinator;
import net.server.coordinator.partysearch.MaplePartySearchCoordinator;
import net.server.coordinator.world.MapleInviteCoordinator;
import net.server.coordinator.world.MapleInviteCoordinator.InviteResult;
import net.server.coordinator.world.MapleInviteCoordinator.InviteType;
import net.server.guild.MapleGuild;
import net.server.guild.MapleGuildCharacter;
import net.server.guild.MapleGuildSummary;
import net.server.services.BaseService;
import net.server.services.ServicesManager;
import net.server.services.type.WorldServices;
import net.server.task.CharacterAutoSaveTask;
import net.server.task.FamilyDailyResetTask;
import net.server.task.FishingTask;
import net.server.task.HiredMerchantTask;
import net.server.task.MapOwnershipTask;
import net.server.task.MountTirednessTask;
import net.server.task.PartySearchTask;
import net.server.task.PetFullnessTask;
import net.server.task.ServerMessageTask;
import net.server.task.TimedMapObjectTask;
import net.server.task.TimeoutTask;
import net.server.task.WeddingReservationTask;
import server.MapleStorage;
import server.TimerManager;
import server.maps.AbstractMapleMapObject;
import server.maps.MapleHiredMerchant;
import server.maps.MaplePlayerShop;
import server.maps.MaplePlayerShopItem;
import tools.PacketCreator;
import tools.Pair;
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
import tools.packets.Fishing;

public class World {
   private final MonitoredReentrantReadWriteLock chnLock = new MonitoredReentrantReadWriteLock(MonitoredLockType.WORLD_CHANNELS, true);
   private final MonitoredReentrantReadWriteLock suggestLock = new MonitoredReentrantReadWriteLock(MonitoredLockType.WORLD_SUGGEST, true);
   private int id, flag, expRate, dropRate, bossDropRate, mesoRate, questRate, travelRate, fishingRate;
   private String eventMessage;
   private List<Channel> channels = new ArrayList<>();
   private Map<Integer, Byte> playerNpcStep = new HashMap<>();
   private Map<Integer, Short> playerNpcPodium = new HashMap<>();
   private Map<Integer, MapleMessenger> messengers = new HashMap<>();
   private AtomicInteger runningMessengerId = new AtomicInteger();
   private Map<Integer, MapleFamily> families = new LinkedHashMap<>();
   private Map<Integer, Integer> relationships = new HashMap<>();
   private Map<Integer, Pair<Integer, Integer>> relationshipCouples = new HashMap<>();
   private Map<Integer, MapleGuildSummary> gsStore = new HashMap<>();
   private PlayerStorage players = new PlayerStorage();
   private ServicesManager services = new ServicesManager(WorldServices.SAVE_CHARACTER);
   private MapleMatchCheckerCoordinator matchChecker = new MapleMatchCheckerCoordinator();
   private MaplePartySearchCoordinator partySearch = new MaplePartySearchCoordinator();

   private MonitoredReadLock chnRLock = MonitoredReadLockFactory.createLock(chnLock);
   private MonitoredWriteLock chnWLock = MonitoredWriteLockFactory.createLock(chnLock);
   private Map<Integer, SortedMap<Integer, MapleCharacter>> accountChars = new HashMap<>();
   private Map<Integer, MapleStorage> accountStorage = new HashMap<>();

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
   private MonitoredReadLock suggestRLock = MonitoredReadLockFactory.createLock(suggestLock);
   private MonitoredWriteLock suggestWLock = MonitoredWriteLockFactory.createLock(suggestLock);

   private Map<Integer, Integer> disabledServerMessages = new HashMap<>();    // reuse owl lock
   private MonitoredReentrantLock srvMessagesLock = MonitoredReentrantLockFactory.createLock(MonitoredLockType.WORLD_SERVER_MESSAGES);
   private ScheduledFuture<?> srvMessagesSchedule;

   private MonitoredReentrantLock activePetsLock = MonitoredReentrantLockFactory.createLock(MonitoredLockType.WORLD_PETS, true);
   private Map<Integer, Integer> activePets = new LinkedHashMap<>();
   private ScheduledFuture<?> petsSchedule;
   private long petUpdate;

   private MonitoredReentrantLock activeMountsLock = MonitoredReentrantLockFactory.createLock(MonitoredLockType.WORLD_MOUNTS, true);
   private Map<Integer, Integer> activeMounts = new LinkedHashMap<>();
   private ScheduledFuture<?> mountsSchedule;
   private long mountUpdate;

   private MonitoredReentrantLock activePlayerShopsLock = MonitoredReentrantLockFactory.createLock(MonitoredLockType.WORLD_PLAYER_SHOPS, true);
   private Map<Integer, MaplePlayerShop> activePlayerShops = new LinkedHashMap<>();

   private MonitoredReentrantLock activeMerchantsLock = MonitoredReentrantLockFactory.createLock(MonitoredLockType.WORLD_MERCHANTS, true);
   private Map<Integer, Pair<MapleHiredMerchant, Integer>> activeMerchants = new LinkedHashMap<>();
   private ScheduledFuture<?> merchantSchedule;
   private long merchantUpdate;

   private Map<Runnable, Long> registeredTimedMapObjects = new LinkedHashMap<>();
   private ScheduledFuture<?> timedMapObjectsSchedule;
   private MonitoredReentrantLock timedMapObjectLock = MonitoredReentrantLockFactory.createLock(MonitoredLockType.WORLD_MAP_OBJECTS, true);

   private Map<MapleCharacter, Integer> fishers = Collections.synchronizedMap(new WeakHashMap<>());

   private ScheduledFuture<?> charactersSchedule;
   private ScheduledFuture<?> marriagesSchedule;
   private ScheduledFuture<?> mapOwnershipSchedule;
   private ScheduledFuture<?> fishingSchedule;
   private ScheduledFuture<?> partySearchSchedule;
   private ScheduledFuture<?> timeoutSchedule;

   public World(int world, int flag, String eventMessage, int expRate, int dropRate, int bossDropRate, int mesoRate, int questRate, int travelRate, int fishingRate) {
      this.id = world;
      this.flag = flag;
      this.eventMessage = eventMessage;
      this.expRate = expRate;
      this.dropRate = dropRate;
      this.bossDropRate = bossDropRate;
      this.mesoRate = mesoRate;
      this.questRate = questRate;
      this.travelRate = travelRate;
      this.fishingRate = fishingRate;
      runningPartyId.set(1000000001);
      runningMessengerId.set(1);

      petUpdate = Server.getInstance().getCurrentTime();
      mountUpdate = petUpdate;

      for (int i = 0; i < 9; i++) {
         cashItemBought.add(new LinkedHashMap<>());
      }

      TimerManager timerManager = TimerManager.getInstance();
      petsSchedule = timerManager.register(new PetFullnessTask(this), 60 * 1000, 60 * 1000);
      srvMessagesSchedule = timerManager.register(new ServerMessageTask(this), 10 * 1000, 10 * 1000);
      mountsSchedule = timerManager.register(new MountTirednessTask(this), 60 * 1000, 60 * 1000);
      merchantSchedule = timerManager.register(new HiredMerchantTask(this), 10 * 60 * 1000, 10 * 60 * 1000);
      timedMapObjectsSchedule = timerManager.register(new TimedMapObjectTask(this), 60 * 1000, 60 * 1000);
      charactersSchedule = timerManager.register(new CharacterAutoSaveTask(this), 60 * 60 * 1000, 60 * 60 * 1000);
      marriagesSchedule = timerManager.register(new WeddingReservationTask(this), YamlConfig.config.server.WEDDING_RESERVATION_INTERVAL * 60 * 1000, YamlConfig.config.server.WEDDING_RESERVATION_INTERVAL * 60 * 1000);
      mapOwnershipSchedule = timerManager.register(new MapOwnershipTask(this), 20 * 1000, 20 * 1000);
      fishingSchedule = timerManager.register(new FishingTask(this), 10 * 1000, 10 * 1000);
      partySearchSchedule = timerManager.register(new PartySearchTask(this), 10 * 1000, 10 * 1000);
      timeoutSchedule = timerManager.register(new TimeoutTask(this), 10 * 1000, 10 * 1000);

      if (YamlConfig.config.server.USE_FAMILY_SYSTEM) {
         long timeLeft = Server.getTimeLeftForNextDay();
         FamilyDailyResetTask.resetEntitlementUsage(this);
         timerManager.register(new FamilyDailyResetTask(this), 24 * 60 * 60 * 1000, timeLeft);
      }
   }

   private static List<Entry<Integer, SortedMap<Integer, MapleCharacter>>> getSortedAccountCharacterView(Map<Integer, SortedMap<Integer, MapleCharacter>> map) {
      List<Entry<Integer, SortedMap<Integer, MapleCharacter>>> list = new ArrayList<>(map.size());
      list.addAll(map.entrySet());

      list.sort(Comparator.comparingInt(Entry::getKey));

      return list;
   }

   private static Integer getPetKey(MapleCharacter chr, byte petSlot) {    // assuming max 3 pets
      return (chr.getId() << 2) + petSlot;
   }

   private static void executePlayerNpcMapDataUpdate(EntityManager entityManager, boolean isPodium, Map<Integer, ?> playerNpcData, int value, int worldId, int mapId) {
      if (playerNpcData.containsKey(mapId)) {
         if (isPodium) {
            PlayerNpcAdministrator.getInstance().setPodium(entityManager, value, worldId, mapId);
         } else {
            PlayerNpcAdministrator.getInstance().setStep(entityManager, value, worldId, mapId);
         }
      } else {
         if (isPodium) {
            PlayerNpcAdministrator.getInstance().addPodium(entityManager, value, worldId, mapId);
         } else {
            PlayerNpcAdministrator.getInstance().addStep(entityManager, value, worldId, mapId);
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

   public boolean addChannel(Channel channel) {
      chnWLock.lock();
      try {
         if (channel.getId() == channels.size() + 1) {
            channels.add(channel);
            return true;
         } else {
            return false;
         }
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
      return eventMessage;
   }

   public int getExpRate() {
      return expRate;
   }

   /**
    * Sets the given rate for all logged in characters.
    *
    * @param rate   the rate to set
    * @param setter a setter to set the appropriate rate
    */
   protected void setGenericRate(int rate, Consumer<Integer> setter) {
      Collection<MapleCharacter> list = getPlayerStorage().getAllCharacters();
      list.parallelStream().filter(MapleCharacter::isLoggedIn).forEach(MapleCharacter::revertWorldRates);
      setter.accept(rate);
      list.parallelStream().filter(MapleCharacter::isLoggedIn).forEach(MapleCharacter::setWorldRates);
   }

   public void setExpRate(int exp) {
      setGenericRate(exp, input -> this.expRate = input);
   }

   public int getDropRate() {
      return dropRate;
   }

   public void setDropRate(int drop) {
      setGenericRate(drop, input -> this.dropRate = input);
   }

   public int getBossDropRate() {
      return bossDropRate;
   }

   public void setBossDropRate(int bossDropRate) {
      this.bossDropRate = bossDropRate;
   }

   public int getMesoRate() {
      return mesoRate;
   }

   public void setMesoRate(int meso) {
      setGenericRate(meso, input -> this.mesoRate = input);
   }

   public int getQuestRate() {
      return questRate;
   }

   public void setQuestRate(int quest) {
      this.questRate = quest;
   }

   public int getTravelRate() {
      return travelRate;
   }

   public void setTravelRate(int travel) {
      this.travelRate = travel;
   }

   public int getTransportationTime(int travelTime) {
      return (int) Math.ceil(travelTime / travelRate);
   }

   public int getFishingRate() {
      return fishingRate;
   }

   public void setFishingRate(int quest) {
      this.fishingRate = quest;
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
         accountStorage.put(accountId, storage);
      } finally {
         accountCharsLock.unlock();
      }
   }

   public void unregisterAccountStorage(Integer accountId) {
      accountCharsLock.lock();
      try {
         accountStorage.remove(accountId);
      } finally {
         accountCharsLock.unlock();
      }
   }

   public MapleStorage getAccountStorage(Integer accountId) {
      return accountStorage.get(accountId);
   }

   public List<MapleCharacter> loadAndGetAllCharactersView() {
      Server.getInstance().loadAllAccountsCharactersView();
      return getAllCharactersView();
   }

   public List<MapleCharacter> getAllCharactersView() {
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
      Channel channel = chr.getClient().getChannelServer();

      if (channel != null) {
         if (!channel.removePlayer(chr)) {
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

      Optional<MapleCharacter> character = mgc.getCharacter();
      if (character.isEmpty()) {
         return Optional.empty();
      }

      Optional<MapleGuild> guild = Server.getInstance().getGuild(mgc.getGuildId(), mgc.getWorld(), character.get());
      if (guild.isEmpty()) {
         return Optional.empty();
      }

      if (!gsStore.containsKey(mgc.getGuildId())) {
         gsStore.put(mgc.getGuildId(), new MapleGuildSummary(guild.get()));
      }
      return guild;
   }

   public boolean isWorldCapacityFull() {
      return getWorldCapacityStatus() == 2;
   }

   public int getWorldCapacityStatus() {
      int worldCap = getChannelsSize() * YamlConfig.config.server.CHANNEL_LOAD;
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
      if (!gsStore.containsKey(gid)) {
         Server.getInstance().getGuild(gid, wid, null).ifPresent(guild -> gsStore.put(gid, new MapleGuildSummary(guild)));
      }
      return gsStore.get(gid);
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

   public void registerCharacterParty(Integer characterId, Integer partyId) {
      partyLock.lock();
      try {
         partyChars.put(characterId, partyId);
      } finally {
         partyLock.unlock();
      }
   }

   public void unregisterCharacterParty(Integer characterId) {
      partyLock.lock();
      try {
         partyChars.remove(characterId);
      } finally {
         partyLock.unlock();
      }
   }

   public Integer getCharacterPartyId(Integer characterId) {
      partyLock.lock();
      try {
         return partyChars.get(characterId);
      } finally {
         partyLock.unlock();
      }
   }

   public MapleParty createParty(MaplePartyCharacter maplePartyCharacter) {
      int partyId = runningPartyId.getAndIncrement();
      MapleParty party = new MapleParty(partyId, id, maplePartyCharacter);

      partyLock.lock();
      try {
         parties.put(party.getId(), party);
         registerCharacterParty(maplePartyCharacter.getId(), partyId);
      } finally {
         partyLock.unlock();
      }

      party.addMember(maplePartyCharacter);
      return party;
   }

   public Optional<MapleParty> getParty(int partyId) {
      partyLock.lock();
      try {
         return Optional.ofNullable(parties.get(partyId));
      } finally {
         partyLock.unlock();
      }
   }

   public MapleParty disbandParty(int partyId) {
      partyLock.lock();
      try {
         return parties.remove(partyId);
      } finally {
         partyLock.unlock();
      }
   }

   public void removeMapPartyMembers(int partyId) {
      Optional<MapleParty> party = getParty(partyId);
      if (party.isEmpty()) {
         return;
      }

      party.map(MapleParty::getMembers).orElse(Collections.emptyList()).stream()
            .map(MaplePartyCharacter::getPlayer)
            .flatMap(Optional::stream)
            .map(AbstractMapleCharacterObject::getMap)
            .filter(Objects::nonNull)
            .forEach(mapleMap -> mapleMap.removeParty(partyId));
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

   public void partyChat(MapleParty party, String message, String from) {
      party.getMembers().parallelStream()
            .filter(partyCharacter -> !partyCharacter.getName().equals(from))
            .map(partyCharacter -> getPlayerStorage().getCharacterByName(partyCharacter.getName()))
            .flatMap(Optional::stream)
            .forEach(character -> PacketCreator.announce(character, new MultiChat(from, message, 1)));
   }

   public void buddyChat(int[] recipientCharacterIds, int cidFrom, String nameFrom, String message) {
      Arrays.stream(recipientCharacterIds)
            .mapToObj(id -> getPlayerStorage().getCharacterById(id))
            .flatMap(Optional::stream)
            .filter(character -> character.getBuddyList().containsVisible(cidFrom))
            .forEach(character -> PacketCreator.announce(character, new MultiChat(nameFrom, message, 0)));
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

   public void messengerInvite(String sender, int messengerId, String targetName, int channelFrom) {
      if (isConnected(targetName)) {
         getPlayerStorage().getCharacterByName(targetName).ifPresent(target -> target.getMessenger()
               .ifPresentOrElse(messenger -> {
                  getChannel(channelFrom).getPlayerStorage().getCharacterByName(sender).ifPresent(from -> PacketCreator.announce(from, new MessengerChat(sender + " : " + target + " is already using Maple Messenger")));
               }, () -> {
                  getChannel(channelFrom).getPlayerStorage().getCharacterByName(sender).ifPresent(from -> {
                     if (MapleInviteCoordinator.createInvite(InviteType.MESSENGER, from, messengerId, target.getId())) {
                        PacketCreator.announce(target, new MessengerInvite(sender, messengerId));
                        PacketCreator.announce(from, new MessengerNote(targetName, 4, 1));
                     } else {
                        PacketCreator.announce(from, new MessengerChat(sender + " : " + target + " is already managing a Maple Messenger invitation"));
                     }
                  });
               }));
      }
   }

   public void addMessengerPlayer(MapleMessenger messenger, String nameFrom, int channelFrom, int position) {
      for (MapleMessengerCharacter messengerCharacter : messenger.getMembers()) {
         getPlayerStorage().getCharacterByName(messengerCharacter.getName()).ifPresent(character -> {
            if (!messengerCharacter.getName().equals(nameFrom)) {
               getChannel(channelFrom).getPlayerStorage().getCharacterByName(nameFrom).ifPresent(from -> {
                  PacketCreator.announce(character, new MessengerAddCharacter(nameFrom, from, position, (byte) (channelFrom - 1)));
                  PacketCreator.announce(from, new MessengerAddCharacter(character.getName(), character, messengerCharacter.getPosition(), (byte) (messengerCharacter.getChannel() - 1)));
               });
            } else {
               PacketCreator.announce(character, new MessengerJoin(messengerCharacter.getPosition()));
            }
         });
      }
   }

   public void removeMessengerPlayer(MapleMessenger messenger, int position) {
      messenger.getMembers().parallelStream()
            .map(messengerCharacter -> getPlayerStorage().getCharacterByName(messengerCharacter.getName()))
            .flatMap(Optional::stream)
            .forEach(character -> PacketCreator.announce(character, new MessengerRemoveCharacter(position)));
   }

   public void messengerChat(MapleMessenger messenger, String message, String nameFrom) {
      messenger.getMembers().parallelStream()
            .filter(messengerCharacter -> !messengerCharacter.getName().equals(nameFrom))
            .map(messengerCharacter -> getPlayerStorage().getCharacterByName(messengerCharacter.getName()))
            .flatMap(Optional::stream)
            .forEach(character -> PacketCreator.announce(character, new MessengerChat(message)));
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

   public void updateMessenger(MapleMessenger messenger, String nameFrom, int position, int channelFrom) {
      messenger.getMembers().parallelStream().forEach(messengerCharacter -> {
         Channel ch = getChannel(channelFrom);
         if (!(messengerCharacter.getName().equals(nameFrom))) {
            ch.getPlayerStorage().getCharacterByName(messengerCharacter.getName())
                  .ifPresent(character -> getChannel(channelFrom).getPlayerStorage().getCharacterByName(nameFrom)
                        .ifPresent(from -> PacketCreator.announce(character, new MessengerUpdateCharacter(nameFrom, from, position, (byte) (channelFrom - 1)))));
         }
      });
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

   public MapleMessenger createMessenger(MapleMessengerCharacter messengerCharacter) {
      int messengerId = runningMessengerId.getAndIncrement();
      MapleMessenger messenger = new MapleMessenger(messengerId, messengerCharacter);
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

   public void addOwlItemSearch(Integer itemId) {
      suggestWLock.lock();
      try {
         owlSearched.merge(itemId, 1, Integer::sum);
      } finally {
         suggestWLock.unlock();
      }
   }

   public List<Pair<Integer, Integer>> getOwlSearchedItems() {
      if (YamlConfig.config.server.USE_ENFORCE_ITEM_SUGGESTION) {
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

   public void addCashItemBought(Integer snId) {
      suggestWLock.lock();
      try {
         Map<Integer, Integer> tabItemBought = cashItemBought.get(snId / 10000000);

         tabItemBought.merge(snId, 1, Integer::sum);
      } finally {
         suggestWLock.unlock();
      }
   }

   private List<List<Pair<Integer, Integer>>> getBoughtCashItems() {
      if (YamlConfig.config.server.USE_ENFORCE_ITEM_SUGGESTION) {
         List<List<Pair<Integer, Integer>>> boughtCounts = new ArrayList<>(9);

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
      List<Integer> tabLeaderboard;

      // descending order
      Comparator<Pair<Integer, Integer>> comparator = (p1, p2) -> p2.getRight().compareTo(p1.getRight());

      PriorityQueue<Pair<Integer, Integer>> queue = new PriorityQueue<>(Math.max(1, tabSellers.size()), comparator);
      queue.addAll(tabSellers);

      tabLeaderboard = new LinkedList<>();
      for (int i = 0; i < Math.min(tabSellers.size(), 5); i++) {
         tabLeaderboard.add(queue.remove().getLeft());
      }

      return tabLeaderboard;
   }

   public List<List<Integer>> getMostSellerCashItems() {
      List<List<Pair<Integer, Integer>>> mostSellers = this.getBoughtCashItems();
      List<List<Integer>> cashLeaderboard = new ArrayList<>(9);
      List<Integer> tabLeaderboard;
      List<Integer> allLeaderboard = null;

      for (List<Pair<Integer, Integer>> tabSellers : mostSellers) {
         if (tabSellers.size() < 5) {
            if (allLeaderboard == null) {
               List<Pair<Integer, Integer>> allSellers = new LinkedList<>();
               for (List<Pair<Integer, Integer>> tabItems : mostSellers) {
                  allSellers.addAll(tabItems);
               }

               allLeaderboard = getMostSellerOnTab(allSellers);
            }

            tabLeaderboard = new LinkedList<>();
            if (allLeaderboard.size() < 5) {
               for (int i : GameConstants.CASH_DATA) {
                  tabLeaderboard.add(i);
               }
            } else {
               tabLeaderboard.addAll(allLeaderboard);
            }
         } else {
            tabLeaderboard = getMostSellerOnTab(tabSellers);
         }

         cashLeaderboard.add(tabLeaderboard);
      }

      return cashLeaderboard;
   }

   public void registerPetHunger(MapleCharacter chr, byte petSlot) {
      if (chr.isGM() && YamlConfig.config.server.GM_PETS_NEVER_HUNGRY || YamlConfig.config.server.PETS_NEVER_HUNGRY) {
         return;
      }

      Integer key = getPetKey(chr, petSlot);

      activePetsLock.lock();
      try {
         int initProc;
         if (Server.getInstance().getCurrentTime() - petUpdate > 55000) {
            initProc = YamlConfig.config.server.PET_EXHAUST_COUNT - 2;
         } else {
            initProc = YamlConfig.config.server.PET_EXHAUST_COUNT - 1;
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
         deployedPets = new HashMap<>(activePets);
      } finally {
         activePetsLock.unlock();
      }

      for (Map.Entry<Integer, Integer> dp : deployedPets.entrySet()) {
         int characterId = dp.getKey() / 4;
         getPlayerStorage().getCharacterById(characterId)
               .filter(MapleCharacter::isLoggedInWorld)
               .ifPresent(character -> {
                  int dpVal = dp.getValue() + 1;
                  if (dpVal == YamlConfig.config.server.PET_EXHAUST_COUNT) {
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

   public void registerMountHunger(int ownerId, boolean isGm) {
      if (isGm && YamlConfig.config.server.GM_PETS_NEVER_HUNGRY || YamlConfig.config.server.PETS_NEVER_HUNGRY) {
         return;
      }

      activeMountsLock.lock();
      try {
         int initProc;
         if (Server.getInstance().getCurrentTime() - mountUpdate > 45000) {
            initProc = YamlConfig.config.server.MOUNT_EXHAUST_COUNT - 2;
         } else {
            initProc = YamlConfig.config.server.MOUNT_EXHAUST_COUNT - 1;
         }

         activeMounts.put(ownerId, initProc);
      } finally {
         activeMountsLock.unlock();
      }
   }

   public void unregisterMountHunger(int ownerId) {
      activeMountsLock.lock();
      try {
         activeMounts.remove(ownerId);
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
               .filter(MapleCharacter::isLoggedInWorld)
               .ifPresent(character -> {
                  int dpVal = dp.getValue() + 1;
                  if (dpVal == YamlConfig.config.server.MOUNT_EXHAUST_COUNT) {
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

   public MaplePlayerShop getPlayerShop(int ownerId) {
      activePlayerShopsLock.lock();
      try {
         return activePlayerShops.get(ownerId);
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

   public MapleHiredMerchant getHiredMerchant(int ownerId) {
      activeMerchantsLock.lock();
      try {
         if (activeMerchants.containsKey(ownerId)) {
            return activeMerchants.get(ownerId).getLeft();
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

         for (Entry<Runnable, Long> entry : registeredTimedMapObjects.entrySet()) {
            if (entry.getValue() <= timeNow) {
               toRemove.add(entry.getKey());
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

   public boolean registerDisabledServerMessage(int characterId) {
      srvMessagesLock.lock();
      try {
         boolean alreadyDisabled = disabledServerMessages.containsKey(characterId);
         disabledServerMessages.put(characterId, 0);

         return alreadyDisabled;
      } finally {
         srvMessagesLock.unlock();
      }
   }

   public boolean unregisterDisabledServerMessage(int characterId) {
      srvMessagesLock.lock();
      try {
         return disabledServerMessages.remove(characterId) != null;
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

         toRemove.forEach(disabledServerMessages::remove);
      } finally {
         srvMessagesLock.unlock();
      }

      if (!toRemove.isEmpty()) {
         toRemove.parallelStream()
               .map(characterId -> players.getCharacterById(characterId))
               .flatMap(Optional::stream)
               .filter(MapleCharacter::isLoggedInWorld)
               .forEach(character -> PacketCreator.announce(character, new ServerMessage(character.getClient().getChannelServer().getServerMessage())));
      }
   }

   public void setPlayerNpcMapStep(int mapId, int step) {
      setPlayerNpcMapData(mapId, step, -1, false);
   }

   public void setPlayerNpcMapPodiumData(int mapId, int podium) {
      setPlayerNpcMapData(mapId, -1, podium, false);
   }

   public void setPlayerNpcMapData(int mapId, int step, int podium) {
      setPlayerNpcMapData(mapId, step, podium, true);
   }

   private void setPlayerNpcMapData(int mapId, int step, int podium, boolean silent) {
      if (!silent) {
         DatabaseConnection.getInstance().withConnection(connection -> {
            if (step != -1) {
               executePlayerNpcMapDataUpdate(connection, false, playerNpcStep, step, id, mapId);
            }

            if (podium != -1) {
               executePlayerNpcMapDataUpdate(connection, true, playerNpcPodium, podium, id, mapId);
            }
         });
      }

      if (step != -1) {
         playerNpcStep.put(mapId, (byte) step);
      }
      if (podium != -1) {
         playerNpcPodium.put(mapId, (short) podium);
      }
   }

   public int getPlayerNpcMapStep(int mapId) {
      try {
         return playerNpcStep.get(mapId);
      } catch (NullPointerException npe) {
         return 0;
      }
   }

   public int getPlayerNpcMapPodiumData(int mapId) {
      try {
         return playerNpcPodium.get(mapId);
      } catch (NullPointerException npe) {
         return 1;
      }
   }

   public void resetPlayerNpcMapData() {
      playerNpcStep.clear();
      playerNpcPodium.clear();
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

   public List<Pair<MaplePlayerShopItem, AbstractMapleMapObject>> getAvailableItemBundles(int itemId) {
      List<Pair<MaplePlayerShopItem, AbstractMapleMapObject>> availableHiredMerchants = new ArrayList<>();

      for (MapleHiredMerchant hiredMerchant : getActiveMerchants()) {
         List<MaplePlayerShopItem> itemBundles = hiredMerchant.sendAvailableBundles(itemId);

         for (MaplePlayerShopItem playerShopItem : itemBundles) {
            availableHiredMerchants.add(new Pair<>(playerShopItem, hiredMerchant));
         }
      }

      for (MaplePlayerShop playerShop : getActivePlayerShops()) {
         List<MaplePlayerShopItem> itemBundles = playerShop.sendAvailableBundles(itemId);

         for (MaplePlayerShopItem playerShopItem : itemBundles) {
            availableHiredMerchants.add(new Pair<>(playerShopItem, playerShop));
         }
      }

      availableHiredMerchants.sort(Comparator.comparingInt(p -> p.getLeft().price()));

      availableHiredMerchants.subList(0, Math.min(availableHiredMerchants.size(), 200));    //truncates the list to have up to 200 elements
      return availableHiredMerchants;
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
      synchronized (fishers) {
         if (fishers.containsKey(chr)) {
            return false;
         }

         fishers.put(chr, baitLevel);
         return true;
      }
   }

   public int unregisterFisherPlayer(MapleCharacter chr) {
      Integer baitLevel = fishers.remove(chr);
      return Objects.requireNonNullElse(baitLevel, 0);
   }

   public void runCheckFishingSchedule() {
      double[] fishingLikelihoods = Fishing.fetchFishingLikelihood();
      double yearLikelihood = fishingLikelihoods[0], timeLikelihood = fishingLikelihoods[1];

      if (!fishers.isEmpty()) {
         List<MapleCharacter> fisherList;

         synchronized (fishers) {
            fisherList = new ArrayList<>(fishers.keySet());
         }

         for (MapleCharacter chr : fisherList) {
            int baitLevel = unregisterFisherPlayer(chr);
            Fishing.doFishing(chr, baitLevel, yearLikelihood, timeLikelihood);
         }
      }
   }

   public void runPartySearchUpdateSchedule() {
      partySearch.updatePartySearchStorage();
      partySearch.runPartySearch();
   }

   public BaseService getServiceAccess(WorldServices sv) {
      return services.getAccess(sv).getService();
   }

   private void closeWorldServices() {
      services.shutdown();
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

      closeWorldServices();
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
