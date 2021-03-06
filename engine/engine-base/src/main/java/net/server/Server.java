package net.server;

import client.MapleCharacter;
import client.MapleClient;
import client.SkillFactory;
import client.command.CommandsExecutor;
import client.database.data.CharacterData;
import client.database.data.WorldRankData;
import client.inventory.Item;
import client.inventory.ItemFactory;
import client.inventory.manipulator.MapleCashIdGenerator;
import client.newyear.NewYearCardRecord;
import client.processor.CharacterProcessor;
import client.processor.MapleFamilyProcessor;
import client.processor.NewYearCardProcessor;
import com.ms.logs.LogType;
import com.ms.logs.LoggerOriginator;
import com.ms.logs.LoggerUtil;
import com.ms.shared.rest.RestService;
import com.ms.shared.rest.ServerFactory;
import com.ms.shared.rest.UriBuilder;
import config.YamlConfig;
import constants.ItemConstants;
import constants.ServerConstants;
import constants.game.GameConstants;
import constants.net.OpcodeConstants;
import database.DatabaseConnection;
import database.PersistenceManager;
import database.administrator.*;
import database.provider.*;
import net.MapleServerHandler;
import net.mina.MapleCodecFactory;
import net.server.audit.ThreadTracker;
import net.server.audit.locks.MonitoredLockType;
import net.server.audit.locks.MonitoredReadLock;
import net.server.audit.locks.MonitoredReentrantReadWriteLock;
import net.server.audit.locks.MonitoredWriteLock;
import net.server.audit.locks.factory.MonitoredReadLockFactory;
import net.server.audit.locks.factory.MonitoredReentrantLockFactory;
import net.server.audit.locks.factory.MonitoredWriteLockFactory;
import net.server.channel.Channel;
import net.server.coordinator.session.MapleSessionCoordinator;
import net.server.guild.MapleAlliance;
import net.server.guild.MapleGuild;
import net.server.processor.MapleGuildProcessor;
import net.server.task.*;
import net.server.world.World;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.buffer.SimpleBufferAllocator;
import org.apache.mina.core.service.IoAcceptor;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;
import org.glassfish.grizzly.http.server.HttpServer;
import server.CashShop.CashItemFactory;
import server.MapleSkillBookInformationProvider;
import server.ThreadManager;
import server.TimerManager;
import server.WorldRecommendation;
import server.expeditions.MapleExpeditionBossLog;
import server.life.MaplePlayerNPCFactory;
import server.processor.QuestProcessor;
import tools.*;
import tools.packet.PacketInput;

