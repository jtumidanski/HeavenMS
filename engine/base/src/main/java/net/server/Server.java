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
package net.server;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.security.Security;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Properties;
import java.util.Set;
import java.util.TimeZone;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.buffer.SimpleBufferAllocator;
import org.apache.mina.core.service.IoAcceptor;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;

import client.MapleCharacter;
import client.MapleClient;
import client.SkillFactory;
import client.command.CommandsExecutor;
import client.database.administrator.AccountAdministrator;
import client.database.administrator.CharacterAdministrator;
import client.database.administrator.NxCodeAdministrator;
import client.database.administrator.NxCodeItemAdministrator;
import client.database.administrator.PetAdministrator;
import client.database.administrator.WorldTransferAdministrator;
import client.database.data.CharacterData;
import client.database.data.WorldRankData;
import client.database.provider.AccountProvider;
import client.database.provider.CharacterProvider;
import client.database.provider.GlobalUserRankProvider;
import client.database.provider.NameChangeProvider;
import client.database.provider.NxCodeProvider;
import client.database.provider.NxCouponProvider;
import client.database.provider.PlayerNpcFieldProvider;
import client.database.provider.WorldTransferProvider;
import client.inventory.Item;
import client.inventory.ItemFactory;
import client.inventory.manipulator.MapleCashIdGenerator;
import client.newyear.NewYearCardRecord;
import client.processor.CharacterProcessor;
import client.processor.MapleFamilyProcessor;
import constants.GameConstants;
import constants.ItemConstants;
import constants.OpcodeConstants;
import constants.ServerConstants;
import net.MapleServerHandler;
import net.mina.MapleCodecFactory;
import net.server.audit.ThreadTracker;
import net.server.audit.locks.MonitoredLockType;
import net.server.audit.locks.MonitoredReentrantReadWriteLock;
import net.server.audit.locks.factory.MonitoredReentrantLockFactory;
import net.server.channel.Channel;
import net.server.coordinator.MapleSessionCoordinator;
import net.server.guild.MapleAlliance;
import net.server.guild.MapleGuild;
import net.server.guild.MapleGuildCharacter;
import net.server.processor.MapleGuildProcessor;
import net.server.worker.BossLogWorker;
import net.server.worker.CharacterDiseaseWorker;
import net.server.worker.CouponWorker;
import net.server.worker.DueyFredrickWorker;
import net.server.worker.EventRecallCoordinatorWorker;
import net.server.worker.InvitationWorker;
import net.server.worker.LoginCoordinatorWorker;
import net.server.worker.LoginStorageWorker;
import net.server.worker.RankingCommandWorker;
import net.server.worker.RankingLoginWorker;
import net.server.worker.ReleaseLockWorker;
import net.server.worker.RespawnWorker;
import net.server.world.World;
import server.CashShop.CashItemFactory;
import server.MapleSkillbookInformationProvider;
import server.ThreadManager;
import server.TimerManager;
import server.expeditions.MapleExpeditionBossLog;
import server.life.MaplePlayerNPCFactory;
import server.quest.MapleQuest;
import tools.AutoJCE;
import tools.DatabaseConnection;
import tools.FilePrinter;
import tools.Pair;

public class Server {

   private static final Set<Integer> activeFly = new HashSet<>();
   private static final Map<Integer, Integer> couponRates = new HashMap<>(30);
   private static final List<Integer> activeCoupons = new LinkedList<>();
   public static long uptime = System.currentTimeMillis();
   private static Server instance = null;
   private final Properties subnetInfo = new Properties();
   private final Map<Integer, Set<Integer>> accountChars = new HashMap<>();
   private final Map<Integer, Short> accountCharacterCount = new HashMap<>();
   private final Map<Integer, Integer> worldChars = new HashMap<>();
   private final Map<String, Integer> transitioningChars = new HashMap<>();
   private final Map<Integer, MapleGuild> guilds = new HashMap<>(100);
   private final Map<MapleClient, Long> inLoginState = new HashMap<>(100);
   private final PlayerBuffStorage buffStorage = new PlayerBuffStorage();
   private final Map<Integer, MapleAlliance> alliances = new HashMap<>(100);
   private final Map<Integer, NewYearCardRecord> newyears = new HashMap<>();
   private final List<MapleClient> processDiseaseAnnouncePlayers = new LinkedList<>();
   private final List<MapleClient> registeredDiseaseAnnouncePlayers = new LinkedList<>();
   private final List<WorldRankData> playerRanking = new LinkedList<>();
   private final Lock srvLock = MonitoredReentrantLockFactory.createLock(MonitoredLockType.SERVER);
   private final Lock disLock = MonitoredReentrantLockFactory.createLock(MonitoredLockType.SERVER_DISEASES);
   private final ReentrantReadWriteLock wldLock = new MonitoredReentrantReadWriteLock(MonitoredLockType.SERVER_WORLDS, true);
   private final ReadLock wldRLock = wldLock.readLock();
   private final WriteLock wldWLock = wldLock.writeLock();
   private final ReentrantReadWriteLock lgnLock = new MonitoredReentrantReadWriteLock(MonitoredLockType.SERVER_LOGIN, true);
   private final ReadLock lgnRLock = lgnLock.readLock();
   private final WriteLock lgnWLock = lgnLock.writeLock();
   private final AtomicLong currentTime = new AtomicLong(0);
   private IoAcceptor acceptor;
   private List<Map<Integer, String>> channels = new LinkedList<>();
   private List<World> worlds = new ArrayList<>();
   private List<Pair<Integer, String>> worldRecommendedList = new LinkedList<>();
   private long serverCurrentTime = 0;

   private boolean availableDeveloperRoom = false;
   private boolean online = false;

   public static Server getInstance() {
      if (instance == null) {
         instance = new Server();
      }
      return instance;
   }

   private static int getWorldProperty(Properties p, String property, int wid, int defaultValue) {
      String content = p.getProperty(property + wid);
      return content != null ? Integer.parseInt(content) : defaultValue;
   }

   public static Properties loadWorldINI() {
      Properties p = new Properties();
      try {
         p.load(new FileInputStream("world.ini"));
         return p;
      } catch (Exception e) {
         e.printStackTrace();
         System.out.println("[SEVERE] Could not find/open 'world.ini'.");
         return null;
      }
   }

   private static long getTimeLeftForNextHour() {
      Calendar nextHour = Calendar.getInstance();
      nextHour.add(Calendar.HOUR, 1);
      nextHour.set(Calendar.MINUTE, 0);
      nextHour.set(Calendar.SECOND, 0);

      return Math.max(0, nextHour.getTimeInMillis() - System.currentTimeMillis());
   }

