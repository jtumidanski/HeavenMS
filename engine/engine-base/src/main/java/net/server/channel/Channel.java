package net.server.channel;

import java.io.File;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.stream.Collectors;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.buffer.SimpleBufferAllocator;
import org.apache.mina.core.service.IoAcceptor;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.transport.socket.SocketSessionConfig;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;

import client.MapleCharacter;
import client.processor.CharacterProcessor;
import config.YamlConfig;
import constants.game.GameConstants;
import net.MapleServerHandler;
import net.mina.MapleCodecFactory;
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
import net.server.channel.processor.WeddingProcessor;
import net.server.services.BaseService;
import net.server.services.ServicesManager;
import net.server.services.type.ChannelServices;
import net.server.world.MapleParty;
import net.server.world.World;
import scripting.event.EventScriptManager;
import server.TimerManager;
import server.events.gm.MapleEvent;
import server.expeditions.MapleExpedition;
import server.expeditions.MapleExpeditionType;
import server.maps.MapleHiredMerchant;
import server.maps.MapleMap;
import server.maps.MapleMapManager;
import server.maps.MapleMiniDungeon;
import server.maps.MapleMiniDungeonInfo;
import tools.I18nMessage;
import tools.LogType;
import tools.LoggerOriginator;
import tools.LoggerUtil;
import tools.MessageBroadcaster;
import tools.PacketCreator;
import tools.Pair;
import tools.ServerNoticeType;
import tools.packet.message.ServerMessage;

public final class Channel {

   private final Map<Integer, Integer> storedVars = new HashMap<>();
   private int port = 7575;
   private PlayerStorage players = new PlayerStorage();
   private int world, channel;
   private IoAcceptor acceptor;
   private String ip, serverMessage;
   private MapleMapManager mapManager;
   private EventScriptManager eventSM;
   private ServicesManager services;
   private Map<Integer, MapleHiredMerchant> hiredMerchants = new HashMap<>();
   private Set<Integer> playersAway = new HashSet<>();
   private Map<MapleExpeditionType, MapleExpedition> expeditions = new HashMap<>();
   private Map<Integer, MapleMiniDungeon> dungeons = new HashMap<>();
   private List<MapleExpeditionType> expeditionTypes = new ArrayList<>();
   private Set<MapleMap> ownedMaps = Collections.synchronizedSet(Collections.newSetFromMap(new WeakHashMap<>()));
   private MapleEvent event;
   private boolean finishedShutdown = false;
   private int usedDojo = 0;
   private Set<Integer> usedMC = new HashSet<>();

   private int[] dojoStage;
   private long[] dojoFinishTime;
   private ScheduledFuture<?>[] dojoTask;
   private Map<Integer, Integer> dojoParty = new HashMap<>();

   private List<Integer> chapelReservationQueue = new LinkedList<>();
   private List<Integer> cathedralReservationQueue = new LinkedList<>();
   private ScheduledFuture<?> chapelReservationTask;
   private ScheduledFuture<?> cathedralReservationTask;

   private Integer ongoingChapel = null;
   private Boolean ongoingChapelType = null;
   private Set<Integer> ongoingChapelGuests = null;
   private Integer ongoingCathedral = null;
   private Boolean ongoingCathedralType = null;
   private Set<Integer> ongoingCathedralGuests = null;
   private long ongoingStartTime;

   private MonitoredReentrantReadWriteLock merchantLock = new MonitoredReentrantReadWriteLock(MonitoredLockType.MERCHANT, true);
   private MonitoredReadLock merchantReadLock = MonitoredReadLockFactory.createLock(merchantLock);
   private MonitoredWriteLock merchantWriteLock = MonitoredWriteLockFactory.createLock(merchantLock);

   private MonitoredReentrantLock[] faceLock = new MonitoredReentrantLock[YamlConfig.config.server.CHANNEL_LOCKS];

