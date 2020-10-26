package scripting.event;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import javax.script.Invocable;
import javax.script.ScriptException;

import client.MapleCharacter;
import config.YamlConfig;
import constants.game.GameConstants;
import jdk.nashorn.api.scripting.ScriptUtils;
import net.server.Server;
import net.server.audit.LockCollector;
import net.server.audit.locks.MonitoredLockType;
import net.server.audit.locks.MonitoredReentrantLock;
import net.server.audit.locks.factory.MonitoredReentrantLockFactory;
import net.server.channel.Channel;
import net.server.world.MapleParty;
import net.server.world.MaplePartyCharacter;
import net.server.world.World;
import scripting.event.scheduler.EventScriptScheduler;
import server.MapleMarriage;
import server.ThreadManager;
import server.expeditions.MapleExpedition;
import server.life.MapleLifeFactory;
import server.life.MapleMonster;
import server.maps.MapleMap;
import server.processor.QuestProcessor;
import tools.I18nMessage;
import tools.LogType;
import tools.LoggerOriginator;
import tools.LoggerUtil;
import tools.MessageBroadcaster;
import tools.ServerNoticeType;
import tools.exceptions.EventInstanceInProgressException;

public class EventManager {
   private static final int maxLobbies = 8;     // an event manager holds up to this amount of concurrent lobbies
   private final Queue<Integer> queuedGuilds = new LinkedList<>();
   private final Map<Integer, Integer> queuedGuildLeaders = new HashMap<>();
   private Invocable iv;
   private Channel channel;
   private World world;
   private Server server;
   private EventScriptScheduler ess = new EventScriptScheduler();
   private Map<String, EventInstanceManager> instances = new HashMap<>();
   private Map<String, Integer> instanceLocks = new HashMap<>();
   private List<Boolean> openedLobbies;
   private List<EventInstanceManager> readyInstances = new LinkedList<>();
   private Integer readyId = 0, onLoadInstances = 0;
   private Properties props = new Properties();
   private String name;
   private MonitoredReentrantLock lobbyLock = MonitoredReentrantLockFactory.createLock(MonitoredLockType.EM_LOBBY);
   private MonitoredReentrantLock queueLock = MonitoredReentrantLockFactory.createLock(MonitoredLockType.EM_QUEUE);
   private MonitoredReentrantLock startLock = MonitoredReentrantLockFactory.createLock(MonitoredLockType.EM_START);
   private Set<Integer> playerPermit = new HashSet<>();
   private Semaphore startSemaphore = new Semaphore(7);

   public EventManager(Channel channel, Invocable iv, String name) {
      this.server = Server.getInstance();
      this.iv = iv;
      this.channel = channel;
      this.world = server.getWorld(channel.getWorld());
      this.name = name;

      this.openedLobbies = new ArrayList<>();
      for (int i = 0; i < maxLobbies; i++) {
         this.openedLobbies.add(false);
      }
   }

   private boolean isDisposed() {
      return onLoadInstances <= -1000;
   }

   public void cancel() {  // make sure to only call this when there are NO PLAYERS ONLINE to mess around with the event manager!
      ess.dispose();

      try {
         iv.invokeFunction("cancelSchedule", (Object) null);
      } catch (ScriptException | NoSuchMethodException ex) {
         ex.printStackTrace();
      }

      Collection<EventInstanceManager> eimList;
      synchronized (instances) {
         eimList = getInstances();
         instances.clear();
      }

      for (EventInstanceManager eim : eimList) {
         eim.dispose(true);
      }

      List<EventInstanceManager> eventInstanceManagers;
      queueLock.lock();
      try {
         eventInstanceManagers = new ArrayList<>(readyInstances);
         readyInstances.clear();
         onLoadInstances = Integer.MIN_VALUE / 2;
      } finally {
         queueLock.unlock();
      }

      for (EventInstanceManager eim : eventInstanceManagers) {
         eim.dispose(true);
      }

      props.clear();
      channel = null;
      world = null;
      server = null;
      iv = null;

      disposeLocks();
   }

   private void disposeLocks() {
      LockCollector.getInstance().registerDisposeAction(this::emptyLocks);
   }