   public static long getTimeLeftForNextDay() {
      Calendar nextDay = Calendar.getInstance();
      nextDay.add(Calendar.DAY_OF_MONTH, 1);
      nextDay.set(Calendar.HOUR_OF_DAY, 0);
      nextDay.set(Calendar.MINUTE, 0);
      nextDay.set(Calendar.SECOND, 0);

      return Math.max(0, nextDay.getTimeInMillis() - System.currentTimeMillis());
   }

   public static void cleanNxcodeCoupons(Connection con) {
      if (!ServerConstants.USE_CLEAR_OUTDATED_COUPONS) {
         return;
      }

      long timeClear = System.currentTimeMillis() - 14 * 24 * 60 * 60 * 1000;

      List<Integer> expiredCodes = NxCodeProvider.getInstance().getExpiredCodes(con, timeClear);
      NxCodeItemAdministrator.getInstance().deleteItems(con, expiredCodes);
      NxCodeAdministrator.getInstance().deleteExpired(con, timeClear);
   }

   private static List<WorldRankData> updatePlayerRankingFromDB(int worldId) {
      return DatabaseConnection.getInstance().withConnectionResult(connection -> {
         if (!ServerConstants.USE_WHOLE_SERVER_RANKING) {
            if (worldId >= 0) {
               return GlobalUserRankProvider.getInstance().getWorldRanks(connection, worldId);
            } else {
               return GlobalUserRankProvider.getInstance().getWorldRanksRange(connection, -worldId);
            }
         } else {
            return GlobalUserRankProvider.getInstance().getRanksWholeServer(connection, worldId);
         }
      }).orElse(new ArrayList<>());
   }

   public static void main(String[] args) {
      System.setProperty("wzpath", "wz");
      Security.setProperty("crypto.policy", "unlimited");
      AutoJCE.removeCryptographyRestrictions();
      Server.getInstance().init();
   }

   private static Pair<Short, List<List<MapleCharacter>>> loadAccountCharactersViewFromDb(int accId, int wlen) {
      short characterCount = 0;
      List<List<MapleCharacter>> wchars = new ArrayList<>(wlen);
      for (int i = 0; i < wlen; i++) wchars.add(i, new LinkedList<>());

      List<MapleCharacter> chars = new LinkedList<>();
      int curWorld = 0;
      List<Pair<Item, Integer>> accEquips = ItemFactory.loadEquippedItems(accId, true, true);
      Map<Integer, List<Item>> accPlayerEquips = new HashMap<>();

      for (Pair<Item, Integer> ae : accEquips) {
         List<Item> playerEquips = accPlayerEquips.computeIfAbsent(ae.getRight(), k -> new LinkedList<>());
         playerEquips.add(ae.getLeft());
      }

      List<CharacterData> characterDataList = DatabaseConnection.getInstance().withConnectionResult(connection -> CharacterProvider.getInstance().getByAccountId(connection, accId)).orElse(new ArrayList<>());
      for (CharacterData characterData : characterDataList) {
         characterCount++;

         int cworld = characterData.world();
         if (cworld >= wlen) {
            continue;
         }

         if (cworld > curWorld) {
            wchars.add(curWorld, chars);

            curWorld = cworld;
            chars = new LinkedList<>();
         }

         chars.add(CharacterProcessor.getInstance().loadCharacterEntryFromDB(characterData, accPlayerEquips.get(characterData.id())));
      }

      wchars.add(curWorld, chars);

      return new Pair<>(characterCount, wchars);
   }

   public void loadAccountStorages(MapleClient c) {
      int accountId = c.getAccID();
      Set<Integer> accWorlds = new HashSet<>();
      lgnWLock.lock();
      try {
         Set<Integer> chars = accountChars.get(accountId);

         for (Integer cid : chars) {
            Integer worldid = worldChars.get(cid);
            if (worldid != null) {
               accWorlds.add(worldid);
            }
         }
      } finally {
         lgnWLock.unlock();
      }

      List<World> worldList = this.getWorlds();
      for (Integer worldid : accWorlds) {
         if (worldid < worldList.size()) {
            World wserv = worldList.get(worldid);
            wserv.registerAccountStorage(accountId);
         }
      }
   }

   private static String getRemoteIp(IoSession session) {
      return MapleSessionCoordinator.getSessionRemoteAddress(session);
   }

   public int getCurrentTimestamp() {
      return (int) (Server.getInstance().getCurrentTime() - Server.uptime);
   }

   public long getCurrentTime() {  // returns a slightly delayed time value, under frequency of UPDATE_INTERVAL
      return serverCurrentTime;
   }

   public void updateCurrentTime() {
      serverCurrentTime = currentTime.addAndGet(ServerConstants.UPDATE_INTERVAL);
   }

   public long forceUpdateCurrentTime() {
      long timeNow = System.currentTimeMillis();
      serverCurrentTime = timeNow;
      currentTime.set(timeNow);

      return timeNow;
   }

   public boolean isOnline() {
      return online;
   }

   public List<Pair<Integer, String>> worldRecommendedList() {
      return worldRecommendedList;
   }

   public void setNewYearCard(NewYearCardRecord nyc) {
      newyears.put(nyc.getId(), nyc);
   }

   public NewYearCardRecord getNewYearCard(int cardid) {
      return newyears.get(cardid);
   }

   public NewYearCardRecord removeNewYearCard(int cardid) {
      return newyears.remove(cardid);
   }

   public void setAvailableDeveloperRoom() {
      availableDeveloperRoom = true;
   }

   public boolean canEnterDeveloperRoom() {
      return availableDeveloperRoom;
   }

   private void loadPlayerNpcMapStepFromDb() {
      DatabaseConnection.getInstance().withConnection(connection -> PlayerNpcFieldProvider.getInstance().get(connection).forEach(fieldData -> {
         World world = getWorld(fieldData.worldId());
         if (world != null) {
            world.setPlayerNpcMapData(fieldData.mapId(), fieldData.step(), fieldData.podium());
         }
      }));
   }

   public World getWorld(int id) {
      wldRLock.lock();
      try {
         try {
            return worlds.get(id);
         } catch (IndexOutOfBoundsException e) {
            return null;
         }
      } finally {
         wldRLock.unlock();
      }
   }

   public List<World> getWorlds() {
      wldRLock.lock();
      try {
         return Collections.unmodifiableList(worlds);
      } finally {
         wldRLock.unlock();
      }
   }

   public int getWorldsSize() {
      wldRLock.lock();
      try {
         return worlds.size();
      } finally {
         wldRLock.unlock();
      }
   }

   public Channel getChannel(int world, int channel) {
      try {
         return this.getWorld(world).getChannel(channel);
      } catch (NullPointerException npe) {
         return null;
      }
   }

   public List<Channel> getChannelsFromWorld(int world) {
      try {
         return this.getWorld(world).getChannels();
      } catch (NullPointerException npe) {
         return new ArrayList<>(0);
      }
   }