   private MonitoredReentrantLock lock = MonitoredReentrantLockFactory.createLock(MonitoredLockType.CHANNEL, true);

   public Channel(final int world, final int channel, long startTime) {
      this.world = world;
      this.channel = channel;

      this.ongoingStartTime = startTime + 10000;  // rude approach to a world's last channel boot time, placeholder for the 1st wedding reservation ever
      this.mapManager = new MapleMapManager(null, world, channel);
      try {
         port = 7575 + this.channel - 1;
         port += (world * 100);
         ip = YamlConfig.config.server.HOST + ":" + port;
         IoBuffer.setUseDirectBuffer(false);
         IoBuffer.setAllocator(new SimpleBufferAllocator());
         acceptor = new NioSocketAcceptor();
         acceptor.setHandler(new MapleServerHandler(world, channel));
         acceptor.getSessionConfig().setIdleTime(IdleStatus.BOTH_IDLE, 30);
         acceptor.getFilterChain().addLast("codec", new ProtocolCodecFilter(new MapleCodecFactory()));
         acceptor.bind(new InetSocketAddress(port));
         ((SocketSessionConfig) acceptor.getSessionConfig()).setTcpNoDelay(true);
         expeditionTypes.addAll(Arrays.asList(MapleExpeditionType.values()));

         if (Server.getInstance().isOnline()) {
            eventSM = new EventScriptManager(this, getEvents());
            eventSM.init();
         } else {
            String[] ev = {"0_EXAMPLE"};
            eventSM = new EventScriptManager(this, ev);
         }

         dojoStage = new int[20];
         dojoFinishTime = new long[20];
         dojoTask = new ScheduledFuture<?>[20];
         for (int i = 0; i < 20; i++) {
            dojoStage[i] = 0;
            dojoFinishTime[i] = 0;
            dojoTask[i] = null;
         }

         services = new ServicesManager(ChannelServices.OVERALL);
         LoggerUtil.printInfo(LoggerOriginator.ENGINE, LogType.PROCESS, "    Channel " + getId() + ": Listening on port " + port);
      } catch (Exception e) {
         LoggerUtil.printError(LoggerOriginator.ENGINE, LogType.EXCEPTION, e);
      }
   }

   private static String[] getEvents() {
      List<String> events = new ArrayList<>();
      for (File file : new File("script/src/main/groovy/event").listFiles()) {
         events.add(file.getName().substring(0, file.getName().length() - 7));
      }
      return events.toArray(new String[0]);
   }

   private static int getDojoSlot(int dojoMapId) {
      return (dojoMapId % 100) + ((dojoMapId / 10000 == 92502) ? 5 : 0);
   }

   private static String getTimeLeft(long futureTime) {
      StringBuilder str = new StringBuilder();
      long leftTime = futureTime - System.currentTimeMillis();

      if (leftTime < 0) {
         return null;
      }

      byte mode = 0;
      if (leftTime / (60 * 1000) > 0) {
         mode++;     //counts minutes

         if (leftTime / (60 * 60 * 1000) > 0) {
            mode++;     //counts hours
         }
      }

      switch (mode) {
         case 2:
            int hours = (int) ((leftTime / (1000 * 60 * 60)));
            str.append(hours).append(" hours, ");

         case 1:
            int minutes = (int) ((leftTime / (1000 * 60)) % 60);
            str.append(minutes).append(" minutes, ");

         default:
            int seconds = (int) (leftTime / 1000) % 60;
            str.append(seconds).append(" seconds");
      }

      return str.toString();
   }

   private static int getMonsterCarnivalRoom(boolean cpq1, int field) {
      return (cpq1 ? 0 : 100) + field;
   }

   public synchronized void reloadEventScriptManager() {
      if (finishedShutdown) {
         return;
      }

      eventSM.cancel();
      eventSM = null;
      eventSM = new EventScriptManager(this, getEvents());
   }