   private void emptyLocks() {
      lobbyLock = lobbyLock.dispose();
      queueLock = queueLock.dispose();
      startLock = startLock.dispose();
   }

   private List<Integer> convertToIntegerArray(List<Object> list) {
      List<Integer> intList = new ArrayList<>();

      for (Object d : list) {
         intList.add((Integer) d);
      }

      return intList;
   }

   public long getLobbyDelay() {
      return YamlConfig.config.server.EVENT_LOBBY_DELAY;
   }

   private List<Integer> getLobbyRange() {
      try {
         return convertToIntegerArray((List<Object>) iv.invokeFunction("setLobbyRange", (Object) null));
      } catch (ScriptException | NoSuchMethodException ex) { // they didn't define a lobby range
         List<Integer> defaultRange = new ArrayList<>();
         defaultRange.add(0);
         defaultRange.add(maxLobbies);

         return defaultRange;
      }
   }

   public EventScheduledFuture schedule(String methodName, long delay) {
      return schedule(methodName, null, delay);
   }

   public EventScheduledFuture schedule(final String methodName, final EventInstanceManager eim, long delay) {
      Runnable r = () -> {
         try {
            iv.invokeFunction(methodName, eim);
         } catch (ScriptException | NoSuchMethodException ex) {
            LoggerUtil.printError(LoggerOriginator.ENGINE, LogType.EXCEPTION, ex);
         }
      };

      ess.registerEntry(r, delay);

      // hate to do that, but those schedules can still be cancelled, so well... Let GC do it's job
      return new EventScheduledFuture(r, ess);
   }

   public EventScheduledFuture scheduleAtTimestamp(final String methodName, long timestamp) {
      Runnable r = () -> {
         try {
            iv.invokeFunction(methodName, (Object) null);
         } catch (ScriptException | NoSuchMethodException ex) {
            LoggerUtil.printError(LoggerOriginator.ENGINE, LogType.EXCEPTION, ex);
         }
      };

      ess.registerEntry(r, timestamp - server.getCurrentTime());
      return new EventScheduledFuture(r, ess);
   }

   public World getWorldServer() {
      return world;
   }

   public Channel getChannelServer() {
      return channel;
   }

   public Invocable getIv() {
      return iv;
   }

   public EventInstanceManager getInstance(String name) {
      return instances.get(name);
   }

   public Collection<EventInstanceManager> getInstances() {
      synchronized (instances) {
         return new LinkedList<>(instances.values());
      }
   }

   public EventInstanceManager newInstance(String name) throws EventInstanceInProgressException {
      EventInstanceManager ret = getReadyInstance();

      if (ret == null) {
         ret = new EventInstanceManager(this, name);
      } else {
         ret.setName(name);
      }

      synchronized (instances) {
         if (instances.containsKey(name)) {
            throw new EventInstanceInProgressException(name, this.getName());
         }

         instances.put(name, ret);
      }
      return ret;
   }

   public MapleMarriage newMarriage(String name) throws EventInstanceInProgressException {
      MapleMarriage ret = new MapleMarriage(this, name);

      synchronized (instances) {
         if (instances.containsKey(name)) {
            throw new EventInstanceInProgressException(name, this.getName());
         }

         instances.put(name, ret);
      }
      return ret;
   }

   public void disposeInstance(final String name) {
      ess.registerEntry(() -> {
         freeLobbyInstance(name);

         synchronized (instances) {
            instances.remove(name);
         }
      }, YamlConfig.config.server.EVENT_LOBBY_DELAY * 1000);
   }

   public void setProperty(String key, String value) {
      props.setProperty(key, value);
   }

   public void setIntProperty(String key, int value) {
      setProperty(key, value);
   }

   public void setProperty(String key, int value) {
      props.setProperty(key, value + "");
   }

   public String getProperty(String key) {
      return props.getProperty(key);
   }

   public int getIntProperty(String key) {
      return Integer.parseInt(props.getProperty(key));
   }