   public List<Channel> getAllChannels() {
      try {
         List<Channel> channelz = new ArrayList<>();
         for (World world : this.getWorlds()) {
            channelz.addAll(world.getChannels());
         }
         return channelz;
      } catch (NullPointerException npe) {
         return new ArrayList<>(0);
      }
   }

   public Set<Integer> getOpenChannels(int world) {
      wldRLock.lock();
      try {
         return new HashSet<>(channels.get(world).keySet());
      } finally {
         wldRLock.unlock();
      }
   }

   private String getIP(int world, int channel) {
      wldRLock.lock();
      try {
         return channels.get(world).get(channel);
      } finally {
         wldRLock.unlock();
      }
   }

   public String[] getInetSocket(int world, int channel) {
      try {
         return getIP(world, channel).split(":");
      } catch (Exception e) {
         return null;
      }
   }

   private void dumpData() {
      wldWLock.lock();
      try {
         System.out.println(worlds);
         System.out.println(channels);
         System.out.println(worldRecommendedList);
         System.out.println();
         System.out.println("---------------------");
      } finally {
         wldWLock.unlock();
      }
   }

   public int addChannel(int worldid) {
      wldWLock.lock();
      try {
         if (worldid >= worlds.size()) {
            return -3;
         }

         Map<Integer, String> worldChannels = channels.get(worldid);
         if (worldChannels == null) {
            return -3;
         }

         int channelid = worldChannels.size();
         if (channelid >= ServerConstants.CHANNEL_SIZE) {
            return -2;
         }

         Properties p = loadWorldINI();
         if (p == null) {
            return -1;
         }

         channelid++;
         World world = this.getWorld(worldid);
         Channel channel = new Channel(worldid, channelid, getCurrentTime());

         channel.setServerMessage(p.getProperty("whyamirecommended" + worldid));

         world.addChannel(channel);
         worldChannels.put(channelid, channel.getIP());

         return channelid;
      } finally {
         wldWLock.unlock();
      }
   }

   public int addWorld() {
      Properties p = loadWorldINI();
      if (p == null) {
         return -2;
      }

      int newWorld = initWorld(p);
      if (newWorld > -1) {
         installWorldPlayerRanking(newWorld);

         Set<Integer> accounts;
         lgnRLock.lock();
         try {
            accounts = new HashSet<>(accountChars.keySet());
         } finally {
            lgnRLock.unlock();
         }

         for (Integer accId : accounts) {
            loadAccountCharactersView(accId, 0, newWorld);
         }
      }

      return newWorld;
   }

   private int initWorld(Properties p) {
      wldWLock.lock();
      try {
         int i = worlds.size();

         if (i >= ServerConstants.WLDLIST_SIZE) {
            return -1;
         }

         System.out.println("Starting world " + i);
         int exprate = getWorldProperty(p, "exprate", i, ServerConstants.EXP_RATE);
         int mesorate = getWorldProperty(p, "mesorate", i, ServerConstants.MESO_RATE);
         int droprate = getWorldProperty(p, "droprate", i, ServerConstants.DROP_RATE);
         int bossdroprate = getWorldProperty(p, "bossdroprate", i, ServerConstants.BOSS_DROP_RATE);
         int questrate = getWorldProperty(p, "questrate", i, ServerConstants.QUEST_RATE);
         int travelrate = getWorldProperty(p, "travelrate", i, ServerConstants.TRAVEL_RATE);
         int fishingrate = getWorldProperty(p, "fishrate", i, ServerConstants.FISHING_RATE);

         World world = new World(i,
               Integer.parseInt(p.getProperty("flag" + i)),
               p.getProperty("eventmessage" + i),
               exprate, droprate, bossdroprate, mesorate, questrate, travelrate, fishingrate);

         worldRecommendedList.add(new Pair<>(i, p.getProperty("whyamirecommended" + i)));
         worlds.add(world);

         Map<Integer, String> channelInfo = new HashMap<>();
         long bootTime = getCurrentTime();
         for (int channelId = 1; channelId <= Integer.parseInt(p.getProperty("channels" + i)); channelId++) {
            Channel channel = new Channel(i, channelId, bootTime);

            world.addChannel(channel);
            channelInfo.put(channelId, channel.getIP());
         }

         channels.add(i, channelInfo);

         world.setServerMessage(p.getProperty("servermessage" + i));
         System.out.println("Finished loading world " + i + "\r\n");

         return i;
      } finally {
         wldWLock.unlock();
      }
   }

   public boolean removeChannel(int worldid) {   //lol don't!
      wldWLock.lock();
      try {
         if (worldid >= worlds.size()) {
            return false;
         }

         World world = worlds.get(worldid);
         if (world != null) {
            int channel = world.removeChannel();

            Map<Integer, String> m = channels.get(worldid);
            if (m != null) {
               m.remove(channel);
            }

            return channel > -1;
         }
      } finally {
         wldWLock.unlock();
      }

      return false;
   }

   public boolean removeWorld() {   //lol don't!
      World w;
      int worldid;

      wldRLock.lock();
      try {
         worldid = worlds.size() - 1;
         if (worldid < 0) {
            return false;
         }

         w = worlds.get(worldid);
      } finally {
         wldRLock.unlock();
      }

      if (w == null || !w.canUninstall()) {
         return false;
      }

      wldWLock.lock();
      try {
         if (worldid == worlds.size() - 1) {
            removeWorldPlayerRanking();
            w.shutdown();

            worlds.remove(worldid);
            channels.remove(worldid);
            worldRecommendedList.remove(worldid);
         } else {
            return false;
         }
      } finally {
         wldWLock.unlock();
      }

      return true;
   }

   private void resetServerWorlds() {  // thanks maple006 for noticing proprietary lists assigned to null
      wldWLock.lock();
      try {
         worlds.clear();
         channels.clear();
         worldRecommendedList.clear();
      } finally {
         wldWLock.unlock();
      }
   }

   public Map<Integer, Integer> getCouponRates() {
      return couponRates;
   }

   private void loadCouponRates(Connection c) {
      NxCouponProvider.getInstance().getCoupons(c).forEach(coupon -> couponRates.put(coupon.couponId(), coupon.rate()));
   }

   public List<Integer> getActiveCoupons() {
      synchronized (activeCoupons) {
         return activeCoupons;
      }
   }

   public void commitActiveCoupons() {
      for (World world : getWorlds()) {
         for (MapleCharacter chr : world.getPlayerStorage().getAllCharacters()) {
            if (!chr.isLoggedin()) {
               continue;
            }

            chr.updateCouponRates();
         }
      }
   }

   public void toggleCoupon(Integer couponId) {
      if (ItemConstants.isRateCoupon(couponId)) {
         synchronized (activeCoupons) {
            if (activeCoupons.contains(couponId)) {
               activeCoupons.remove(couponId);
            } else {
               activeCoupons.add(couponId);
            }

            commitActiveCoupons();
         }
      }
   }