   public final synchronized void shutdown() {
      try {
         if (finishedShutdown) {
            return;
         }

         LoggerUtil.printInfo(LoggerOriginator.ENGINE, LogType.PROCESS, "Shutting down Channel " + channel + " on World " + world);

         closeAllMerchants();
         disconnectAwayPlayers();
         players.disconnectAll();

         eventSM.dispose();
         eventSM = null;

         mapManager.dispose();
         mapManager = null;

         closeChannelSchedules();
         players = null;

         MapleServerHandler handler = (MapleServerHandler) acceptor.getHandler();
         handler.dispose();
         acceptor.unbind();

         finishedShutdown = true;
         LoggerUtil.printInfo(LoggerOriginator.ENGINE, LogType.PROCESS, "Successfully shut down Channel " + channel + " on World " + world + "\r\n");
      } catch (Exception e) {
         LoggerUtil.printError(LoggerOriginator.ENGINE, LogType.PROCESS, e, "Error while shutting down Channel " + channel + " on World " + world + "\r\n" + e);
      }
   }

   private void closeChannelServices() {
      services.shutdown();
   }

   private void closeChannelSchedules() {
      lock.lock();
      try {
         for (int i = 0; i < dojoTask.length; i++) {
            if (dojoTask[i] != null) {
               dojoTask[i].cancel(false);
               dojoTask[i] = null;
            }
         }
      } finally {
         lock.unlock();
      }

      closeChannelServices();
      disposeLocks();
   }

   private void disposeLocks() {
      LockCollector.getInstance().registerDisposeAction(this::emptyLocks);
   }

   private void emptyLocks() {
      for (int i = 0; i < YamlConfig.config.server.CHANNEL_LOCKS; i++) {
         faceLock[i] = faceLock[i].dispose();
      }

      lock = lock.dispose();
   }

   private void closeAllMerchants() {
      try {
         List<MapleHiredMerchant> hiredMerchants;

         merchantWriteLock.lock();
         try {
            hiredMerchants = new ArrayList<>(this.hiredMerchants.values());
            this.hiredMerchants.clear();
         } finally {
            merchantWriteLock.unlock();
         }
         hiredMerchants.forEach(MapleHiredMerchant::forceClose);
      } catch (Exception e) {
         e.printStackTrace();
      }
   }

   public MapleMapManager getMapFactory() {
      return mapManager;
   }

   public BaseService getServiceAccess(ChannelServices serviceType) {
      return services.getAccess(serviceType).getService();
   }

   public int getWorld() {
      return world;
   }

   public World getWorldServer() {
      return Server.getInstance().getWorld(world);
   }

   public void addPlayer(MapleCharacter chr) {
      players.addPlayer(chr);
      PacketCreator.announce(chr, new ServerMessage(serverMessage));
   }

   public String getServerMessage() {
      return serverMessage;
   }

   public void setServerMessage(String message) {
      this.serverMessage = message;
      broadcastPacket(PacketCreator.create(new ServerMessage(message)));
      getWorldServer().resetDisabledServerMessages();
   }

   public PlayerStorage getPlayerStorage() {
      return players;
   }

   public boolean removePlayer(MapleCharacter chr) {
      return players.removePlayer(chr.getId()) != null;
   }

   public int getChannelCapacity() {
      return (int) (Math.ceil(((float) players.getAllCharacters().size() / YamlConfig.config.server.CHANNEL_LOAD) * 800));
   }

   public void broadcastPacket(final byte[] data) {
      for (MapleCharacter chr : players.getAllCharacters()) {
         chr.announce(data);
      }
   }

   public final int getId() {
      return channel;
   }

   public String getIP() {
      return ip;
   }

   public MapleEvent getEvent() {
      return event;
   }

   public void setEvent(MapleEvent event) {
      this.event = event;
   }

   public EventScriptManager getEventSM() {
      return eventSM;
   }

   public void broadcastGMPacket(final byte[] data) {
      for (MapleCharacter chr : players.getAllCharacters()) {
         if (chr.isGM()) {
            chr.announce(data);
         }
      }
   }