   private void setLockLobby(int lobbyId, boolean lock) {
      lobbyLock.lock();
      try {
         openedLobbies.set(lobbyId, lock);
      } finally {
         lobbyLock.unlock();
      }
   }

   private boolean startLobbyInstance(int lobbyId) {
      lobbyLock.lock();
      try {
         if (lobbyId < 0) {
            lobbyId = 0;
         } else if (lobbyId >= maxLobbies) {
            lobbyId = maxLobbies - 1;
         }

         if (!openedLobbies.get(lobbyId)) {
            openedLobbies.set(lobbyId, true);
            return true;
         }

         return false;
      } finally {
         lobbyLock.unlock();
      }
   }

   private void freeLobbyInstance(String lobbyName) {
      Integer i = instanceLocks.get(lobbyName);
      if (i == null) {
         return;
      }

      instanceLocks.remove(lobbyName);
      if (i > -1) {
         setLockLobby(i, false);
      }
   }

   public String getName() {
      return name;
   }

   private int availableLobbyInstance() {
      List<Integer> lr = getLobbyRange();
      int lb = 0, hb = 0;

      if (lr.size() >= 2) {
         lb = Math.max(lr.get(0), 0);
         hb = Math.min(lr.get(1), maxLobbies - 1);
      }

      for (int i = lb; i <= hb; i++) {
         if (startLobbyInstance(i)) {
            return i;
         }
      }

      return -1;
   }

   private String getInternalScriptExceptionMessage(Throwable a) {
      if (!(a instanceof ScriptException)) {
         return null;
      }

      while (true) {
         Throwable t = a;
         a = a.getCause();

         if (a == null) {
            return t.getMessage();
         }
      }
   }

   private EventInstanceManager createInstance(String name, Object... args) throws ScriptException, NoSuchMethodException {
      return (EventInstanceManager) iv.invokeFunction(name, args);
   }

   private void registerEventInstance(String eventName, int lobbyId) {
      Integer oldLobby = instanceLocks.get(eventName);
      if (oldLobby != null) {
         setLockLobby(oldLobby, false);
      }

      instanceLocks.put(eventName, lobbyId);
   }

   public boolean startInstance(MapleExpedition expedition) {
      return startInstance(-1, expedition);
   }

   public boolean startInstance(int lobbyId, MapleExpedition expedition) {
      return startInstance(lobbyId, expedition, expedition.getLeader());
   }

   //Expedition method: starts an expedition
   public boolean startInstance(int lobbyId, MapleExpedition expedition, MapleCharacter leader) {
      return startInstanceInternal(lobbyId, leader, eventInstanceManager -> expedition.start(), leader.getClient().getChannel());
   }

   public boolean startInstanceInternal(int lobbyId, MapleCharacter leader, Consumer<EventInstanceManager> success, Object... args) {
      if (this.isDisposed()) {
         return false;
      }

      try {
         if (!playerPermit.contains(leader.getId()) && startSemaphore.tryAcquire(7777, TimeUnit.MILLISECONDS)) {
            playerPermit.add(leader.getId());

            startLock.lock();
            try {
               try {
                  if (lobbyId == -1) {
                     lobbyId = availableLobbyInstance();
                     if (lobbyId == -1) {
                        return false;
                     }
                  } else {
                     if (!startLobbyInstance(lobbyId)) {
                        return false;
                     }
                  }

                  EventInstanceManager eim;
                  try {
                     eim = createInstance("setup", args);
                     registerEventInstance(eim.getName(), lobbyId);
                  } catch (ScriptException | NullPointerException e) {
                     String message = getInternalScriptExceptionMessage(e);
                     if (message != null && !message.startsWith(EventInstanceInProgressException.KEY)) {
                        throw e;
                     }

                     if (lobbyId > -1) {
                        setLockLobby(lobbyId, false);
                     }
                     return false;
                  }

                  eim.setLeader(leader);
                  success.accept(eim);
                  eim.startEvent();
               } catch (ScriptException | NoSuchMethodException ex) {
                  LoggerUtil.printError(LoggerOriginator.ENGINE, LogType.EXCEPTION, ex);
               }

               return true;
            } finally {
               startLock.unlock();
               playerPermit.remove(leader.getId());
               startSemaphore.release();
            }
         }
      } catch (InterruptedException ie) {
         playerPermit.remove(leader.getId());
      }

      return false;
   }