   public void updateActiveCoupons() {
      synchronized (activeCoupons) {
         activeCoupons.clear();
         DatabaseConnection.getInstance().withConnectionResult(connection -> NxCouponProvider.getInstance().getActiveCoupons(connection))
               .ifPresent(result -> result.forEach(coupon -> activeCoupons.add(coupon.couponId())));
      }
   }

   public void runAnnouncePlayerDiseasesSchedule() {
      List<MapleClient> processDiseaseAnnounceClients;
      disLock.lock();
      try {
         processDiseaseAnnounceClients = new LinkedList<>(processDiseaseAnnouncePlayers);
         processDiseaseAnnouncePlayers.clear();
      } finally {
         disLock.unlock();
      }

      while (!processDiseaseAnnounceClients.isEmpty()) {
         MapleClient c = processDiseaseAnnounceClients.remove(0);
         MapleCharacter player = c.getPlayer();
         if (player != null && player.isLoggedinWorld()) {
            player.announceDiseases();
            player.collectDiseases();
         }
      }

      disLock.lock();
      try {
         // this is to force the system to wait for at least one complete tick before releasing disease info for the registered clients
         while (!registeredDiseaseAnnouncePlayers.isEmpty()) {
            MapleClient c = registeredDiseaseAnnouncePlayers.remove(0);
            processDiseaseAnnouncePlayers.add(c);
         }
      } finally {
         disLock.unlock();
      }
   }

   public void registerAnnouncePlayerDiseases(MapleClient c) {
      disLock.lock();
      try {
         registeredDiseaseAnnouncePlayers.add(c);
      } finally {
         disLock.unlock();
      }
   }

   public WorldRankData getWorldPlayerRanking(int worldId) {
      wldRLock.lock();
      try {
         return playerRanking.get(!ServerConstants.USE_WHOLE_SERVER_RANKING ? worldId : 0);
      } finally {
         wldRLock.unlock();
      }
   }

   private void installWorldPlayerRanking(int worldId) {
      List<WorldRankData> ranking = updatePlayerRankingFromDB(worldId);
      if (!ranking.isEmpty()) {
         wldWLock.lock();
         try {
            playerRanking.clear();
            playerRanking.addAll(ranking);
         } finally {
            wldWLock.unlock();
         }
      }
   }

   private void removeWorldPlayerRanking() {
      if (!ServerConstants.USE_WHOLE_SERVER_RANKING) {
         wldWLock.lock();
         try {
            if (playerRanking.size() < this.getWorldsSize()) {
               return;
            }

            playerRanking.clear();
         } finally {
            wldWLock.unlock();
         }
      } else {
         List<WorldRankData> ranking = updatePlayerRankingFromDB(-1 * (this.getWorldsSize() - 2));  // update ranking list
         wldWLock.lock();
         try {
            playerRanking.addAll(ranking);
         } finally {
            wldWLock.unlock();
         }
      }
   }

   public void updateWorldPlayerRanking() {
      List<WorldRankData> rankUpdates = updatePlayerRankingFromDB(-1 * (this.getWorldsSize() - 1));
      if (!rankUpdates.isEmpty()) {
         wldWLock.lock();
         try {
            playerRanking.clear();
            playerRanking.addAll(rankUpdates);
         } finally {
            wldWLock.unlock();
         }
      }
   }

   private void initWorldPlayerRanking() {
      if (ServerConstants.USE_WHOLE_SERVER_RANKING) {
         playerRanking.add(new WorldRankData(0));
      }
      updateWorldPlayerRanking();
   }

   private void clearMissingPetsFromDb() {
      DatabaseConnection.getInstance().withConnection(connection -> {
         PetAdministrator.getInstance().unreferenceMissingPetsFromInventory(connection);
         PetAdministrator.getInstance().deleteMissingPets(connection);
      });
   }


   public void init() {
      Properties p = loadWorldINI();
      if (p == null) {
         System.exit(0);
      }

      System.out.println("HeavenMS v" + ServerConstants.VERSION + " starting up.\r\n");

      if (ServerConstants.SHUTDOWNHOOK) {
         Runtime.getRuntime().addShutdownHook(new Thread(shutdown(false)));
      }

      TimeZone.setDefault(TimeZone.getTimeZone(ServerConstants.TIMEZONE));

      DatabaseConnection.getInstance().withConnection(connection -> {
         AccountAdministrator.getInstance().logoutAllAccounts(connection);
         CharacterAdministrator.getInstance().removeAllMerchants(connection);
         cleanNxcodeCoupons(connection);
         loadCouponRates(connection);
         updateActiveCoupons();
      });

      applyAllNameChanges(); //name changes can be missed by INSTANT_NAME_CHANGE
      applyAllWorldTransfers();
      clearMissingPetsFromDb();
      MapleCashIdGenerator.getInstance().loadExistentCashIdsFromDb();

      IoBuffer.setUseDirectBuffer(false);
      IoBuffer.setAllocator(new SimpleBufferAllocator());
      acceptor = new NioSocketAcceptor();
      acceptor.getFilterChain().addLast("codec", new ProtocolCodecFilter(new MapleCodecFactory()));

      ThreadManager.getInstance().start();
      TimerManager tMan = TimerManager.getInstance();
      tMan.start();
      tMan.register(tMan.purge(), ServerConstants.PURGING_INTERVAL);//Purging ftw...
      disconnectIdlesOnLoginTask();

      long timeLeft = getTimeLeftForNextHour();
      tMan.register(new CharacterDiseaseWorker(), ServerConstants.UPDATE_INTERVAL, ServerConstants.UPDATE_INTERVAL);
      tMan.register(new ReleaseLockWorker(), 2 * 60 * 1000, 2 * 60 * 1000);
      tMan.register(new CouponWorker(), ServerConstants.COUPON_INTERVAL, timeLeft);
      tMan.register(new RankingCommandWorker(), 5 * 60 * 1000, 5 * 60 * 1000);
      tMan.register(new RankingLoginWorker(), ServerConstants.RANKING_INTERVAL, timeLeft);
      tMan.register(new LoginCoordinatorWorker(), 60 * 60 * 1000, timeLeft);
      tMan.register(new EventRecallCoordinatorWorker(), 60 * 60 * 1000, timeLeft);
      tMan.register(new LoginStorageWorker(), 2 * 60 * 1000, 2 * 60 * 1000);
      tMan.register(new DueyFredrickWorker(), 60 * 60 * 1000, timeLeft);
      tMan.register(new InvitationWorker(), 30 * 1000, 30 * 1000);
      tMan.register(new RespawnWorker(), ServerConstants.RESPAWN_INTERVAL, ServerConstants.RESPAWN_INTERVAL);

      timeLeft = getTimeLeftForNextDay();
      MapleExpeditionBossLog.resetBossLogTable();
      tMan.register(new BossLogWorker(), 24 * 60 * 60 * 1000, timeLeft);

      long timeToTake = System.currentTimeMillis();
      SkillFactory.loadAllSkills();
      System.out.println("Skills loaded in " + ((System.currentTimeMillis() - timeToTake) / 1000.0) + " seconds");

      timeToTake = System.currentTimeMillis();

      CashItemFactory.getSpecialCashItems();
      System.out.println("Items loaded in " + ((System.currentTimeMillis() - timeToTake) / 1000.0) + " seconds");

      timeToTake = System.currentTimeMillis();
      MapleQuest.loadAllQuest();
      System.out.println("Quest loaded in " + ((System.currentTimeMillis() - timeToTake) / 1000.0) + " seconds\r\n");

      NewYearCardRecord.startPendingNewYearCardRequests();

      if (ServerConstants.USE_THREAD_TRACKER) {
         ThreadTracker.getInstance().registerThreadTrackerTask();
      }

      try {
         Integer worldCount = Math.min(GameConstants.WORLD_NAMES.length, Integer.parseInt(p.getProperty("worlds")));

         for (int i = 0; i < worldCount; i++) {
            initWorld(p);
         }
         initWorldPlayerRanking();

         MaplePlayerNPCFactory.loadFactoryMetadata();
         loadPlayerNpcMapStepFromDb();
      } catch (Exception e) {
         e.printStackTrace();//For those who get errors
         System.out.println("[SEVERE] Syntax error in 'world.ini'.");
         System.exit(0);
      }

      System.out.println();

      if (ServerConstants.USE_FAMILY_SYSTEM) {
         timeToTake = System.currentTimeMillis();
         MapleFamilyProcessor.getInstance().loadAllFamilies();
         System.out.println("Families loaded in " + ((System.currentTimeMillis() - timeToTake) / 1000.0) + " seconds\r\n");
      }

      System.out.println();

      acceptor.getSessionConfig().setIdleTime(IdleStatus.BOTH_IDLE, 30);
      acceptor.setHandler(new MapleServerHandler());
      try {
         acceptor.bind(new InetSocketAddress(8484));
      } catch (IOException ex) {
         ex.printStackTrace();
      }

      System.out.println("Listening on port 8484\r\n\r\n");

      System.out.println("HeavenMS is now online.\r\n");
      online = true;

      MapleSkillbookInformationProvider.getInstance();
      OpcodeConstants.generateOpcodeNames();
      CommandsExecutor.getInstance();
   }