   public List<MapleCharacter> getPartyMembers(MapleParty party) {
      return party.getMembers().stream()
            .filter(member -> member.getChannel() == getId())
            .map(member -> getPlayerStorage().getCharacterByName(member.getName()))
            .flatMap(Optional::stream)
            .collect(Collectors.toList());
   }

   public void insertPlayerAway(int chrId) {   // either they in CS or MTS
      playersAway.add(chrId);
   }

   public void removePlayerAway(int chrId) {
      playersAway.remove(chrId);
   }

   public boolean canUninstall() {
      return players.getSize() == 0 && playersAway.isEmpty();
   }

   private void disconnectAwayPlayers() {
      playersAway.stream()
            .map(id -> getWorldServer().getPlayerStorage().getCharacterById(id))
            .flatMap(Optional::stream)
            .filter(MapleCharacter::isLoggedIn)
            .forEach(character -> character.getClient().forceDisconnect());
   }

   public Map<Integer, MapleHiredMerchant> getHiredMerchants() {
      merchantReadLock.lock();
      try {
         return Collections.unmodifiableMap(hiredMerchants);
      } finally {
         merchantReadLock.unlock();
      }
   }

   public void addHiredMerchant(int characterId, MapleHiredMerchant hm) {
      merchantWriteLock.lock();
      try {
         hiredMerchants.put(characterId, hm);
      } finally {
         merchantWriteLock.unlock();
      }
   }

   public void removeHiredMerchant(int characterId) {
      merchantWriteLock.lock();
      try {
         hiredMerchants.remove(characterId);
      } finally {
         merchantWriteLock.unlock();
      }
   }

   public boolean addExpedition(MapleExpedition expedition) {
      synchronized (expeditions) {
         if (expeditions.containsKey(expedition.getType())) {
            return false;
         }

         expeditions.put(expedition.getType(), expedition);
         expedition.beginRegistration();
         return true;
      }
   }

   public void removeExpedition(MapleExpedition expedition) {
      synchronized (expeditions) {
         expeditions.remove(expedition.getType());
      }
   }

   public MapleExpedition getExpedition(MapleExpeditionType type) {
      return expeditions.get(type);
   }

   public List<MapleExpedition> getExpeditions() {
      synchronized (expeditions) {
         return new ArrayList<>(expeditions.values());
      }
   }

   public boolean isConnected(String name) {
      return getPlayerStorage().getCharacterByName(name).isPresent();
   }

   public boolean isActive() {
      EventScriptManager esm = this.getEventSM();
      return esm != null && esm.isActive();
   }

   public boolean finishedShutdown() {
      return finishedShutdown;
   }

   public int getStoredVar(int key) {
      if (storedVars.containsKey(key)) {
         return storedVars.get(key);
      }

      return 0;
   }

   public void setStoredVar(int key, int val) {
      this.storedVars.put(key, val);
   }

   public int lookupPartyDojo(MapleParty party) {
      if (party == null) {
         return -1;
      }

      Integer i = dojoParty.get(party.hashCode());
      return (i != null) ? i : -1;
   }

   public int ingressDojo(boolean isPartyDojo, int fromStage) {
      return ingressDojo(isPartyDojo, null, fromStage);
   }

   public int ingressDojo(boolean isPartyDojo, MapleParty party, int fromStage) {
      lock.lock();
      try {
         int dojoList = this.usedDojo;
         int range, slot = 0;

         if (!isPartyDojo) {
            dojoList = dojoList >> 5;
            range = 15;
         } else {
            range = 5;
         }

         while ((dojoList & 1) != 0) {
            dojoList = (dojoList >> 1);
            slot++;
         }

         if (slot < range) {
            int slotMapId = (isPartyDojo ? 925030000 : 925020000) + (100 * (fromStage + 1)) + slot;
            int dojoSlot = getDojoSlot(slotMapId);
            if (party != null) {
               if (dojoParty.containsKey(party.hashCode())) {
                  return -2;
               }
               dojoParty.put(party.hashCode(), dojoSlot);
            }

            this.usedDojo |= (1 << dojoSlot);

            this.resetDojo(slotMapId);
            this.startDojoSchedule(slotMapId);
            return slot;
         } else {
            return -1;
         }
      } finally {
         lock.unlock();
      }
   }