   //Regular method: player
   public boolean startInstance(MapleCharacter chr) {
      return startInstance(-1, chr);
   }

   public boolean startInstance(int lobbyId, MapleCharacter leader) {
      return startInstance(lobbyId, leader, leader, 1);
   }

   public boolean startInstance(int lobbyId, MapleCharacter chr, MapleCharacter leader, int difficulty) {
      return startInstanceInternal(lobbyId, leader, eventInstanceManager -> {
         if (chr != null) {
            eventInstanceManager.registerPlayer(chr);
         }
      }, difficulty, (lobbyId > -1) ? lobbyId : leader.getId());
   }

   //PQ method: starts a PQ
   public boolean startInstance(MapleParty party, MapleMap map) {
      return startInstance(-1, party, map);
   }

   public boolean startInstance(int lobbyId, MapleParty party, MapleMap map) {
      return startInstance(lobbyId, party, map, party.getLeader().getPlayer().orElseThrow());
   }

   public boolean startInstance(int lobbyId, MapleParty party, MapleMap map, MapleCharacter leader) {
      if (this.isDisposed()) {
         return false;
      }

      try {
         if (!playerPermit.contains(leader.getId()) && startSemaphore.tryAcquire(7777, TimeUnit.MILLISECONDS)) {
            playerPermit.add(leader.getId());

            startLock.lock();
            try {
               try {
                  if (lobbyId == -1) {
                     lobbyId = availableLobbyInstance();
                     if (lobbyId == -1) {
                        return false;
                     }
                  } else {
                     if (!startLobbyInstance(lobbyId)) {
                        return false;
                     }
                  }

                  EventInstanceManager eim;
                  try {
                     eim = createInstance("setup", (Object) null);
                     registerEventInstance(eim.getName(), lobbyId);
                  } catch (ScriptException | NullPointerException e) {
                     String message = getInternalScriptExceptionMessage(e);
                     if (message != null && !message.startsWith(EventInstanceInProgressException.KEY)) {
                        throw e;
                     }

                     if (lobbyId > -1) {
                        setLockLobby(lobbyId, false);
                     }
                     return false;
                  }

                  eim.setLeader(leader);

                  eim.registerParty(party, map);
                  party.setEligibleMembers(null);

                  eim.startEvent();
               } catch (ScriptException | NoSuchMethodException ex) {
                  LoggerUtil.printError(LoggerOriginator.ENGINE, LogType.EXCEPTION, ex);
               }

               return true;
            } finally {
               startLock.unlock();
               playerPermit.remove(leader.getId());
               startSemaphore.release();
            }
         }
      } catch (InterruptedException ie) {
         playerPermit.remove(leader.getId());
      }

      return false;
   }

   //PQ method: starts a PQ with a difficulty level, requires function setup(difficulty, leader id) instead of setup()
   public boolean startInstance(MapleParty party, MapleMap map, int difficulty) {
      return startInstance(-1, party, map, difficulty);
   }

   public boolean startInstance(int lobbyId, MapleParty party, MapleMap map, int difficulty) {
      return startInstance(lobbyId, party, map, difficulty, party.getLeader().getPlayer().orElseThrow());
   }

   public boolean startInstance(int lobbyId, MapleParty party, MapleMap map, int difficulty, MapleCharacter leader) {
      return startInstanceInternal(lobbyId, leader, eventInstanceManager -> {
         eventInstanceManager.registerParty(party, map);
         party.setEligibleMembers(null);
      }, difficulty, (lobbyId > -1) ? lobbyId : party.getLeaderId());
   }

   //non-PQ method for starting instance
   public boolean startInstance(EventInstanceManager eim, String ldr) {
      return startInstance(-1, eim, ldr);
   }

   public boolean startInstance(EventInstanceManager eim, MapleCharacter ldr) {
      return startInstance(-1, eim, ldr.getName(), ldr);
   }