   public Properties getSubnetInfo() {
      return subnetInfo;
   }

   public Optional<MapleAlliance> getAlliance(int id) {
      synchronized (alliances) {
         if (alliances.containsKey(id)) {
            return Optional.of(alliances.get(id));
         }
         return Optional.empty();
      }
   }

   public void addAlliance(int id, MapleAlliance alliance) {
      synchronized (alliances) {
         if (!alliances.containsKey(id)) {
            alliances.put(id, alliance);
         }
      }
   }

   public void disbandAlliance(int id) {
      synchronized (alliances) {
         MapleAlliance alliance = alliances.get(id);
         if (alliance != null) {
            for (Integer gid : alliance.getGuilds()) {
               guilds.get(gid).setAllianceId(0);
            }
            alliances.remove(id);
         }
      }
   }

   public void allianceMessage(int id, final byte[] packet, int exception, int guildex) {
      MapleAlliance alliance = alliances.get(id);
      if (alliance != null) {
         for (Integer gid : alliance.getGuilds()) {
            if (guildex == gid) {
               continue;
            }
            MapleGuild guild = guilds.get(gid);
            if (guild != null) {
               guild.broadcast(packet, exception);
            }
         }
      }
   }

   public boolean addGuildtoAlliance(int aId, int guildId) {
      MapleAlliance alliance = alliances.get(aId);
      if (alliance != null) {
         alliance.addGuild(guildId);
         guilds.get(guildId).setAllianceId(aId);
         return true;
      }
      return false;
   }

   public boolean removeGuildFromAlliance(int aId, int guildId) {
      MapleAlliance alliance = alliances.get(aId);
      if (alliance != null) {
         alliance.removeGuild(guildId);
         guilds.get(guildId).setAllianceId(0);
         return true;
      }
      return false;
   }

   public boolean setAllianceRanks(int aId, String[] ranks) {
      MapleAlliance alliance = alliances.get(aId);
      if (alliance != null) {
         alliance.setRankTitle(ranks);
         return true;
      }
      return false;
   }

   public boolean setAllianceNotice(int aId, String notice) {
      MapleAlliance alliance = alliances.get(aId);
      if (alliance != null) {
         alliance.setNotice(notice);
         return true;
      }
      return false;
   }

   public boolean increaseAllianceCapacity(int aId, int inc) {
      MapleAlliance alliance = alliances.get(aId);
      if (alliance != null) {
         alliance.increaseCapacity(inc);
         return true;
      }
      return false;
   }

   public int createGuild(int leaderId, String name) {
      return MapleGuildProcessor.getInstance().createGuild(leaderId, name);
   }

   public Optional<MapleGuild> getGuildByName(String name) {
      synchronized (guilds) {
         for (MapleGuild mg : guilds.values()) {
            if (mg.getName().equalsIgnoreCase(name)) {
               return Optional.of(mg);
            }
         }

         return Optional.empty();
      }
   }

   public Optional<MapleGuild> getGuild(int id) {
      synchronized (guilds) {
         if (guilds.get(id) != null) {
            return Optional.of(guilds.get(id));
         }

         return Optional.empty();
      }
   }

   public Optional<MapleGuild> getGuild(int id, int world) {
      return getGuild(id, world, null);
   }

   public Optional<MapleGuild> getGuild(int id, int world, MapleCharacter mc) {
      synchronized (guilds) {
         MapleGuild g = guilds.get(id);
         if (g != null) {
            return Optional.of(g);
         }

         g = new MapleGuild(id, world);
         if (g.getId() < 1) {
            return Optional.empty();
         }

         if (mc != null) {
            MapleGuildCharacter mgc = g.getMGC(mc.getId());
            if (mgc != null) {
               mc.setMGC(mgc);
               mgc.setCharacter(mc);
            } else {
               FilePrinter.printError(FilePrinter.GUILD_CHAR_ERROR, "Could not find " + mc.getName() + " when loading guild " + id + ".");
            }
            g.setOnline(mc.getId(), true, mc.getClient().getChannel());
         }

         guilds.put(id, g);
         return Optional.of(g);
      }
   }

   public void setGuildMemberOnline(MapleCharacter mc, boolean bOnline, int channel) {
      getGuild(mc.getGuildId(), mc.getWorld(), mc).ifPresent(guild -> guild.setOnline(mc.getId(), bOnline, channel));
   }