import javax.persistence.EntityManager;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.security.Security;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Lock;
import java.util.stream.Collectors;

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
    private final Map<Integer, NewYearCardRecord> newYearCardRecords = new HashMap<>();
    private final List<MapleClient> processDiseaseAnnouncePlayers = new LinkedList<>();
    private final List<MapleClient> registeredDiseaseAnnouncePlayers = new LinkedList<>();
    private final List<WorldRankData> playerRanking = new LinkedList<>();
    private final Lock srvLock = MonitoredReentrantLockFactory.createLock(MonitoredLockType.SERVER);
    private final Lock disLock = MonitoredReentrantLockFactory.createLock(MonitoredLockType.SERVER_DISEASES);
    private final MonitoredReentrantReadWriteLock wldLock = new MonitoredReentrantReadWriteLock(MonitoredLockType.SERVER_WORLDS, true);
    private final MonitoredReadLock wldRLock = MonitoredReadLockFactory.createLock(wldLock);
    private final MonitoredWriteLock wldWLock = MonitoredWriteLockFactory.createLock(wldLock);
    private final MonitoredReentrantReadWriteLock lgnLock = new MonitoredReentrantReadWriteLock(MonitoredLockType.SERVER_LOGIN, true);
    private final MonitoredReadLock lgnRLock = MonitoredReadLockFactory.createLock(lgnLock);
    private final MonitoredWriteLock lgnWLock = MonitoredWriteLockFactory.createLock(lgnLock);
    private final AtomicLong currentTime = new AtomicLong(0);
    private IoAcceptor acceptor;
    private List<Map<Integer, String>> channels = new LinkedList<>();
    private List<World> worlds = new ArrayList<>();
    private List<WorldRecommendation> worldRecommendedList = new LinkedList<>();
    private long serverCurrentTime = 0;
    private HttpServer server;

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

    public static void cleanNxCodeCoupons(EntityManager entityManager) {
        if (!YamlConfig.config.server.USE_CLEAR_OUTDATED_COUPONS) {
            return;
        }

        long timeClear = System.currentTimeMillis() - 14 * 24 * 60 * 60 * 1000;

        List<Integer> expiredCodes = NxCodeProvider.getInstance().getExpiredCodes(entityManager, timeClear);
        NxCodeItemAdministrator.getInstance().deleteItems(entityManager, expiredCodes);
        NxCodeAdministrator.getInstance().deleteExpired(entityManager, timeClear);
    }

    private List<WorldRankData> updatePlayerRankingFromDB(int worldId) {
        return DatabaseConnection.getInstance().withConnectionResult(connection -> {
            if (!YamlConfig.config.server.USE_WHOLE_SERVER_RANKING) {
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
        PersistenceManager.construct("ms-master");
        System.setProperty("wzpath", "wz");
        Security.setProperty("crypto.policy", "unlimited");
        AutoJCE.removeCryptographyRestrictions();
        Server.getInstance().init();
    }

    private static Pair<Short, List<List<MapleCharacter>>> loadAccountCharactersViewFromDb(int accountId, int worldCount) {
        short characterCount = 0;
        List<List<MapleCharacter>> worldCharacterList = new ArrayList<>(worldCount);
        for (int i = 0; i < worldCount; i++) {
            worldCharacterList.add(i, new LinkedList<>());
        }

        List<MapleCharacter> chars = new LinkedList<>();
        int curWorld = 0;
        List<Pair<Item, Integer>> accEquips = ItemFactory.loadEquippedItems(accountId, true, true);
        Map<Integer, List<Item>> accPlayerEquips = new HashMap<>();

        for (Pair<Item, Integer> ae : accEquips) {
            List<Item> playerEquips = accPlayerEquips.computeIfAbsent(ae.getRight(), k -> new LinkedList<>());
            playerEquips.add(ae.getLeft());
        }

        List<CharacterData> characterDataList = DatabaseConnection.getInstance().withConnectionResult(connection -> CharacterProvider.getInstance().getByAccountId(connection, accountId)).orElse(new ArrayList<>());
        for (CharacterData characterData : characterDataList) {
            characterCount++;

            int worldId = characterData.world();
            if (worldId >= worldCount) {
                continue;
            }

            if (worldId > curWorld) {
                worldCharacterList.add(curWorld, chars);

                curWorld = worldId;
                chars = new LinkedList<>();
            }

            chars.add(CharacterProcessor.getInstance().loadCharacterEntryFromDB(characterData, accPlayerEquips.get(characterData.id())));
        }

        worldCharacterList.add(curWorld, chars);

        return new Pair<>(characterCount, worldCharacterList);
    }

    public void loadAccountStorage(MapleClient client) {
        int accountId = client.getAccID();
        Set<Integer> accountWorlds = new HashSet<>();
        lgnWLock.lock();
        try {
            accountChars.get(accountId).stream()
                    .map(worldChars::get)
                    .filter(Objects::nonNull)
                    .forEach(accountWorlds::add);
        } finally {
            lgnWLock.unlock();
        }

        accountWorlds.stream()
                .filter(worldId -> worldId < getWorlds().size())
                .map(worldId -> getWorlds().get(worldId))
                .forEach(world -> world.registerAccountStorage(accountId));
    }

    private static String getRemoteHost(MapleClient client) {
        return MapleSessionCoordinator.getSessionRemoteAddress(client.getSession());
    }

    public int getCurrentTimestamp() {
        return (int) (Server.getInstance().getCurrentTime() - Server.uptime);
    }

    public long getCurrentTime() {  // returns a slightly delayed time value, under frequency of UPDATE_INTERVAL
        return serverCurrentTime;
    }

    public void updateCurrentTime() {
        serverCurrentTime = currentTime.addAndGet(YamlConfig.config.server.UPDATE_INTERVAL);
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

    public List<WorldRecommendation> worldRecommendedList() {
        return worldRecommendedList;
    }

    public void setNewYearCard(NewYearCardRecord nyc) {
        newYearCardRecords.put(nyc.id(), nyc);
    }

    public NewYearCardRecord getNewYearCard(int cardId) {
        return newYearCardRecords.get(cardId);
    }

    public NewYearCardRecord removeNewYearCard(int cardId) {
        return newYearCardRecords.remove(cardId);
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

    public Optional<World> getWorldById(int worldId) {
        wldRLock.lock();
        try {
            if (worldId < 0 || worldId >= worlds.size()) {
                return Optional.empty();
            }
            return Optional.of(worlds.get(worldId));
        } finally {
            wldRLock.unlock();
        }
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
            return getWorlds().stream()
                    .flatMap(world -> world.getChannels().stream())
                    .collect(Collectors.toList());
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
        wldRLock.lock();
        try {
            System.out.println(worlds);
            System.out.println(channels);
            System.out.println(worldRecommendedList);
            System.out.println();
            System.out.println("---------------------");
        } finally {
            wldRLock.unlock();
        }
    }

    public int addChannel(int worldId) {
        World world;
        Map<Integer, String> channelInfo;
        int channelId;

        wldRLock.lock();
        try {
            if (worldId >= worlds.size()) {
                return -3;
            }

            channelInfo = channels.get(worldId);
            if (channelInfo == null) {
                return -3;
            }

            channelId = channelInfo.size();
            if (channelId >= YamlConfig.config.server.CHANNEL_SIZE) {
                return -2;
            }

            channelId++;
            world = this.getWorld(worldId);
        } finally {
            wldWLock.unlock();
        }

        Channel channel = new Channel(worldId, channelId, getCurrentTime());
        channel.setServerMessage(YamlConfig.config.worlds.get(worldId).why_am_i_recommended);

        if (world.addChannel(channel)) {
            wldWLock.lock();
            try {
                channelInfo.put(channelId, channel.getIP());
            } finally {
                wldWLock.unlock();
            }
        }

        return channelId;
    }

    public int addWorld() {
        int newWorld = initWorld();
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

    private int initWorld() {
        int i;
        wldRLock.lock();
        try {
            i = worlds.size();

            if (i >= YamlConfig.config.server.WLDLIST_SIZE) {
                return -1;
            }

        } finally {
            wldRLock.unlock();
        }

        LoggerUtil.printInfo(LoggerOriginator.ENGINE, LogType.PROCESS, "Starting world " + i);

        int expRate = YamlConfig.config.worlds.get(i).exp_rate;
        int mesoRate = YamlConfig.config.worlds.get(i).meso_rate;
        int dropRate = YamlConfig.config.worlds.get(i).drop_rate;
        int bossDropRate = YamlConfig.config.worlds.get(i).boss_drop_rate;
        int questRate = YamlConfig.config.worlds.get(i).quest_rate;
        int travelRate = YamlConfig.config.worlds.get(i).travel_rate;
        int fishingRate = YamlConfig.config.worlds.get(i).fishing_rate;

        int flag = YamlConfig.config.worlds.get(i).flag;
        String event_message = YamlConfig.config.worlds.get(i).event_message;
        String why_am_i_recommended = YamlConfig.config.worlds.get(i).why_am_i_recommended;

        World world = new World(i,
                flag,
                event_message,
                expRate, dropRate, bossDropRate, mesoRate, questRate, travelRate, fishingRate);

        Map<Integer, String> channelInfo = new HashMap<>();
        long bootTime = getCurrentTime();
        for (int j = 1; j <= YamlConfig.config.worlds.get(i).channels; j++) {
            Channel channel = new Channel(i, j, bootTime);

            world.addChannel(channel);
            channelInfo.put(j, channel.getIP());
        }

        boolean canDeploy;

        wldWLock.lock();
        try {
            canDeploy = world.getId() == worlds.size();
            if (canDeploy) {
                worldRecommendedList.add(new WorldRecommendation(i, why_am_i_recommended));
                worlds.add(world);
                channels.add(i, channelInfo);
            }

        } finally {
            wldWLock.unlock();
        }

        if (canDeploy) {

            world.setServerMessage(YamlConfig.config.worlds.get(i).server_message);
            LoggerUtil.printInfo(LoggerOriginator.ENGINE, LogType.PROCESS, "Finished loading world " + i);

            return i;
        } else {
            LoggerUtil.printInfo(LoggerOriginator.ENGINE, LogType.PROCESS, "Could not load world " + i + "...");
            world.shutdown();
            return -2;
        }
    }

    public boolean removeChannel(int worldId) {
        World world;

        wldRLock.lock();
        try {
            if (worldId >= worlds.size()) {
                return false;
            }

            world = worlds.get(worldId);
        } finally {
            wldRLock.unlock();
        }

        if (world != null) {
            int channel = world.removeChannel();
            wldWLock.lock();
            try {

                Map<Integer, String> m = channels.get(worldId);
                if (m != null) {
                    m.remove(channel);
                }
            } finally {
                wldWLock.unlock();
            }

            return channel > -1;
        }

        return false;
    }

    public boolean removeWorld() {   //lol don't!
        World w;
        int worldId;

        wldRLock.lock();
        try {
            worldId = worlds.size() - 1;
            if (worldId < 0) {
                return false;
            }

            w = worlds.get(worldId);
        } finally {
            wldRLock.unlock();
        }

        if (w == null || !w.canUninstall()) {
            return false;
        }

        removeWorldPlayerRanking();
        w.shutdown();

        wldWLock.lock();
        try {
            if (worldId == worlds.size() - 1) {
                worlds.remove(worldId);
                channels.remove(worldId);
                worldRecommendedList.remove(worldId);
            }
        } finally {
            wldWLock.unlock();
        }

        return true;
    }

    private void resetServerWorlds() {
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

    private void loadCouponRates(EntityManager entityManager) {
        NxCouponProvider.getInstance().getCoupons(entityManager).forEach(coupon -> couponRates.put(coupon.couponId(), coupon.rate()));
    }

    public List<Integer> getActiveCoupons() {
        synchronized (activeCoupons) {
            return activeCoupons;
        }
    }

    public void commitActiveCoupons() {
        for (World world : getWorlds()) {
            for (MapleCharacter chr : world.getPlayerStorage().getAllCharacters()) {
                if (!chr.isLoggedIn()) {
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
            DatabaseConnection.getInstance().withConnection(session -> NxCouponProvider.getInstance().getActiveCoupons(session).forEach(coupon -> activeCoupons.add(coupon.couponId())));
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
            if (player != null && player.isLoggedInWorld()) {
                player.announceAbnormalStatuses();
                player.collectAbnormalStatuses();
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
            return playerRanking.get(!YamlConfig.config.server.USE_WHOLE_SERVER_RANKING ? worldId : 0);
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
        if (!YamlConfig.config.server.USE_WHOLE_SERVER_RANKING) {
            wldWLock.lock();
            try {
                if (playerRanking.size() < worlds.size()) {
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
        if (YamlConfig.config.server.USE_WHOLE_SERVER_RANKING) {
            playerRanking.add(new WorldRankData(0));
        }
        updateWorldPlayerRanking();
    }

    private void clearMissingPetsFromDb() {
        DatabaseConnection.getInstance().withConnection(connection -> {
            PetAdministrator.getInstance().removeMissingPetReferencesFromInventory(connection);
            PetAdministrator.getInstance().deleteMissingPets(connection);
        });
    }


    public void init() {
        LoggerUtil.printInfo(LoggerOriginator.ENGINE, LogType.PROCESS, "HeavenMS v" + ServerConstants.VERSION + " starting up.");

        if (YamlConfig.config.server.SHUTDOWNHOOK) {
            Runtime.getRuntime().addShutdownHook(new Thread(shutdown(false)));
        }

        TimeZone.setDefault(TimeZone.getTimeZone(YamlConfig.config.server.TIMEZONE));

        // Start webservice
        URI uri = UriBuilder.host(RestService.MASTER).uri();
        server = ServerFactory.create(uri);

        DatabaseConnection.getInstance().withConnection(connection -> {
            AccountAdministrator.getInstance().logoutAllAccounts(connection);
            CharacterAdministrator.getInstance().removeAllMerchants(connection);
            cleanNxCodeCoupons(connection);
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
        tMan.register(tMan.purge(), YamlConfig.config.server.PURGING_INTERVAL);//Purging ftw...
        disconnectIdlesOnLoginTask();

        long timeLeft = getTimeLeftForNextHour();
        tMan.register(new CharacterDiseaseTask(), YamlConfig.config.server.UPDATE_INTERVAL, YamlConfig.config.server.UPDATE_INTERVAL);
        tMan.register(new ReleaseLockTask(), 2 * 60 * 1000, 2 * 60 * 1000);
        tMan.register(new CouponTask(), YamlConfig.config.server.COUPON_INTERVAL, timeLeft);
        tMan.register(new RankingCommandTask(), 5 * 60 * 1000, 5 * 60 * 1000);
        tMan.register(new RankingLoginTask(), YamlConfig.config.server.RANKING_INTERVAL, timeLeft);
        tMan.register(new LoginCoordinatorTask(), 60 * 60 * 1000, timeLeft);
        tMan.register(new EventRecallCoordinatorTask(), 60 * 60 * 1000, timeLeft);
        tMan.register(new LoginStorageTask(), 2 * 60 * 1000, 2 * 60 * 1000);
        tMan.register(new DueyFredrickTask(), 60 * 60 * 1000, timeLeft);
        tMan.register(new InvitationTask(), 30 * 1000, 30 * 1000);
        tMan.register(new RespawnTask(), YamlConfig.config.server.RESPAWN_INTERVAL, YamlConfig.config.server.RESPAWN_INTERVAL);

        timeLeft = getTimeLeftForNextDay();
        MapleExpeditionBossLog.resetBossLogTable();
        tMan.register(new BossLogTask(), 24 * 60 * 60 * 1000, timeLeft);

        long timeToTake = System.currentTimeMillis();
        SkillFactory.loadAllSkills();
        LoggerUtil.printInfo(LoggerOriginator.ENGINE, LogType.PROCESS, "Skills loaded in " + ((System.currentTimeMillis() - timeToTake) / 1000.0) + " seconds");

        timeToTake = System.currentTimeMillis();

        CashItemFactory.getSpecialCashItems();
        LoggerUtil.printInfo(LoggerOriginator.ENGINE, LogType.PROCESS, "Items loaded in " + ((System.currentTimeMillis() - timeToTake) / 1000.0) + " seconds");

        NewYearCardProcessor.getInstance().startPendingNewYearCardRequests();

        if (YamlConfig.config.server.USE_THREAD_TRACKER) {
            ThreadTracker.getInstance().registerThreadTrackerTask();
        }

        try {
            int worldCount = Math.min(GameConstants.WORLD_NAMES.length, YamlConfig.config.server.WORLDS);

            for (int i = 0; i < worldCount; i++) {
                initWorld();
            }
            initWorldPlayerRanking();

            MaplePlayerNPCFactory.loadFactoryMetadata();
            loadPlayerNpcMapStepFromDb();
        } catch (Exception e) {
            LoggerUtil.printError(LoggerOriginator.ENGINE, LogType.EXCEPTION, "Syntax error in 'world.ini'.");
            System.exit(0);
        }

        if (YamlConfig.config.server.USE_FAMILY_SYSTEM) {
            timeToTake = System.currentTimeMillis();
            MapleFamilyProcessor.getInstance().loadAllFamilies();
            LoggerUtil.printInfo(LoggerOriginator.ENGINE, LogType.PROCESS, "Families loaded in " + ((System.currentTimeMillis() - timeToTake) / 1000.0) + " seconds.");
        }

        acceptor.getSessionConfig().setIdleTime(IdleStatus.BOTH_IDLE, 30);
        acceptor.setHandler(new MapleServerHandler());
        try {
            acceptor.bind(new InetSocketAddress(8484));
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        LoggerUtil.printInfo(LoggerOriginator.ENGINE, LogType.PROCESS, "Listening on port 8484.");
        LoggerUtil.printInfo(LoggerOriginator.ENGINE, LogType.PROCESS, "HeavenMS is now online.");
        online = true;

        MapleSkillBookInformationProvider.getInstance();
        OpcodeConstants.generateOpcodeNames();
        CommandsExecutor.getInstance();

        for (Channel ch : this.getAllChannels()) {
            ch.reloadEventScriptManager();
        }
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
                alliance.guilds().parallelStream().forEach(guildId -> MapleGuildProcessor.getInstance().setGuildAllianceId(guildId, id));
                alliances.remove(id);
            }
        }
    }

    public void allianceMessage(int id, PacketInput packetInput, int exception, int guildEx) {
        MapleAlliance alliance = alliances.get(id);
        if (alliance != null) {
            for (Integer gid : alliance.guilds()) {
                if (guildEx == gid) {
                    continue;
                }
                MapleGuild guild = guilds.get(gid);
                if (guild != null) {
                    MasterBroadcaster.getInstance().sendToGuild(guild, packetInput, false, exception);
                }
            }
        }
    }

    public void addGuildToAlliance(int aId, int guildId) {
        MapleAlliance alliance = alliances.get(aId);
        if (alliance != null) {
            alliances.put(aId, alliance.addGuild(guildId));
            MapleGuildProcessor.getInstance().setGuildAllianceId(guildId, aId);
        }
    }

    public void removeGuildFromAlliance(int aId, int guildId) {
        MapleAlliance alliance = alliances.get(aId);
        if (alliance != null) {
            alliances.put(aId, alliance.removeGuild(guildId));
            MapleGuildProcessor.getInstance().setGuildAllianceId(guildId, 0);
        }
    }

    public boolean setAllianceRanks(int aId, String[] ranks) {
        MapleAlliance alliance = alliances.get(aId);
        if (alliance != null) {
            alliances.put(aId, alliance.setRankTitles(ranks));
            return true;
        }
        return false;
    }

    public boolean setAllianceNotice(int aId, String notice) {
        MapleAlliance alliance = alliances.get(aId);
        if (alliance != null) {
            alliances.put(aId, alliance.setNotice(notice));
            return true;
        }
        return false;
    }

    public boolean increaseAllianceCapacity(int aId, int inc) {
        MapleAlliance alliance = alliances.get(aId);
        if (alliance != null) {
            alliances.put(aId, alliance.increaseCapacity(inc));
            return true;
        }
        return false;
    }

    public int createGuild(int leaderId, String name) {
        return MapleGuildProcessor.getInstance().createGuild(leaderId, name);
    }

    public Optional<MapleGuild> getGuildByName(String name) {
        synchronized (guilds) {
            return guilds.values().parallelStream()
                    .filter(guild -> guild.getName().equalsIgnoreCase(name))
                    .findFirst();
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

            g = MapleGuildProcessor.getInstance().createGuild(id, world);
            if (g.getId() < 1) {
                return Optional.empty();
            }

            if (mc != null) {
                g.findMember(mc.getId()).ifPresentOrElse(guildCharacter -> {
                    mc.setMGC(guildCharacter);
                    guildCharacter.setCharacter(mc);
                }, () -> LoggerUtil.printError(LoggerOriginator.ENGINE, LogType.GUILD_CHAR_ERROR, "Could not find " + mc.getName() + " when loading guild " + id + "."));

                MapleGuildProcessor.getInstance().setMemberOnline(g, mc.getId(), true, mc.getClient().getChannel());
            }

            guilds.put(id, g);
            return Optional.of(g);
        }
    }

    public Optional<MapleGuild> removeGuild(int guildId) {
        synchronized (guilds) {
            if (!guilds.containsKey(guildId)) {
                return Optional.empty();
            }

            MapleGuild guild = guilds.remove(guildId);
            return Optional.of(guild);
        }
    }

    public PlayerBuffStorage getPlayerBuffStorage() {
        return buffStorage;
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

    public void changeFly(Integer accountId, boolean canFly) {
        if (canFly) {
            activeFly.add(accountId);
        } else {
            activeFly.remove(accountId);
        }
    }

    public boolean canFly(Integer accountId) {
        return activeFly.contains(accountId);
    }

    public int getCharacterWorld(Integer characterId) {
        lgnRLock.lock();
        try {
            Integer worldId = worldChars.get(characterId);
            return worldId != null ? worldId : -1;
        } finally {
            lgnRLock.unlock();
        }
    }

    public boolean haveCharacterEntry(Integer accountId, Integer characterId) {
        lgnRLock.lock();
        try {
            Set<Integer> accChars = accountChars.get(accountId);
            return accChars.contains(characterId);
        } finally {
            lgnRLock.unlock();
        }
    }

    public short getAccountCharacterCount(Integer accountId) {
        lgnRLock.lock();
        try {
            return accountCharacterCount.get(accountId);
        } finally {
            lgnRLock.unlock();
        }
    }

    public short getAccountWorldCharacterCount(Integer accountId, Integer worldId) {
        lgnRLock.lock();
        try {
            short count = 0;

            for (Integer chr : accountChars.get(accountId)) {
                if (worldChars.get(chr).equals(worldId)) {
                    count++;
                }
            }

            return count;
        } finally {
            lgnRLock.unlock();
        }
    }

    private Set<Integer> getAccountCharacterEntries(Integer accountId) {
        lgnRLock.lock();
        try {
            return new HashSet<>(accountChars.get(accountId));
        } finally {
            lgnRLock.unlock();
        }
    }

    public void updateCharacterEntry(MapleCharacter chr) {
        MapleCharacter chrView = chr.generateCharacterEntry();

        lgnWLock.lock();
        try {
            World world = this.getWorld(chrView.getWorld());
            if (world != null) {
                world.registerAccountCharacterView(chrView.getAccountID(), chrView);
            }
        } finally {
            lgnWLock.unlock();
        }
    }

    public void createCharacterEntry(MapleCharacter chr) {
        Integer accountId = chr.getAccountID(), characterId = chr.getId(), worldId = chr.getWorld();

        lgnWLock.lock();
        try {
            accountCharacterCount.put(accountId, (short) (accountCharacterCount.get(accountId) + 1));

            Set<Integer> accChars = accountChars.get(accountId);
            accChars.add(characterId);

            worldChars.put(characterId, worldId);

            MapleCharacter chrView = chr.generateCharacterEntry();

            World world = this.getWorld(chrView.getWorld());
            if (world != null) {
                world.registerAccountCharacterView(chrView.getAccountID(), chrView);
            }
        } finally {
            lgnWLock.unlock();
        }
    }
    
    /*
    public void deleteAccountEntry(Integer accountId) { is this even a thing?
        lgnWLock.lock();
        try {
            accountCharacterCount.remove(accountId);
            accountChars.remove(accountId);
        } finally {
            lgnWLock.unlock();
        }
    }
    */

    public void deleteCharacterEntry(Integer accountId, Integer characterId) {
        lgnWLock.lock();
        try {
            accountCharacterCount.put(accountId, (short) (accountCharacterCount.get(accountId) - 1));

            Set<Integer> accChars = accountChars.get(accountId);
            accChars.remove(characterId);

            Integer worldId = worldChars.remove(characterId);
            if (worldId != null) {
                World world = this.getWorld(worldId);
                if (world != null) {
                    world.unregisterAccountCharacterView(accountId, characterId);
                }
            }
        } finally {
            lgnWLock.unlock();
        }
    }

    public void transferWorldCharacterEntry(MapleCharacter chr, Integer toWorldId) { // used before setting the new world id on the character object
        lgnWLock.lock();
        try {
            Integer characterId = chr.getId(), accountId = chr.getAccountID(), worldId = worldChars.get(chr.getId());
            if (worldId != null) {
                World world = this.getWorld(worldId);
                if (world != null) {
                    world.unregisterAccountCharacterView(accountId, characterId);
                }
            }

            worldChars.put(characterId, toWorldId);

            MapleCharacter chrView = chr.generateCharacterEntry();

            World world = this.getWorld(toWorldId);
            if (world != null) {
                world.registerAccountCharacterView(chrView.getAccountID(), chrView);
            }
        } finally {
            lgnWLock.unlock();
        }
    }

    public Pair<Pair<Integer, List<MapleCharacter>>, List<Pair<Integer, List<MapleCharacter>>>> loadAccountCharacterList(Integer accountId, int visibleWorlds) {
        List<World> worlds = this.getWorlds();
        if (worlds.size() > visibleWorlds) {
            worlds = worlds.subList(0, visibleWorlds);
        }

        List<Pair<Integer, List<MapleCharacter>>> accChars = new ArrayList<>(worlds.size() + 1);
        int chrTotal = 0;
        List<MapleCharacter> lastWorldCharacters = null;

        lgnRLock.lock();
        try {
            for (World w : worlds) {
                List<MapleCharacter> worldCharacters = w.getAccountCharactersView(accountId);
                if (worldCharacters == null) {
                    if (!accountChars.containsKey(accountId)) {
                        accountCharacterCount.put(accountId, (short) 0);
                        accountChars.put(accountId, new HashSet<>());    // not advisable at all to write on the map on a read-protected environment
                    }                                                           // yet it's known there's no problem since no other point in the source does
                } else if (!worldCharacters.isEmpty()) {                                  // this action.
                    lastWorldCharacters = worldCharacters;

                    accChars.add(new Pair<>(w.getId(), worldCharacters));
                    chrTotal += worldCharacters.size();
                }
            }
        } finally {
            lgnRLock.unlock();
        }

        return new Pair<>(new Pair<>(chrTotal, lastWorldCharacters), accChars);
    }

    public void loadAllAccountsCharactersView() {
        DatabaseConnection.getInstance().withConnection(entityManager -> AccountProvider.getInstance().getAllAccountIds(entityManager).forEach(id -> {
            if (isFirstAccountLogin(id)) {
                loadAccountCharactersView(id, 0, 0);
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
            LoggerUtil.printInfo(LoggerOriginator.ENGINE, LogType.CHANGE_CHARACTER_NAME, "Name change applied : from \"" + namePair.getLeft() + "\" to \"" + namePair.getRight() + "\" at " + Calendar.getInstance().getTime().toString());
        }
    }

    private static void applyAllWorldTransfers() {
        List<Pair<Integer, Pair<Integer, Integer>>> worldTransfers = new LinkedList<>();
        DatabaseConnection.getInstance().withConnection(connection ->
                WorldTransferProvider.getInstance().getPendingTransfers(connection).forEach(result -> {
                    String reason = CharacterProcessor.getInstance().checkWorldTransferEligibility(connection, result.characterId(), result.from(), result.to());
                    if (reason != null) {
                        WorldTransferAdministrator.getInstance().cancelById(connection, result.id());
                        LoggerUtil.printInfo(LoggerOriginator.ENGINE, LogType.WORLD_TRANSFER, "World transfer cancelled : Character ID " + result.characterId() + " at " + Calendar.getInstance().getTime().toString() + ", Reason : " + reason);
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
            LoggerUtil.printInfo(LoggerOriginator.ENGINE, LogType.WORLD_TRANSFER, "World transfer applied : Character ID " + charId + " from World " + oldWorld + " to World " + newWorld + " at " + Calendar.getInstance().getTime().toString());
        }
    }

    public void loadAccountCharacters(MapleClient client) {
        Integer accId = client.getAccID();
        if (!isFirstAccountLogin(accId)) {
            Set<Integer> accountWorlds;

            lgnRLock.lock();
            try {
                accountWorlds = getAccountCharacterEntries(accId).stream().map(worldChars::get).collect(Collectors.toSet());
            } finally {
                lgnRLock.unlock();
            }

            int gmLevel = 0;
            for (Integer accountWorld : accountWorlds) {
                World world = this.getWorld(accountWorld);

                if (world != null) {
                    for (MapleCharacter chr : world.getAllCharactersView()) {
                        if (gmLevel < chr.gmLevel()) {
                            gmLevel = chr.gmLevel();
                        }
                    }
                }
            }

            client.setGMLevel(gmLevel);
            return;
        }

        int gmLevel = loadAccountCharactersView(client.getAccID(), 0, 0);
        client.setGMLevel(gmLevel);
    }

    private int loadAccountCharactersView(Integer accId, int gmLevel, int fromWorldId) {    // returns the maximum gmLevel found
        List<World> worlds = this.getWorlds();
        Pair<Short, List<List<MapleCharacter>>> accCharacters = loadAccountCharactersViewFromDb(accId, worlds.size());

        lgnWLock.lock();
        try {
            List<List<MapleCharacter>> accChars = accCharacters.getRight();
            accountCharacterCount.put(accId, accCharacters.getLeft());

            Set<Integer> chars = accountChars.get(accId);
            if (chars == null) {
                chars = new HashSet<>(5);
            }

            for (int worldId = fromWorldId; worldId < worlds.size(); worldId++) {
                World world = worlds.get(worldId);
                List<MapleCharacter> worldCharacters = accChars.get(worldId);
                world.loadAccountCharactersView(accId, worldCharacters);

                for (MapleCharacter chr : worldCharacters) {
                    int cid = chr.getId();
                    if (gmLevel < chr.gmLevel()) {
                        gmLevel = chr.gmLevel();
                    }

                    chars.add(cid);
                    worldChars.put(cid, worldId);
                }
            }

            accountChars.put(accId, chars);
        } finally {
            lgnWLock.unlock();
        }

        return gmLevel;
    }

    public void setCharacterIdInTransition(MapleClient client, int charId) {
        String remoteIp = getRemoteHost(client);

        lgnWLock.lock();
        try {
            transitioningChars.put(remoteIp, charId);
        } finally {
            lgnWLock.unlock();
        }
    }

    public boolean validateCharacterIdInTransition(MapleClient client, int charId) {
        if (!YamlConfig.config.server.USE_IP_VALIDATION) {
            return true;
        }

        String remoteIp = getRemoteHost(client);

        lgnWLock.lock();
        try {
            Integer cid = transitioningChars.remove(remoteIp);
            return cid != null && cid.equals(charId);
        } finally {
            lgnWLock.unlock();
        }
    }

    public Integer freeCharacterIdInTransition(MapleClient client) {
        if (!YamlConfig.config.server.USE_IP_VALIDATION) {
            return null;
        }

        String remoteIp = getRemoteHost(client);

        lgnWLock.lock();
        try {
            return transitioningChars.remove(remoteIp);
        } finally {
            lgnWLock.unlock();
        }
    }

    public boolean hasCharacterIdInTransition(MapleClient client) {
        if (!YamlConfig.config.server.USE_IP_VALIDATION) {
            return true;
        }

        String remoteIp = getRemoteHost(client);

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

        for (MapleClient c : toDisconnect) {
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
        return () -> shutdownInternal(restart);
    }

    private synchronized void shutdownInternal(boolean restart) {
        LoggerUtil.printInfo(LoggerOriginator.ENGINE, LogType.PROCESS, (restart ? "Restarting" : "Shutting down") + " the server!");
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

        if (YamlConfig.config.server.USE_THREAD_TRACKER) {
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

        resetServerWorlds();

        ThreadManager.getInstance().stop();
        TimerManager.getInstance().purge();
        TimerManager.getInstance().stop();

        LoggerUtil.printInfo(LoggerOriginator.ENGINE, LogType.PROCESS, "Worlds + Channels are offline.");
        acceptor.unbind();
        acceptor = null;

        if (!restart) {
            server.shutdownNow();
            new Thread(() -> System.exit(0)).start();
        } else {
            LoggerUtil.printInfo(LoggerOriginator.ENGINE, LogType.PROCESS, "Restarting the server....");
            try {
                instance.finalize();
            } catch (Throwable ex) {
                ex.printStackTrace();
            }
            instance = null;
            System.gc();
            getInstance().init();
        }
    }
}