   public boolean startInstance(int lobbyId, EventInstanceManager eim, String ldr) {
      return eim.getEm().getChannelServer().getPlayerStorage().getCharacterByName(ldr).map(character -> startInstance(-1, eim, ldr, character)).orElse(false);
   }

   public boolean startInstance(int lobbyId, EventInstanceManager eim, String ldr, MapleCharacter leader) {
      if (this.isDisposed()) {
         return false;
      }

      try {
         if (!playerPermit.contains(leader.getId()) && startSemaphore.tryAcquire(7777, TimeUnit.MILLISECONDS)) {
            playerPermit.add(leader.getId());

            startLock.lock();
            try {
               try {
                  if (lobbyId == -1) {
                     lobbyId = availableLobbyInstance();
                     if (lobbyId == -1) {
                        return false;
                     }
                  } else {
                     if (!startLobbyInstance(lobbyId)) {
                        return false;
                     }
                  }

                  if (eim == null) {
                     if (lobbyId > -1) {
                        setLockLobby(lobbyId, false);
                     }
                     return false;
                  }
                  registerEventInstance(eim.getName(), lobbyId);
                  eim.setLeader(leader);

                  iv.invokeFunction("setup", eim);
                  eim.setProperty("leader", ldr);

                  eim.startEvent();
               } catch (ScriptException | NoSuchMethodException ex) {
                  LoggerUtil.printError(LoggerOriginator.ENGINE, LogType.EXCEPTION, ex);
               }

               return true;
            } finally {
               startLock.unlock();
               playerPermit.remove(leader.getId());
               startSemaphore.release();
            }
         }
      } catch (InterruptedException ie) {
         playerPermit.remove(leader.getId());
      }

      return false;
   }

   public List<MaplePartyCharacter> getEligibleParty(MapleParty party) {
      if (party == null) {
         return (new ArrayList<>());
      }
      try {
         Object p = iv.invokeFunction("getEligibleParty", party.getPartyMembersOnline());

         if (p != null) {
            List<MaplePartyCharacter> partyCharacters;

            partyCharacters = new ArrayList<>(((Map<String, MaplePartyCharacter>) (ScriptUtils.convert(p, Map.class))).values());

            party.setEligibleMembers(partyCharacters);
            return partyCharacters;
         }
      } catch (ScriptException | NoSuchMethodException ex) {
         ex.printStackTrace();
      }

      return (new ArrayList<>());
   }

   public void clearPQ(EventInstanceManager eim) {
      try {
         iv.invokeFunction("clearPQ", eim);
      } catch (ScriptException | NoSuchMethodException ex) {
         LoggerUtil.printError(LoggerOriginator.ENGINE, LogType.EXCEPTION, ex);
      }
   }

   public void clearPQ(EventInstanceManager eim, MapleMap toMap) {
      try {
         iv.invokeFunction("clearPQ", eim, toMap);
      } catch (ScriptException | NoSuchMethodException ex) {
         LoggerUtil.printError(LoggerOriginator.ENGINE, LogType.EXCEPTION, ex);
      }
   }

   public Optional<MapleMonster> getMonster(int mid) {
      return (MapleLifeFactory.getMonster(mid));
   }

   private void exportReadyGuild(Integer guildId) {
      server.getGuild(guildId).ifPresent(guild -> MessageBroadcaster.getInstance().sendGuildServerNotice(guild, ServerNoticeType.LIGHT_BLUE, I18nMessage.from("GUILD_QUEST_STRATEGY").with(getChannelServer().getId())));
   }

   private void exportMovedQueueToGuild(Integer guildId, int place) {
      server.getGuild(guildId).ifPresent(guild -> MessageBroadcaster.getInstance().sendGuildServerNotice(guild, ServerNoticeType.LIGHT_BLUE, I18nMessage.from("GUILD_QUEST_WAITING").with(getChannelServer().getId(), GameConstants.ordinal(place))));
   }