   public int addGuildMember(MapleGuildCharacter mgc, MapleCharacter chr) {
      MapleGuild g = guilds.get(mgc.getGuildId());
      if (g != null) {
         return g.addGuildMember(mgc, chr);
      }
      return 0;
   }

   public boolean setGuildAllianceId(int gId, int aId) {
      MapleGuild guild = guilds.get(gId);
      if (guild != null) {
         guild.setAllianceId(aId);
         return true;
      }
      return false;
   }

   public void resetAllianceGuildPlayersRank(int gId) {
      guilds.get(gId).resetAllianceGuildPlayersRank();
   }

   public void leaveGuild(MapleGuildCharacter mgc) {
      MapleGuild g = guilds.get(mgc.getGuildId());
      if (g != null) {
         g.leaveGuild(mgc);
      }
   }

   public void guildChat(int gid, String name, int cid, String msg) {
      MapleGuild g = guilds.get(gid);
      if (g != null) {
         g.guildChat(name, cid, msg);
      }
   }

   public void changeRank(int gid, int cid, int newRank) {
      MapleGuild g = guilds.get(gid);
      if (g != null) {
         g.changeRank(cid, newRank);
      }
   }

   public void expelMember(MapleGuildCharacter initiator, String name, int cid) {
      MapleGuild g = guilds.get(initiator.getGuildId());
      if (g != null) {
         g.expelMember(initiator, name, cid);
      }
   }

   public void setGuildNotice(int gid, String notice) {
      MapleGuild g = guilds.get(gid);
      if (g != null) {
         g.setGuildNotice(notice);
      }
   }

   public void memberLevelJobUpdate(MapleGuildCharacter mgc) {
      MapleGuild g = guilds.get(mgc.getGuildId());
      if (g != null) {
         g.memberLevelJobUpdate(mgc);
      }
   }

   public void changeRankTitle(int gid, String[] ranks) {
      MapleGuild g = guilds.get(gid);
      if (g != null) {
         g.changeRankTitle(ranks);
      }
   }

   public void setGuildEmblem(int gid, short bg, byte bgcolor, short logo, byte logocolor) {
      MapleGuild g = guilds.get(gid);
      if (g != null) {
         g.setGuildEmblem(bg, bgcolor, logo, logocolor);
      }
   }

   public void disbandGuild(int gid) {
      synchronized (guilds) {
         MapleGuild g = guilds.get(gid);
         g.disbandGuild();
         guilds.remove(gid);
      }
   }

   public boolean increaseGuildCapacity(int gid) {
      MapleGuild g = guilds.get(gid);
      if (g != null) {
         return g.increaseCapacity();
      }
      return false;
   }

   public void gainGP(int gid, int amount) {
      MapleGuild g = guilds.get(gid);
      if (g != null) {
         g.gainGP(amount);
      }
   }

   public void guildMessage(int gid, byte[] packet) {
      guildMessage(gid, packet, -1);
   }

   public void guildMessage(int gid, byte[] packet, int exception) {
      MapleGuild g = guilds.get(gid);
      if (g != null) {
         g.broadcast(packet, exception);
      }
   }

   public PlayerBuffStorage getPlayerBuffStorage() {
      return buffStorage;
   }

   public void deleteGuildCharacter(MapleCharacter mc) {
      setGuildMemberOnline(mc, false, (byte) -1);
      if (mc.getMGC().getGuildRank() > 1) {
         leaveGuild(mc.getMGC());
      } else {
         disbandGuild(mc.getMGC().getGuildId());
      }
   }

   public void deleteGuildCharacter(MapleGuildCharacter mgc) {
      if (mgc.getCharacter() != null) {
         setGuildMemberOnline(mgc.getCharacter(), false, (byte) -1);
      }
      if (mgc.getGuildRank() > 1) {
         leaveGuild(mgc);
      } else {
         disbandGuild(mgc.getGuildId());
      }
   }

   public void reloadGuildCharacters(int world) {
      World worlda = getWorld(world);
      for (MapleCharacter mc : worlda.getPlayerStorage().getAllCharacters()) {
         if (mc.getGuildId() > 0) {
            setGuildMemberOnline(mc, true, worlda.getId());
            memberLevelJobUpdate(mc.getMGC());
         }
      }
      worlda.reloadGuildSummary();
   }

   public void broadcastMessage(int world, final byte[] packet) {
      for (Channel ch : getChannelsFromWorld(world)) {
         ch.broadcastPacket(packet);
      }
   }

   public void broadcastGMMessage(int world, final byte[] packet) {
      for (Channel ch : getChannelsFromWorld(world)) {
         ch.broadcastGMPacket(packet);
      }
   }

   public boolean isGmOnline(int world) {
      for (Channel ch : getChannelsFromWorld(world)) {
         for (MapleCharacter player : ch.getPlayerStorage().getAllCharacters()) {
            if (player.isGM()) {
               return true;
            }
         }
      }
      return false;
   }

   public void changeFly(Integer accountid, boolean canFly) {
      if (canFly) {
         activeFly.add(accountid);
      } else {
         activeFly.remove(accountid);
      }
   }

   public boolean canFly(Integer accountid) {
      return activeFly.contains(accountid);
   }

   public int getCharacterWorld(Integer chrid) {
      lgnRLock.lock();
      try {
         Integer worldid = worldChars.get(chrid);
         return worldid != null ? worldid : -1;
      } finally {
         lgnRLock.unlock();
      }
   }

   public boolean haveCharacterEntry(Integer accountid, Integer chrid) {
      lgnRLock.lock();
      try {
         Set<Integer> accChars = accountChars.get(accountid);
         return accChars.contains(chrid);
      } finally {
         lgnRLock.unlock();
      }
   }

   public short getAccountCharacterCount(Integer accountid) {
      lgnRLock.lock();
      try {
         return accountCharacterCount.get(accountid);
      } finally {
         lgnRLock.unlock();
      }
   }

   public short getAccountWorldCharacterCount(Integer accountid, Integer worldid) {
      lgnRLock.lock();
      try {
         short count = 0;

         for (Integer chr : accountChars.get(accountid)) {
            if (worldChars.get(chr).equals(worldid)) {
               count++;
            }
         }

         return count;
      } finally {
         lgnRLock.unlock();
      }
   }

   private Set<Integer> getAccountCharacterEntries(Integer accountid) {
      lgnRLock.lock();
      try {
         return new HashSet<>(accountChars.get(accountid));
      } finally {
         lgnRLock.unlock();
      }
   }

   public void updateCharacterEntry(MapleCharacter chr) {
      MapleCharacter chrView = chr.generateCharacterEntry();

      lgnWLock.lock();
      try {
         World wserv = this.getWorld(chrView.getWorld());
         if (wserv != null) {
            wserv.registerAccountCharacterView(chrView.getAccountID(), chrView);
         }
      } finally {
         lgnWLock.unlock();
      }
   }