   private void freeDojoSlot(int slot, MapleParty party) {
      int mask = 0b11111111111111111111;
      mask ^= (1 << slot);

      lock.lock();
      try {
         usedDojo &= mask;
      } finally {
         lock.unlock();
      }

      if (party != null) {
         if (dojoParty.remove(party.hashCode()) != null) {
            return;
         }
      }

      if (dojoParty.containsValue(slot)) {    // strange case, no party there!
         Set<Entry<Integer, Integer>> es = new HashSet<>(dojoParty.entrySet());

         for (Entry<Integer, Integer> e : es) {
            if (e.getValue() == slot) {
               dojoParty.remove(e.getKey());
               break;
            }
         }
      }
   }

   public void resetDojoMap(int fromMapId) {
      for (int i = 0; i < (((fromMapId / 100) % 100 <= 36) ? 5 : 2); i++) {
         this.getMapFactory().getMap(fromMapId + (100 * i)).resetMapObjects();
      }
   }

   public void resetDojo(int dojoMapId) {
      resetDojo(dojoMapId, -1);
   }

   private void resetDojo(int dojoMapId, int thisStg) {
      int slot = getDojoSlot(dojoMapId);
      this.dojoStage[slot] = thisStg;
   }

   public void freeDojoSectionIfEmpty(int dojoMapId) {
      final int slot = getDojoSlot(dojoMapId);
      final int delta = (dojoMapId) % 100;
      final int stage = (dojoMapId / 100) % 100;
      final int dojoBaseMap = (dojoMapId >= 925030000) ? 925030000 : 925020000;

      for (int i = 0; i < 5; i++) { //only 32 stages, but 38 maps
         if (stage + i > 38) {
            break;
         }
         MapleMap dojoMap = getMapFactory().getMap(dojoBaseMap + (100 * (stage + i)) + delta);
         if (!dojoMap.getAllPlayers().isEmpty()) {
            return;
         }
      }

      freeDojoSlot(slot, null);
   }

   private void startDojoSchedule(final int dojoMapId) {
      final int slot = getDojoSlot(dojoMapId);
      final int stage = (dojoMapId / 100) % 100;
      if (stage <= dojoStage[slot]) {
         return;
      }

      long clockTime = (stage > 36 ? 15 : (stage / 6) + 5) * 60000;
      lock.lock();
      try {
         if (this.dojoTask[slot] != null) {
            this.dojoTask[slot].cancel(false);
         }
         this.dojoTask[slot] = TimerManager.getInstance().schedule(() -> {
            final int delta = (dojoMapId) % 100;
            final int dojoBaseMap = (slot < 5) ? 925030000 : 925020000;
            Optional<MapleParty> party = Optional.empty();

            for (int i = 0; i < 5; i++) { //only 32 stages, but 38 maps
               if (stage + i > 38) {
                  break;
               }

               MapleMap dojoExit = getMapFactory().getMap(925020002);
               for (MapleCharacter chr : getMapFactory().getMap(dojoBaseMap + (100 * (stage + i)) + delta).getAllPlayers()) {
                  if (GameConstants.isDojo(chr.getMap().getId())) {
                     chr.changeMap(dojoExit);
                  }
                  party = chr.getParty();
               }
            }
            party.ifPresent(party1 -> freeDojoSlot(slot, party1));
         }, clockTime + 3000);   // let the TIMES UP display for 3 seconds, then warp
      } finally {
         lock.unlock();
      }

      dojoFinishTime[slot] = Server.getInstance().getCurrentTime() + clockTime;
   }