   private List<Integer> getNextGuildQueue() {
      synchronized (queuedGuilds) {
         Integer guildId = queuedGuilds.poll();
         if (guildId == null) {
            return null;
         }

         world.removeGuildQueued(guildId);
         Integer leaderId = queuedGuildLeaders.remove(guildId);

         int place = 1;
         for (Integer i : queuedGuilds) {
            exportMovedQueueToGuild(i, place);
            place++;
         }

         List<Integer> list = new ArrayList<>(2);
         list.add(guildId);
         list.add(leaderId);
         return list;
      }
   }

   public boolean isQueueFull() {
      synchronized (queuedGuilds) {
         return queuedGuilds.size() >= YamlConfig.config.server.EVENT_MAX_GUILD_QUEUE;
      }
   }

   public int getQueueSize() {
      synchronized (queuedGuilds) {
         return queuedGuilds.size();
      }
   }

   public byte addGuildToQueue(Integer guildId, Integer leaderId) {
      if (world.isGuildQueued(guildId)) {
         return -1;
      }

      if (!isQueueFull()) {
         boolean canStartAhead;
         synchronized (queuedGuilds) {
            canStartAhead = queuedGuilds.isEmpty();

            queuedGuilds.add(guildId);
            world.putGuildQueued(guildId);
            queuedGuildLeaders.put(guildId, leaderId);

            int place = queuedGuilds.size();
            exportMovedQueueToGuild(guildId, place);
         }

         if (canStartAhead) {
            if (!attemptStartGuildInstance()) {
               synchronized (queuedGuilds) {
                  queuedGuilds.add(guildId);
                  world.putGuildQueued(guildId);
                  queuedGuildLeaders.put(guildId, leaderId);
               }
            } else {
               return 2;
            }
         }

         return 1;
      } else {
         return 0;
      }
   }

   public boolean attemptStartGuildInstance() {
      MapleCharacter chr = null;
      List<Integer> guildInstance = null;
      while (chr == null) {
         guildInstance = getNextGuildQueue();
         if (guildInstance == null) {
            return false;
         }

         chr = channel.getPlayerStorage().getCharacterById(guildInstance.get(1)).orElse(null);
      }

      if (startInstance(chr)) {
         exportReadyGuild(guildInstance.get(0));
         return true;
      } else {
         return false;
      }
   }

   public void startQuest(MapleCharacter chr, int id, int npcId) {
      try {
         QuestProcessor.getInstance().forceStart(chr, id, npcId);
      } catch (NullPointerException ex) {
         ex.printStackTrace();
      }
   }

   public void completeQuest(MapleCharacter chr, int id, int npcId) {
      try {
         QuestProcessor.getInstance().forceComplete(chr, id, npcId);
      } catch (NullPointerException ex) {
         ex.printStackTrace();
      }
   }

   public int getTransportationTime(int travelTime) {
      return this.getWorldServer().getTransportationTime(travelTime);
   }

   private void fillEimQueue() {
      ThreadManager.getInstance().newTask(new EventManagerWorker());  //call new thread to fill up readied instances queue
   }

   private EventInstanceManager getReadyInstance() {
      queueLock.lock();
      try {
         if (readyInstances.isEmpty()) {
            fillEimQueue();
            return null;
         }

         EventInstanceManager eim = readyInstances.remove(0);
         fillEimQueue();

         return eim;
      } finally {
         queueLock.unlock();
      }
   }

   private void instantiateQueuedInstance() {
      int nextEventId;
      queueLock.lock();
      try {
         if (this.isDisposed() || readyInstances.size() + onLoadInstances >= Math.ceil((double) maxLobbies / 3.0)) {
            return;
         }

         onLoadInstances++;
         nextEventId = readyId;
         readyId++;
      } finally {
         queueLock.unlock();
      }

      EventInstanceManager eim = new EventInstanceManager(this, "sampleName" + nextEventId);
      queueLock.lock();
      try {
         if (this.isDisposed()) {  // EM already disposed
            return;
         }

         readyInstances.add(eim);
         onLoadInstances--;
      } finally {
         queueLock.unlock();
      }

      instantiateQueuedInstance();    // keep filling the queue until reach threshold.
   }

   private class EventManagerWorker implements Runnable {

      @Override
      public void run() {
         instantiateQueuedInstance();
      }
   }
}