   public void createCharacterEntry(MapleCharacter chr) {
      Integer accountid = chr.getAccountID(), chrid = chr.getId(), world = chr.getWorld();

      lgnWLock.lock();
      try {
         accountCharacterCount.put(accountid, (short) (accountCharacterCount.get(accountid) + 1));

         Set<Integer> accChars = accountChars.get(accountid);
         accChars.add(chrid);

         worldChars.put(chrid, world);

         MapleCharacter chrView = chr.generateCharacterEntry();

         World wserv = this.getWorld(chrView.getWorld());
         if (wserv != null) {
            wserv.registerAccountCharacterView(chrView.getAccountID(), chrView);
         }
      } finally {
         lgnWLock.unlock();
      }
   }
    
    /*
    public void deleteAccountEntry(Integer accountid) { is this even a thing?
        lgnWLock.lock();
        try {
            accountCharacterCount.remove(accountid);
            accountChars.remove(accountid);
        } finally {
            lgnWLock.unlock();
        }
    }
    */

   public void deleteCharacterEntry(Integer accountid, Integer chrid) {
      lgnWLock.lock();
      try {
         accountCharacterCount.put(accountid, (short) (accountCharacterCount.get(accountid) - 1));

         Set<Integer> accChars = accountChars.get(accountid);
         accChars.remove(chrid);

         Integer world = worldChars.remove(chrid);
         if (world != null) {
            World wserv = this.getWorld(world);
            if (wserv != null) {
               wserv.unregisterAccountCharacterView(accountid, chrid);
            }
         }
      } finally {
         lgnWLock.unlock();
      }
   }

   public void transferWorldCharacterEntry(MapleCharacter chr, Integer toWorld) { // used before setting the new worldid on the character object
      lgnWLock.lock();
      try {
         Integer chrid = chr.getId(), accountid = chr.getAccountID(), world = worldChars.get(chr.getId());
         if (world != null) {
            World wserv = this.getWorld(world);
            if (wserv != null) {
               wserv.unregisterAccountCharacterView(accountid, chrid);
            }
         }

         worldChars.put(chrid, toWorld);

         MapleCharacter chrView = chr.generateCharacterEntry();

         World wserv = this.getWorld(toWorld);
         if (wserv != null) {
            wserv.registerAccountCharacterView(chrView.getAccountID(), chrView);
         }
      } finally {
         lgnWLock.unlock();
      }
   }

   public Pair<Pair<Integer, List<MapleCharacter>>, List<Pair<Integer, List<MapleCharacter>>>> loadAccountCharlist(Integer accountId, int visibleWorlds) {
      List<World> wlist = this.getWorlds();
      if (wlist.size() > visibleWorlds) {
         wlist = wlist.subList(0, visibleWorlds);
      }

      List<Pair<Integer, List<MapleCharacter>>> accChars = new ArrayList<>(wlist.size() + 1);
      int chrTotal = 0;
      List<MapleCharacter> lastwchars = null;

      lgnRLock.lock();
      try {
         for (World w : wlist) {
            List<MapleCharacter> wchars = w.getAccountCharactersView(accountId);
            if (wchars == null) {
               if (!accountChars.containsKey(accountId)) {
                  accountCharacterCount.put(accountId, (short) 0);
                  accountChars.put(accountId, new HashSet<>());    // not advisable at all to write on the map on a read-protected environment
               }                                                           // yet it's known there's no problem since no other point in the source does
            } else if (!wchars.isEmpty()) {                                  // this action.
               lastwchars = wchars;

               accChars.add(new Pair<>(w.getId(), wchars));
               chrTotal += wchars.size();
            }
         }
      } finally {
         lgnRLock.unlock();
      }

      return new Pair<>(new Pair<>(chrTotal, lastwchars), accChars);
   }

   public void loadAllAccountsCharactersView() {
      DatabaseConnection.getInstance().withConnection(connection -> AccountProvider.getInstance().getAllAccountIds(connection)
            .forEach(accountId -> {
               if (isFirstAccountLogin(accountId)) {
                  loadAccountCharactersView(accountId, 0, 0);
               }
            }));
   }

   private boolean isFirstAccountLogin(Integer accId) {
      lgnRLock.lock();
      try {
         return !accountChars.containsKey(accId);
      } finally {
         lgnRLock.unlock();
      }
   }

   private static void applyAllNameChanges() {
      List<Pair<String, String>> changedNames = new LinkedList<>(); //logging only
      DatabaseConnection.getInstance().withConnection(connection ->
            NameChangeProvider.getInstance().getPendingNameChanges(connection).forEach(result -> {
               CharacterAdministrator.getInstance().performNameChange(connection, result.characterId(), result.oldName(), result.newName(), result.id());
               changedNames.add(new Pair<>(result.oldName(), result.newName()));
            }));
      for (Pair<String, String> namePair : changedNames) {
         FilePrinter.print(FilePrinter.CHANGE_CHARACTER_NAME, "Name change applied : from \"" + namePair.getLeft() + "\" to \"" + namePair.getRight() + "\" at " + Calendar.getInstance().getTime().toString());
      }
   }

   private static void applyAllWorldTransfers() {
      List<Pair<Integer, Pair<Integer, Integer>>> worldTransfers = new LinkedList<>(); //logging only <charid, <oldWorld, newWorld>>
      DatabaseConnection.getInstance().withConnection(connection ->
            WorldTransferProvider.getInstance().getPendingTransfers(connection).forEach(result -> {
               String reason = CharacterProcessor.getInstance().checkWorldTransferEligibility(connection, result.characterId(), result.from(), result.to());
               if (reason != null) {
                  WorldTransferAdministrator.getInstance().cancelById(connection, result.id());
                  FilePrinter.print(FilePrinter.WORLD_TRANSFER, "World transfer cancelled : Character ID " + result.characterId() + " at " + Calendar.getInstance().getTime().toString() + ", Reason : " + reason);
               } else {
                  CharacterAdministrator.getInstance().performWorldTransfer(connection, result.characterId(), result.from(), result.to(), result.id());

                  worldTransfers.add(new Pair<>(result.characterId(), new Pair<>(result.from(), result.to())));
               }
            }));
      //log
      for (Pair<Integer, Pair<Integer, Integer>> worldTransferPair : worldTransfers) {
         int charId = worldTransferPair.getLeft();
         int oldWorld = worldTransferPair.getRight().getLeft();
         int newWorld = worldTransferPair.getRight().getRight();
         FilePrinter.print(FilePrinter.WORLD_TRANSFER, "World transfer applied : Character ID " + charId + " from World " + oldWorld + " to World " + newWorld + " at " + Calendar.getInstance().getTime().toString());
      }
   }