   public void dismissDojoSchedule(int dojoMapId, MapleParty party) {
      int slot = getDojoSlot(dojoMapId);
      int stage = (dojoMapId / 100) % 100;
      if (stage <= dojoStage[slot]) {
         return;
      }

      lock.lock();
      try {
         if (this.dojoTask[slot] != null) {
            this.dojoTask[slot].cancel(false);
            this.dojoTask[slot] = null;
         }
      } finally {
         lock.unlock();
      }

      freeDojoSlot(slot, party);
   }

   public boolean setDojoProgress(int dojoMapId) {
      int slot = getDojoSlot(dojoMapId);
      int dojoStg = (dojoMapId / 100) % 100;

      if (this.dojoStage[slot] < dojoStg) {
         this.dojoStage[slot] = dojoStg;
         return true;
      } else {
         return false;
      }
   }

   public long getDojoFinishTime(int dojoMapId) {
      return dojoFinishTime[getDojoSlot(dojoMapId)];
   }

   public boolean addMiniDungeon(int dungeonId) {
      lock.lock();
      try {
         if (dungeons.containsKey(dungeonId)) {
            return false;
         }

         MapleMiniDungeonInfo miniDungeonInfo = MapleMiniDungeonInfo.getDungeon(dungeonId);
         MapleMiniDungeon mmd = new MapleMiniDungeon(miniDungeonInfo.getBase(), this.getMapFactory().getMap(miniDungeonInfo.getDungeonId()).getTimeLimit());

         dungeons.put(dungeonId, mmd);
         return true;
      } finally {
         lock.unlock();
      }
   }

   public MapleMiniDungeon getMiniDungeon(int dungeonId) {
      lock.lock();
      try {
         return dungeons.get(dungeonId);
      } finally {
         lock.unlock();
      }
   }

   public void removeMiniDungeon(int dungeonId) {
      lock.lock();
      try {
         dungeons.remove(dungeonId);
      } finally {
         lock.unlock();
      }
   }

   public Pair<Boolean, Pair<Integer, Set<Integer>>> getNextWeddingReservation(boolean cathedral) {
      Integer ret;

      lock.lock();
      try {
         List<Integer> weddingReservationQueue = (cathedral ? cathedralReservationQueue : chapelReservationQueue);
         if (weddingReservationQueue.isEmpty()) {
            return null;
         }

         ret = weddingReservationQueue.remove(0);
         if (ret == null) {
            return null;
         }
      } finally {
         lock.unlock();
      }

      World world = getWorldServer();

      Pair<Integer, Integer> coupleId = world.getMarriageQueuedCouple(ret);
      Pair<Boolean, Set<Integer>> typeGuests = world.removeMarriageQueued(ret);

      Pair<String, String> couple = new Pair<>(CharacterProcessor.getInstance().getNameById(coupleId.getLeft()), CharacterProcessor.getInstance().getNameById(coupleId.getRight()));
      MessageBroadcaster.getInstance().sendWorldServerNotice(this.world, ServerNoticeType.LIGHT_BLUE, I18nMessage.from("MARRIAGE_WEDDING_START").with(couple.getLeft(), couple.getRight(), (cathedral ? "Cathedral" : "Chapel"), channel));

      return new Pair<>(typeGuests.getLeft(), new Pair<>(ret, typeGuests.getRight()));
   }

   public boolean isWeddingReserved(Integer weddingId) {
      World world = getWorldServer();

      lock.lock();
      try {
         return world.isMarriageQueued(weddingId) || weddingId.equals(ongoingCathedral) || weddingId.equals(ongoingChapel);
      } finally {
         lock.unlock();
      }
   }

   public int getWeddingReservationStatus(Integer weddingId, boolean cathedral) {
      if (weddingId == null) {
         return -1;
      }

      lock.lock();
      try {
         if (cathedral) {
            if (weddingId.equals(ongoingCathedral)) {
               return 0;
            }

            for (int i = 0; i < cathedralReservationQueue.size(); i++) {
               if (weddingId.equals(cathedralReservationQueue.get(i))) {
                  return i + 1;
               }
            }
         } else {
            if (weddingId.equals(ongoingChapel)) {
               return 0;
            }

            for (int i = 0; i < chapelReservationQueue.size(); i++) {
               if (weddingId.equals(chapelReservationQueue.get(i))) {
                  return i + 1;
               }
            }
         }

         return -1;
      } finally {
         lock.unlock();
      }
   }

   public int pushWeddingReservation(Integer weddingId, boolean cathedral, boolean premium, Integer groomId, Integer brideId) {
      if (weddingId == null || isWeddingReserved(weddingId)) {
         return -1;
      }

      World world = getWorldServer();
      world.putMarriageQueued(weddingId, cathedral, premium, groomId, brideId);

      lock.lock();
      try {
         List<Integer> weddingReservationQueue = (cathedral ? cathedralReservationQueue : chapelReservationQueue);

         int delay = YamlConfig.config.server.WEDDING_RESERVATION_DELAY - 1 - weddingReservationQueue.size();
         for (int i = 0; i < delay; i++) {
            weddingReservationQueue.add(null);  // push empty slots to fill the waiting time
         }

         weddingReservationQueue.add(weddingId);
         return weddingReservationQueue.size();
      } finally {
         lock.unlock();
      }
   }

   public boolean isOngoingWeddingGuest(boolean cathedral, int playerId) {
      lock.lock();
      try {
         if (cathedral) {
            return ongoingCathedralGuests != null && ongoingCathedralGuests.contains(playerId);
         } else {
            return ongoingChapelGuests != null && ongoingChapelGuests.contains(playerId);
         }
      } finally {
         lock.unlock();
      }
   }

   public Integer getOngoingWedding(boolean cathedral) {
      lock.lock();
      try {
         return cathedral ? ongoingCathedral : ongoingChapel;
      } finally {
         lock.unlock();
      }
   }

   public boolean getOngoingWeddingType(boolean cathedral) {
      lock.lock();
      try {
         return cathedral ? ongoingCathedralType : ongoingChapelType;
      } finally {
         lock.unlock();
      }
   }

   public void closeOngoingWedding(boolean cathedral) {
      lock.lock();
      try {
         if (cathedral) {
            ongoingCathedral = null;
            ongoingCathedralType = null;
            ongoingCathedralGuests = null;
         } else {
            ongoingChapel = null;
            ongoingChapelType = null;
            ongoingChapelGuests = null;
         }
      } finally {
         lock.unlock();
      }
   }

   public void setOngoingWedding(final boolean cathedral, Boolean premium, Integer weddingId, Set<Integer> guests) {
      lock.lock();
      try {
         if (cathedral) {
            ongoingCathedral = weddingId;
            ongoingCathedralType = premium;
            ongoingCathedralGuests = guests;
         } else {
            ongoingChapel = weddingId;
            ongoingChapelType = premium;
            ongoingChapelGuests = guests;
         }
      } finally {
         lock.unlock();
      }

      ongoingStartTime = System.currentTimeMillis();
      if (weddingId != null) {
         ScheduledFuture<?> weddingTask = TimerManager.getInstance().schedule(() -> closeOngoingWedding(cathedral), YamlConfig.config.server.WEDDING_RESERVATION_TIMEOUT * 60 * 1000);

         if (cathedral) {
            cathedralReservationTask = weddingTask;
         } else {
            chapelReservationTask = weddingTask;
         }
      }
   }

   public synchronized boolean acceptOngoingWedding(final boolean cathedral) {     // couple succeeded to show up and started the ceremony
      if (cathedral) {
         if (cathedralReservationTask == null) {
            return false;
         }

         cathedralReservationTask.cancel(false);
         cathedralReservationTask = null;
      } else {
         if (chapelReservationTask == null) {
            return false;
         }

         chapelReservationTask.cancel(false);
         chapelReservationTask = null;
      }

      return true;
   }