   public void loadAccountCharacters(MapleClient c) {
      Integer accId = c.getAccID();
      if (!isFirstAccountLogin(accId)) {
         Set<Integer> accWorlds = new HashSet<>();

         lgnRLock.lock();
         try {
            for (Integer chrid : getAccountCharacterEntries(accId)) {
               accWorlds.add(worldChars.get(chrid));
            }
         } finally {
            lgnRLock.unlock();
         }

         int gmLevel = 0;
         for (Integer aw : accWorlds) {
            World wserv = this.getWorld(aw);

            if (wserv != null) {
               for (MapleCharacter chr : wserv.getAllCharactersView()) {
                  if (gmLevel < chr.gmLevel()) {
                     gmLevel = chr.gmLevel();
                  }
               }
            }
         }

         c.setGMLevel(gmLevel);
         return;
      }

      int gmLevel = loadAccountCharactersView(c.getAccID(), 0, 0);
      c.setGMLevel(gmLevel);
   }

   private int loadAccountCharactersView(Integer accId, int gmLevel, int fromWorldid) {    // returns the maximum gmLevel found
      List<World> wlist = this.getWorlds();
      Pair<Short, List<List<MapleCharacter>>> accCharacters = loadAccountCharactersViewFromDb(accId, wlist.size());

      lgnWLock.lock();
      try {
         List<List<MapleCharacter>> accChars = accCharacters.getRight();
         accountCharacterCount.put(accId, accCharacters.getLeft());

         Set<Integer> chars = accountChars.get(accId);
         if (chars == null) {
            chars = new HashSet<>(5);
         }

         for (int wid = fromWorldid; wid < wlist.size(); wid++) {
            World w = wlist.get(wid);
            List<MapleCharacter> wchars = accChars.get(wid);
            w.loadAccountCharactersView(accId, wchars);

            for (MapleCharacter chr : wchars) {
               int cid = chr.getId();
               if (gmLevel < chr.gmLevel()) {
                  gmLevel = chr.gmLevel();
               }

               chars.add(cid);
               worldChars.put(cid, wid);
            }
         }

         accountChars.put(accId, chars);
      } finally {
         lgnWLock.unlock();
      }

      return gmLevel;
   }

   public void setCharacteridInTransition(IoSession session, int charId) {
      String remoteIp = getRemoteIp(session);

      lgnWLock.lock();
      try {
         transitioningChars.put(remoteIp, charId);
      } finally {
         lgnWLock.unlock();
      }
   }

   public boolean validateCharacteridInTransition(IoSession session, int charId) {
      if (!ServerConstants.USE_IP_VALIDATION) {
         return true;
      }

      String remoteIp = getRemoteIp(session);

      lgnWLock.lock();
      try {
         Integer cid = transitioningChars.remove(remoteIp);
         return cid != null && cid.equals(charId);
      } finally {
         lgnWLock.unlock();
      }
   }

   public Integer freeCharacteridInTransition(IoSession session) {
      if (!ServerConstants.USE_IP_VALIDATION) {
         return null;
      }

      String remoteIp = getRemoteIp(session);

      lgnWLock.lock();
      try {
         return transitioningChars.remove(remoteIp);
      } finally {
         lgnWLock.unlock();
      }
   }

   public boolean hasCharacteridInTransition(IoSession session) {
      if (!ServerConstants.USE_IP_VALIDATION) {
         return true;
      }

      String remoteIp = getRemoteIp(session);

      lgnRLock.lock();
      try {
         return transitioningChars.containsKey(remoteIp);
      } finally {
         lgnRLock.unlock();
      }
   }

   public void registerLoginState(MapleClient c) {
      srvLock.lock();
      try {
         inLoginState.put(c, System.currentTimeMillis() + 600000);
      } finally {
         srvLock.unlock();
      }
   }

   public void unregisterLoginState(MapleClient c) {
      srvLock.lock();
      try {
         inLoginState.remove(c);
      } finally {
         srvLock.unlock();
      }
   }

   private void disconnectIdlesOnLoginState() {
      List<MapleClient> toDisconnect = new LinkedList<>();

      srvLock.lock();
      try {
         long timeNow = System.currentTimeMillis();

         for (Entry<MapleClient, Long> mc : inLoginState.entrySet()) {
            if (timeNow > mc.getValue()) {
               toDisconnect.add(mc.getKey());
            }
         }

         for (MapleClient c : toDisconnect) {
            inLoginState.remove(c);
         }
      } finally {
         srvLock.unlock();
      }

      for (MapleClient c : toDisconnect) {    // thanks Lei for pointing a deadlock issue with srvLock
         if (c.isLoggedIn()) {
            c.disconnect(false, false);
         } else {
            MapleSessionCoordinator.getInstance().closeSession(c.getSession(), true);
         }
      }
   }

   private void disconnectIdlesOnLoginTask() {
      TimerManager.getInstance().register(this::disconnectIdlesOnLoginState, 300000);
   }

   public final Runnable shutdown(final boolean restart) {//no player should be online when trying to shutdown!
      return new Runnable() {
         @Override
         public void run() {
            shutdownInternal(restart);
         }
      };
   }

   private synchronized void shutdownInternal(boolean restart) {
      System.out.println((restart ? "Restarting" : "Shutting down") + " the server!\r\n");
      if (getWorlds() == null) {
         return;//already shutdown
      }
      for (World w : getWorlds()) {
         w.shutdown();
      }

        /*for (World w : getWorlds()) {
            while (w.getPlayerStorage().getAllCharacters().size() > 0) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ie) {
                    System.err.println("FUCK MY LIFE");
                }
            }
        }
        for (Channel ch : getAllChannels()) {
            while (ch.getConnectedClients() > 0) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ie) {
                    System.err.println("FUCK MY LIFE");
                }
            }
        }*/

      List<Channel> allChannels = getAllChannels();

      if (ServerConstants.USE_THREAD_TRACKER) {
         ThreadTracker.getInstance().cancelThreadTrackerTask();
      }

      for (Channel ch : allChannels) {
         while (!ch.finishedShutdown()) {
            try {
               Thread.sleep(1000);
            } catch (InterruptedException ie) {
               ie.printStackTrace();
               System.err.println("FUCK MY LIFE");
            }
         }
      }

      ThreadManager.getInstance().stop();
      TimerManager.getInstance().purge();
      TimerManager.getInstance().stop();

      resetServerWorlds();

      System.out.println("Worlds + Channels are offline.");
      acceptor.unbind();
      acceptor = null;
      if (!restart) {  // shutdown hook deadlocks if System.exit() method is used within its body chores, thanks MIKE for pointing that out
         new Thread(new Runnable() {
            @Override
            public void run() {
               System.exit(0);
            }
         }).start();
      } else {
         System.out.println("\r\nRestarting the server....\r\n");
         try {
            instance.finalize();//FUU I CAN AND IT'S FREE
         } catch (Throwable ex) {
            ex.printStackTrace();
         }
         instance = null;
         System.gc();
         getInstance().init();//DID I DO EVERYTHING?! D:
      }
   }
}