   public long getWeddingTicketExpireTime(int resSlot) {
      return ongoingStartTime + WeddingProcessor.getInstance().getRelativeWeddingTicketExpireTime(resSlot);
   }

   public String getWeddingReservationTimeLeft(Integer weddingId) {
      if (weddingId == null) {
         return null;
      }

      lock.lock();
      try {
         boolean cathedral = true;

         int resStatus;
         resStatus = getWeddingReservationStatus(weddingId, true);
         if (resStatus < 0) {
            cathedral = false;
            resStatus = getWeddingReservationStatus(weddingId, false);

            if (resStatus < 0) {
               return null;
            }
         }

         String venue = (cathedral ? "Cathedral" : "Chapel");
         if (resStatus == 0) {
            return venue + " - RIGHT NOW";
         }

         return venue + " - " + getTimeLeft(ongoingStartTime + (resStatus * YamlConfig.config.server.WEDDING_RESERVATION_INTERVAL * 60 * 1000)) + " from now";
      } finally {
         lock.unlock();
      }
   }

   public Pair<Integer, Integer> getWeddingCoupleForGuest(int guestId, boolean cathedral) {
      lock.lock();
      try {
         return (isOngoingWeddingGuest(cathedral, guestId)) ? getWorldServer().getRelationshipCouple(getOngoingWedding(cathedral)) : null;
      } finally {
         lock.unlock();
      }
   }

   public void registerOwnedMap(MapleMap map) {
      ownedMaps.add(map);
   }

   public void unregisterOwnedMap(MapleMap map) {
      ownedMaps.remove(map);
   }

   public void runCheckOwnedMapsSchedule() {
      if (!ownedMaps.isEmpty()) {
         List<MapleMap> ownedMapsList;

         synchronized (ownedMaps) {
            ownedMapsList = new ArrayList<>(ownedMaps);
         }

         for (MapleMap map : ownedMapsList) {
            map.checkMapOwnerActivity();
         }
      }
   }

   public void initMonsterCarnival(boolean cpq1, int field) {
      usedMC.add(getMonsterCarnivalRoom(cpq1, field));
   }

   public void finishMonsterCarnival(boolean cpq1, int field) {
      usedMC.remove(getMonsterCarnivalRoom(cpq1, field));
   }

   public boolean canInitMonsterCarnival(boolean cpq1, int field) {
      return !usedMC.contains(getMonsterCarnivalRoom(cpq1, field));
   }

   public void debugMarriageStatus() {
      System.out.println(" ----- WORLD DATA -----");
      getWorldServer().debugMarriageStatus();
      System.out.println(" ----- CH. " + channel + " -----");
      printVenueStatus(" ----- CATHEDRAL -----", cathedralReservationQueue, cathedralReservationTask, ongoingCathedral, ongoingCathedralType, ongoingCathedralGuests);
      printVenueStatus(" ----- CHAPEL -----", chapelReservationQueue, chapelReservationTask, ongoingChapel, ongoingChapelType, ongoingChapelGuests);
      System.out.println("Start Time: " + ongoingStartTime);
   }

   private void printVenueStatus(String venueName, List<Integer> reservationQueue, ScheduledFuture<?> reservationTask, Integer ongoing, Boolean ongoingType, Set<Integer> ongoingGuests) {
      System.out.println(venueName);
      System.out.println("Current Queue: " + reservationQueue);
      System.out.println("Cancel Task: " + (reservationTask != null));
      System.out.println("Ongoing wid: " + ongoing);
      System.out.println();
      System.out.println("Ongoing wid: " + ongoing + " isPremium: " + ongoingType);
      System.out.println("Guest list: " + ongoingGuests);
      System.out.println();
   }
}