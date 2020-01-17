package client;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Lock;
import java.util.stream.Collectors;
import javax.script.ScriptEngine;

import org.apache.mina.core.session.IoSession;

import client.database.data.AccountData;
import client.database.data.AccountLoginData;
import client.database.data.CharNameAndIdData;
import client.inventory.MapleInventoryType;
import client.processor.BuddyListProcessor;
import client.processor.CharacterProcessor;
import config.YamlConfig;
import constants.game.GameConstants;
import database.DatabaseConnection;
import database.administrator.AccountAdministrator;
import database.administrator.HwidBanAdministrator;
import database.administrator.MacBanAdministrator;
import database.provider.AccountProvider;
import database.provider.CharacterProvider;
import database.provider.HwidBanProvider;
import database.provider.IpBanProvider;
import database.provider.MacBanProvider;
import database.provider.MacFilterProvider;
import net.server.Server;
import net.server.audit.locks.MonitoredLockType;
import net.server.audit.locks.factory.MonitoredReentrantLockFactory;
import net.server.channel.Channel;
import net.server.coordinator.login.MapleLoginBypassCoordinator;
import net.server.coordinator.session.MapleSessionCoordinator;
import net.server.coordinator.session.MapleSessionCoordinator.AntiMultiClientResult;
import net.server.guild.MapleGuildCharacter;
import net.server.processor.MapleGuildProcessor;
import net.server.processor.MaplePartyProcessor;
import net.server.world.MapleMessenger;
import net.server.world.MapleMessengerCharacter;
import net.server.world.MaplePartyCharacter;
import net.server.world.PartyOperation;
import net.server.world.World;
import scripting.AbstractPlayerInteraction;
import scripting.event.EventInstanceManager;
import scripting.event.EventManager;
import scripting.npc.NPCConversationManager;
import scripting.npc.NPCScriptManager;
import scripting.quest.QuestActionManager;
import scripting.quest.QuestScriptManager;
import server.ThreadManager;
import server.life.MapleMonster;
import server.maps.FieldLimit;
import server.maps.MapleMap;
import server.maps.MapleMiniDungeonInfo;
import tools.BCrypt;
import tools.FilePrinter;
import tools.HexTool;
import tools.LogHelper;
import tools.MapleAESOFB;
import tools.MessageBroadcaster;
import tools.PacketCreator;
import tools.ServerNoticeType;
import tools.I18nMessage;
import tools.packet.ChangeChannel;
import tools.packet.cashshop.ShowCash;
import tools.packet.foreigneffect.ShowHint;
import tools.packet.guild.ShowGuildInfo;
import tools.packet.message.ServerMessage;
import tools.packet.stat.EnableActions;

public class MapleClient {

   public static final int LOGIN_NOT_LOGGED_IN = 0;
   public static final int LOGIN_SERVER_TRANSITION = 1;
   public static final int LOGIN_LOGGED_IN = 2;
   public static final String CLIENT_KEY = "CLIENT";
   public static final String CLIENT_HWID = "HWID";
   public static final String CLIENT_NIBBLE_HWID = "HWID2";
   public static final String CLIENT_REMOTE_ADDRESS = "REMOTE_IP";
   public static final String CLIENT_TRANSITION = "TRANSITION";
   private static final int lockCount = 200;
   private static final Lock[] loginLocks = new Lock[lockCount];
   private Calendar tempBanCalendar;

   static {
      for (int i = 0; i < lockCount; i++) {
         loginLocks[i] = MonitoredReentrantLockFactory.createLock(MonitoredLockType.CLIENT_LOGIN, true);
      }
   }

   private final IoSession session;
   private final Semaphore actionsSemaphore = new Semaphore(7);
   private final Lock lock = MonitoredReentrantLockFactory.createLock(MonitoredLockType.CLIENT, true);
   private final Lock encoderLock = MonitoredReentrantLockFactory.createLock(MonitoredLockType.CLIENT_ENCODER, true);
   private final Lock announcerLock = MonitoredReentrantLockFactory.createLock(MonitoredLockType.CLIENT_ANNOUNCER, true);
   private MapleAESOFB send;
   private MapleAESOFB receive;
   private MapleCharacter player;
   private int channel = 1;
   private int accId = -4;
   private boolean loggedIn = false;
   private boolean serverTransition = false;
   private Calendar birthday = null;
   private String accountName = null;
   private int world;
   private long lastPong;
   private int gmLevel;
   private Set<String> macs = new HashSet<>();
   private Map<String, ScriptEngine> engines = new HashMap<>();
   private byte characterSlots = 3;
   private byte loginAttempt = 0;
   private String pin = "";
   private int pintAttempt = 0;
   private String pic = "";
   private int picAttempt = 0;
   private String hwid = null;
   private byte csAttempt = 0;
   private byte gender = -1;
   private boolean disconnecting = false;
   private int votePoints;
   private int voteTime = -1;
   private int visibleWorlds;
   private long lastNpcClick;
   private long sessionId;
   private long lastPacket = System.currentTimeMillis();
   private Locale locale;

   public MapleClient(MapleAESOFB send, MapleAESOFB receive, IoSession session) {
      this.send = send;
      this.receive = receive;
      this.session = session;
   }

   public void updateLastPacket() {
      lastPacket = System.currentTimeMillis();
   }

   public long getLastPacket() {
      return lastPacket;
   }

   private static boolean checkHash(String hash, String type, String password) {
      try {
         MessageDigest digester = MessageDigest.getInstance(type);
         digester.update(password.getBytes("UTF-8"), 0, password.length());
         return HexTool.toString(digester.digest()).replace(" ", "").toLowerCase().equals(hash);
      } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
         throw new RuntimeException("Encoding the string failed", e);
      }
   }

   public MapleAESOFB getReceiveCrypto() {
      return receive;
   }

   public MapleAESOFB getSendCrypto() {
      return send;
   }

   public IoSession getSession() {
      return session;
   }

   public EventManager getEventManager(String event) {
      return getChannelServer().getEventSM().getEventManager(event);
   }

   public MapleCharacter getPlayer() {
      return player;
   }

   public void setPlayer(MapleCharacter player) {
      this.player = player;
   }

   public AbstractPlayerInteraction getAbstractPlayerInteraction() {
      return new AbstractPlayerInteraction(this);
   }

   public List<MapleCharacter> loadCharacters(int serverId) {
      List<MapleCharacter> chars = new ArrayList<>(15);
      for (CharNameAndIdData cni : loadCharactersInternal(serverId)) {
         chars.add(CharacterProcessor.getInstance().loadCharFromDB(cni.id(), this, false));
      }
      return chars;
   }

   public List<String> loadCharacterNames(int worldId) {
      return loadCharactersInternal(worldId).stream().map(CharNameAndIdData::name).collect(Collectors.toList());
   }

   private List<CharNameAndIdData> loadCharactersInternal(int worldId) {
      return DatabaseConnection.getInstance().withConnectionResult(connection -> CharacterProvider.getInstance().getCharacterInfoForWorld(connection, accId, worldId)).orElse(new ArrayList<>());
   }

   public boolean isLoggedIn() {
      return loggedIn;
   }

   public boolean hasBannedIP() {
      return DatabaseConnection.getInstance().withConnectionResult(connection -> IpBanProvider.getInstance().getIpBanCount(connection, session.getRemoteAddress().toString())).orElse(0L) > 0;
   }

   public boolean hasBannedHWID() {
      if (hwid == null) {
         return false;
      }
      return DatabaseConnection.getInstance().withConnectionResult(connection -> HwidBanProvider.getInstance().getHwidBanCount(connection, hwid)).orElse(0L) > 1;
   }

   public boolean hasBannedMac() {
      if (macs.isEmpty()) {
         return false;
      }
      return DatabaseConnection.getInstance().withConnectionResult(connection -> MacBanProvider.getInstance().getMacBanCount(connection, macs)).orElse(0) > 1;
   }

   private void loadHWIDIfNecessary() {
      if (hwid == null) {
         hwid = DatabaseConnection.getInstance().withConnectionResult(entityManager -> AccountProvider.getInstance().getHwid(entityManager, accId)).orElseThrow();
      }
   }

   private void loadMacsIfNecessary() {
      if (macs.isEmpty()) {
         macs.addAll(DatabaseConnection.getInstance().withConnectionResult(connection -> AccountProvider.getInstance().getMacs(connection, accId)).orElseThrow());
      }
   }

   public void banHWID() {
      loadHWIDIfNecessary();
      DatabaseConnection.getInstance().withConnection(connection -> HwidBanAdministrator.getInstance().banHwid(connection, hwid));
   }

   public void banMacs() {
      loadMacsIfNecessary();

      DatabaseConnection.getInstance().withConnection(connection -> {
         List<String> filtered = MacFilterProvider.getInstance().getMacFilters(connection);
         MacBanAdministrator.getInstance().addMacBan(connection, accId, macs, filtered);
      });
   }

   public int finishLogin() {
      Lock loginLock = loginLocks[this.getAccID() % lockCount];
      loginLock.lock();
      try {
         if (getLoginState() > LOGIN_NOT_LOGGED_IN) { // 0 = LOGIN_NOT_LOGGED_IN, 1= LOGIN_SERVER_TRANSITION, 2 = LOGIN_LOGGED_IN
            loggedIn = false;
            return 7;
         }
         updateLoginState(LOGIN_LOGGED_IN);
      } finally {
         loginLock.unlock();
      }

      return 0;
   }

   public String getPin() {
      return pin;
   }

   public void setPin(String pin) {
      this.pin = pin;
      DatabaseConnection.getInstance().withConnection(connection -> AccountAdministrator.getInstance().setPin(connection, accId, pin));
   }

   public boolean checkPin(String other) {
      if (!(YamlConfig.config.server.ENABLE_PIN && cannotBypassPin())) {
         return true;
      }

      pintAttempt++;
      if (pintAttempt > 5) {
         MapleSessionCoordinator.getInstance().closeSession(session, false);
      }
      if (pin.equals(other)) {
         pintAttempt = 0;
         MapleLoginBypassCoordinator.getInstance().registerLoginBypassEntry(getNibbleHWID(), accId, false);
         return true;
      }
      return false;
   }

   public String getPic() {
      return pic;
   }

   public void setPic(String pic) {
      this.pic = pic;
      DatabaseConnection.getInstance().withConnection(connection -> AccountAdministrator.getInstance().setPic(connection, accId, pic));
   }

   public boolean checkPic(String other) {
      if (!(YamlConfig.config.server.ENABLE_PIC && cannotBypassPic())) {
         return true;
      }

      picAttempt++;
      if (picAttempt > 5) {
         MapleSessionCoordinator.getInstance().closeSession(session, false);
      }
      if (pic.equals(other)) {
         picAttempt = 0;
         MapleLoginBypassCoordinator.getInstance().registerLoginBypassEntry(getNibbleHWID(), accId, true);
         return true;
      }
      return false;
   }

   public int login(String login, String pwd, String nibbleHwid) {
      int loginOk = 5;

      loginAttempt++;
      if (loginAttempt > 4) {
         loggedIn = false;
         MapleSessionCoordinator.getInstance().closeSession(session, false);
         return 6;
      }

      Optional<AccountData> accountData = DatabaseConnection.getInstance().withConnectionResultOpt(connection -> AccountProvider.getInstance().getAccountDataByName(connection, login));
      if (accountData.isEmpty()) {
         accId = -2;
      } else {
         accId = accountData.get().id();
         if (accId <= 0) {
            FilePrinter.printError(FilePrinter.LOGIN_EXCEPTION, "Tried to login with account id " + accId);
            return 15;
         }

         gmLevel = 0;
         pin = accountData.get().pin();
         pic = accountData.get().pic();
         gender = accountData.get().gender().byteValue();
         characterSlots = accountData.get().characterSlots().byteValue();
         locale = new Locale(accountData.get().language(), accountData.get().country());
         String passwordHash = accountData.get().password();
         boolean tos = accountData.get().tos();

         if (accountData.get().banned()) {
            return 3;
         }

         if (getLoginState() > LOGIN_NOT_LOGGED_IN) {
            loggedIn = false;
            loginOk = 7;
         } else if (passwordHash.charAt(0) == '$' && passwordHash.charAt(1) == '2' && BCrypt.checkpw(pwd, passwordHash)) {
            loginOk = tos ? 23 : 0;
         } else if (pwd.equals(passwordHash) || checkHash(passwordHash, "SHA-1", pwd) || checkHash(passwordHash, "SHA-512", pwd)) {
            loginOk = tos ? (!YamlConfig.config.server.BCRYPT_MIGRATION ? 23 : -23) : (!YamlConfig.config.server.BCRYPT_MIGRATION ? 0 : -10);
         } else {
            loggedIn = false;
            loginOk = 4;
         }
      }

      if (loginOk == 0 || loginOk == 4) {
         AntiMultiClientResult res = MapleSessionCoordinator.getInstance().attemptLoginSession(session, nibbleHwid, accId, loginOk == 4);

         switch (res) {
            case SUCCESS:
               if (loginOk == 0) {
                  loginAttempt = 0;
               }
               return loginOk;
            case REMOTE_LOGGEDIN:
               return 17;
            case REMOTE_REACHED_LIMIT:
               return 13;
            case REMOTE_PROCESSING:
               return 10;
            case MANY_ACCOUNT_ATTEMPTS:
               return 16;
            default:
               return 8;
         }
      } else {
         return loginOk;
      }
   }

   public Calendar getTempBanCalendarFromDB() {
      Calendar result = DatabaseConnection.getInstance().withConnectionResult(connection -> AccountProvider.getInstance().getTempBanCalendar(connection, accId)).orElse(null);
      tempBanCalendar = result;
      return result;
   }

   public Calendar getTempBanCalendar() {
      return tempBanCalendar;
   }

   public boolean hasBeenBanned() {
      return tempBanCalendar != null;
   }

   public void updateHWID(String newHwid) {
      String[] split = newHwid.split("_");
      if (split.length > 1 && split[1].length() == 8) {
         StringBuilder hwid = new StringBuilder();
         String convert = split[1];

         int len = convert.length();
         for (int i = len - 2; i >= 0; i -= 2) {
            hwid.append(convert, i, i + 2);
         }
         hwid.insert(4, "-");

         this.hwid = hwid.toString();

         DatabaseConnection.getInstance().withConnection(connection -> AccountAdministrator.getInstance().setHwid(connection, accId, hwid.toString()));
      } else {
         this.disconnect(false, false); // Invalid HWID...
      }
   }

   public void updateMacs(String macData) {
      macs.addAll(Arrays.asList(macData.split(", ")));
      StringBuilder newMacData = new StringBuilder();
      Iterator<String> iter = macs.iterator();
      while (iter.hasNext()) {
         String cur = iter.next();
         newMacData.append(cur);
         if (iter.hasNext()) {
            newMacData.append(", ");
         }
      }

      DatabaseConnection.getInstance().withConnection(connection -> AccountAdministrator.getInstance().setMacs(connection, accId, newMacData.toString()));
   }

   public int getAccID() {
      return accId;
   }

   public void setAccID(int id) {
      this.accId = id;
   }

   public void updateLoginState(int newState) {
      // rules out possibility of multiple account entries
      if (newState == LOGIN_LOGGED_IN) {
         MapleSessionCoordinator.getInstance().updateOnlineSession(this.getSession());
      }

      DatabaseConnection.getInstance().withConnection(connection -> AccountAdministrator.getInstance().setLoggedInStatus(connection, accId, newState));

      if (newState == LOGIN_NOT_LOGGED_IN) {
         loggedIn = false;
         serverTransition = false;
         setAccID(0);
      } else {
         serverTransition = (newState == LOGIN_SERVER_TRANSITION);
         loggedIn = !serverTransition;
      }
   }

   public int getLoginState() {  // 0 = LOGIN_NOT_LOGGED_IN, 1= LOGIN_SERVER_TRANSITION, 2 = LOGIN_LOGGED_IN
      Optional<AccountLoginData> loginData = DatabaseConnection.getInstance().withConnectionResult(connection -> AccountProvider.getInstance().getLoginData(connection, accId));
      if (loginData.isEmpty()) {
         return LOGIN_NOT_LOGGED_IN;
      }

      birthday = Calendar.getInstance();
      birthday.setTime(loginData.get().birthday());

      int state = loginData.get().loggedIn();
      if (state == LOGIN_SERVER_TRANSITION) {
         if (loginData.get().lastLogin().getTime() + 30000 < Server.getInstance().getCurrentTime()) {
            int accountId = accId;
            state = LOGIN_NOT_LOGGED_IN;
            updateLoginState(LOGIN_NOT_LOGGED_IN);
            this.setAccID(accountId);
         }
      }

      if (state == LOGIN_LOGGED_IN) {
         loggedIn = true;
      } else if (state == LOGIN_SERVER_TRANSITION) {
         DatabaseConnection.getInstance().withConnection(connection -> AccountAdministrator.getInstance().setLoggedInStatus(connection, accId, 0));
      } else {
         loggedIn = false;
      }

      return state;
   }

   public boolean checkBirthDate(Calendar date) {
      return date.get(Calendar.YEAR) == birthday.get(Calendar.YEAR) && date.get(Calendar.MONTH) == birthday.get(Calendar.MONTH) && date.get(Calendar.DAY_OF_MONTH) == birthday.get(Calendar.DAY_OF_MONTH);
   }

   private void removePartyPlayer() {
      MapleMap map = player.getMap();
      final int idz = player.getId();

      player.getParty().ifPresent(party -> {
         final MaplePartyCharacter maplePartyCharacter = new MaplePartyCharacter(player);
         maplePartyCharacter.setOnline(false);
         MaplePartyProcessor.getInstance().updateParty(party, PartyOperation.LOG_ON_OFF, maplePartyCharacter);
         if (party.getLeader().getId() == idz && map != null) {
            MaplePartyCharacter partyCharacter = null;
            for (MaplePartyCharacter partyMember : party.getMembers()) {
               if (partyMember != null && partyMember.getId() != idz && (partyCharacter == null || partyCharacter.getLevel() <= partyMember.getLevel()) && map.getCharacterById(partyMember.getId()) != null) {
                  partyCharacter = partyMember;
               }
            }
            if (partyCharacter != null) {
               MaplePartyProcessor.getInstance().updateParty(party, PartyOperation.CHANGE_LEADER, partyCharacter);
            }
         }
      });
   }

   private void removePlayer(World world, boolean serverTransition) {
      try {
         player.setDisconnectedFromChannelWorld();
         player.notifyMapTransferToPartner(-1);
         player.removeIncomingInvites();
         player.cancelAllBuffs(true);

         player.closePlayerInteractions();
         player.closePartySearchInteractions();

         if (!serverTransition) {
            removePartyPlayer();

            EventInstanceManager eim = player.getEventInstance();
            if (eim != null) {
               eim.playerDisconnected(player);
            }

            if (player.getMonsterCarnival() != null) {
               player.getMonsterCarnival().playerDisconnected(getPlayer().getId());
            }

            if (player.getAriantColiseum() != null) {
               player.getAriantColiseum().playerDisconnected(getPlayer());
            }
         }

         if (player.getMap() != null) {
            int mapId = player.getMapId();
            player.getMap().removePlayer(player);
            if (GameConstants.isDojo(mapId)) {
               this.getChannelServer().freeDojoSectionIfEmpty(mapId);
            }
         }

      } catch (final Throwable t) {
         FilePrinter.printError(FilePrinter.ACCOUNT_STUCK, t);
      }
   }

   public final void disconnect(final boolean shutdown, final boolean inCashShop) {
      if (canDisconnect()) {
         ThreadManager.getInstance().newTask(() -> disconnectInternal(shutdown, inCashShop));
      }
   }

   public final void forceDisconnect() {
      if (canDisconnect()) {
         disconnectInternal(true, false);
      }
   }

   private synchronized boolean canDisconnect() {
      if (disconnecting) {
         return false;
      }

      disconnecting = true;
      return true;
   }

   private void disconnectInternal(boolean shutdown, boolean inCashShop) {//once per MapleClient instance
      if (player != null && player.isLoggedIn() && player.getClient() != null) {
         player.cancelMagicDoor();

         final World world = getWorldServer();
         try {
            removePlayer(world, this.serverTransition);

            if (!(channel == -1 || shutdown)) {
               if (!inCashShop) {
                  if (!this.serverTransition) { // meaning not changing channels
                     int messengerId = player.getMessenger().map(MapleMessenger::getId).orElse(0);
                     if (messengerId > 0) {
                        world.leaveMessenger(messengerId, new MapleMessengerCharacter(player, 0));
                     }
                     player.forfeitExpirableQuests();    //This is for those quests that you have to stay logged in for a certain amount of time
                     player.getGuild().ifPresent(guild -> {
                        MapleGuildProcessor.getInstance().setMemberOnline(player, false, player.getClient().getChannel());
                        PacketCreator.announce(player, new ShowGuildInfo(player));

                     });
                     if (player.getBuddyList() != null) {
                        BuddyListProcessor.getInstance().onLogoff(player);
                     }
                  }
               } else {
                  if (!this.serverTransition) { // if dc inside of cash shop.
                     if (player.getBuddyList() != null) {
                        BuddyListProcessor.getInstance().onLogoff(player);
                     }
                  }
               }
            }
         } catch (final Exception e) {
            FilePrinter.printError(FilePrinter.ACCOUNT_STUCK, e);
         } finally {
            if (!this.serverTransition) {
               MapleGuildCharacter guildCharacter = player.getMGC();
               if (guildCharacter != null) {
                  guildCharacter.clear();
               }
               world.removePlayer(player);
               //getChannelServer().removePlayer(player); already being done

               player.saveCoolDowns();
               player.cancelAllAbnormalStatuses();
               player.saveCharToDB(true);

               player.logOff();
               if (YamlConfig.config.server.INSTANT_NAME_CHANGE) {
                  player.doPendingNameChange();
               }
               clear();
            } else {
               getChannelServer().removePlayer(player);

               player.saveCoolDowns();
               player.cancelAllAbnormalStatuses();
               player.saveCharToDB();
            }
         }
      }
      if (!serverTransition && isLoggedIn()) {
         MapleSessionCoordinator.getInstance().closeSession(session, false);
         updateLoginState(MapleClient.LOGIN_NOT_LOGGED_IN);
         session.removeAttribute(MapleClient.CLIENT_KEY);

         clear();
      } else {
         if (session.containsAttribute(MapleClient.CLIENT_KEY)) {
            MapleSessionCoordinator.getInstance().closeSession(session, false);
            session.removeAttribute(MapleClient.CLIENT_KEY);
         }
         if (!Server.getInstance().hasCharacterIdInTransition(this)) {
            updateLoginState(MapleClient.LOGIN_NOT_LOGGED_IN);
         }

         engines = null;
      }
   }

   private void clear() {
      if (this.player != null) {
         this.player.empty(true); // clears schedules and stuff
      }

      Server.getInstance().unregisterLoginState(this);

      this.accountName = null;
      this.macs = null;
      this.hwid = null;
      this.birthday = null;
      this.engines = null;
      this.player = null;
      this.receive = null;
      this.send = null;
      //this.session = null;
   }

   public void setCharacterOnSessionTransitionState(int cid) {
      this.updateLoginState(MapleClient.LOGIN_SERVER_TRANSITION);
      session.setAttribute(MapleClient.CLIENT_TRANSITION);
      Server.getInstance().setCharacterIdInTransition(this, cid);
   }

   public int getChannel() {
      return channel;
   }

   public void setChannel(int channel) {
      this.channel = channel;
   }

   public Channel getChannelServer() {
      return Server.getInstance().getChannel(world, channel);
   }

   public World getWorldServer() {
      return Server.getInstance().getWorld(world);
   }

   public Channel getChannelServer(byte channel) {
      return Server.getInstance().getChannel(world, channel);
   }

   public boolean deleteCharacter(int cid, int senderAccId) {
      MapleCharacter chr = CharacterProcessor.getInstance().loadCharFromDB(cid, this, false);

      Integer partyId = chr.getWorldServer().getCharacterPartyId(cid);
      if (partyId != null) {
         this.setPlayer(chr);

         chr.getWorldServer().getParty(partyId).ifPresent(party -> {
            chr.setParty(party);
            chr.getMPC();
            chr.leaveParty();
         });
         this.setPlayer(null);
      }

      return CharacterProcessor.getInstance().deleteCharFromDB(chr, senderAccId);
   }

   public String getAccountName() {
      return accountName;
   }

   public void setAccountName(String a) {
      this.accountName = a;
   }

   public int getWorld() {
      return world;
   }

   public void setWorld(int world) {
      this.world = world;
   }

   public void pongReceived() {
      lastPong = Server.getInstance().getCurrentTime();
   }

   public void testPing(long timeThen) {
      try {
         if (lastPong < timeThen) {
            if (session != null && session.isConnected()) {
               MapleSessionCoordinator.getInstance().closeSession(session, false);
               updateLoginState(MapleClient.LOGIN_NOT_LOGGED_IN);
               session.removeAttribute(MapleClient.CLIENT_KEY);
            }
         }
      } catch (NullPointerException e) {
         e.printStackTrace();
      }
   }

   public String getHWID() {
      return hwid;
   }

   public void setHWID(String hwid) {
      this.hwid = hwid;
   }

   public Set<String> getMacs() {
      return Collections.unmodifiableSet(macs);
   }

   public int getGMLevel() {
      return gmLevel;
   }

   public void setGMLevel(int level) {
      gmLevel = level;
   }

   public void setScriptEngine(String name, ScriptEngine e) {
      engines.put(name, e);
   }

   public ScriptEngine getScriptEngine(String name) {
      return engines.get(name);
   }

   public void removeScriptEngine(String name) {
      engines.remove(name);
   }

   public NPCConversationManager getCM() {
      return NPCScriptManager.getInstance().getCM(this);
   }

   public QuestActionManager getQM() {
      return QuestScriptManager.getInstance().getQM(this);
   }

   public boolean acceptToS() {
      boolean disconnect = false;
      if (accountName == null) {
         return true;
      }

      boolean tosStatus = DatabaseConnection.getInstance().withConnectionResult(connection -> AccountProvider.getInstance().getTosStatus(connection, accId)).orElse(false);
      if (tosStatus) {
         disconnect = true;
      }
      DatabaseConnection.getInstance().withConnection(connection -> AccountAdministrator.getInstance().acceptTos(connection, accId));
      return disconnect;
   }

   public void checkChar(int accountId) {
      if (!YamlConfig.config.server.USE_CHARACTER_ACCOUNT_CHECK) {
         return;
      }

      for (World w : Server.getInstance().getWorlds()) {
         for (MapleCharacter chr : w.getPlayerStorage().getAllCharacters()) {
            if (accountId == chr.getAccountID()) {
               FilePrinter.print(FilePrinter.EXPLOITS, "Player:  " + chr.getName() + " has been removed from " + GameConstants.WORLD_NAMES[w.getId()] + ". Possible Dupe attempt.");
               chr.getClient().forceDisconnect();
               w.getPlayerStorage().removePlayer(chr.getId());
            }
         }
      }
   }

   public int getVotePoints() {
      votePoints = DatabaseConnection.getInstance().withConnectionResult(connection -> AccountProvider.getInstance().getVotePoints(connection, accId)).orElse(0);
      return votePoints;
   }

   public void addVotePoints(int points) {
      votePoints += points;
      saveVotePoints();
   }

   public void useVotePoints(int points) {
      if (points > votePoints) {
         //Should not happen, should probably log this
         return;
      }
      votePoints -= points;
      saveVotePoints();
      LogHelper.logLeaf(player, false, Integer.toString(points));
   }

   private void saveVotePoints() {
      DatabaseConnection.getInstance().withConnection(connection -> AccountAdministrator.getInstance().updateVotePoints(connection, accId, votePoints));
   }

   public void lockClient() {
      lock.lock();
   }

   public void unlockClient() {
      lock.unlock();
   }

   public boolean tryAcquireClient() {
      if (actionsSemaphore.tryAcquire()) {
         lockClient();
         return true;
      } else {
         return false;
      }
   }

   public void releaseClient() {
      unlockClient();
      actionsSemaphore.release();
   }

   public boolean tryAcquireEncoder() {
      if (actionsSemaphore.tryAcquire()) {
         encoderLock.lock();
         return true;
      } else {
         return false;
      }
   }

   public void unlockEncoder() {
      encoderLock.unlock();
      actionsSemaphore.release();
   }

   public short getAvailableCharacterSlots() {
      return (short) Math.max(0, characterSlots - Server.getInstance().getAccountCharacterCount(accId));
   }

   public short getAvailableCharacterWorldSlots() {
      return (short) Math.max(0, characterSlots - Server.getInstance().getAccountWorldCharacterCount(accId, world));
   }

   public short getAvailableCharacterWorldSlots(int world) {
      return (short) Math.max(0, characterSlots - Server.getInstance().getAccountWorldCharacterCount(accId, world));
   }

   public short getCharacterSlots() {
      return characterSlots;
   }

   public void setCharacterSlots(byte slots) {
      characterSlots = slots;
   }

   public synchronized boolean gainCharacterSlot() {
      if (characterSlots < 15) {
         DatabaseConnection.getInstance().withConnection(connection -> AccountAdministrator.getInstance().updateSlotCount(connection, accId, this.characterSlots += 1));
         return true;
      }
      return false;
   }

   public final byte getGReason() {
      return DatabaseConnection.getInstance().withConnectionResult(connection -> AccountProvider.getInstance().getGReason(connection, accId)).orElse(Byte.MIN_VALUE);
   }

   public byte getGender() {
      return gender;
   }

   public void setGender(byte gender) {
      this.gender = gender;
      DatabaseConnection.getInstance().withConnection(connection -> AccountAdministrator.getInstance().updateGender(connection, accId, gender));
   }

   private void announceDisableServerMessage() {
      if (!this.getWorldServer().registerDisabledServerMessage(player.getId())) {
         PacketCreator.announce(this, new ServerMessage(""));
      }
   }

   public void announceServerMessage() {
      PacketCreator.announce(this, new ServerMessage(getChannelServer().getServerMessage()));
   }

   public synchronized void announceBossHpBar(MapleMonster monster, final int mobHash, final byte[] packet) {
      long timeNow = System.currentTimeMillis();
      int targetHash = player.getTargetHpBarHash();

      if (mobHash != targetHash) {
         if (timeNow - player.getTargetHpBarTime() >= 5 * 1000) {
            // is there a way to INTERRUPT this annoying thread running on the client that drops the boss bar after some time at every attack?
            announceDisableServerMessage();
            announce(packet);

            player.setTargetHpBarHash(mobHash);
            player.setTargetHpBarTime(timeNow);
         }
      } else {
         announceDisableServerMessage();
         announce(packet);

         player.setTargetHpBarTime(timeNow);
      }
   }

   public synchronized void announce(final byte[] packet) {//MINA CORE IS A FUCKING BITCH AND I HATE IT <3
      announcerLock.lock();
      try {
         session.write(packet);
      } finally {
         announcerLock.unlock();
      }
   }

   public void announceHint(String msg, int length) {
      PacketCreator.announce(this, new ShowHint(msg, length, 10));
      PacketCreator.announce(this, new EnableActions());
   }

   public void changeChannel(int channel) {
      Server server = Server.getInstance();
      if (player.isBanned()) {
         disconnect(false, false);
         return;
      }
      if (!player.isAlive() || FieldLimit.CANNOT_MIGRATE.check(player.getMap().getFieldLimit())) {
         PacketCreator.announce(this, new EnableActions());
         return;
      } else if (MapleMiniDungeonInfo.isDungeonMap(player.getMapId())) {
         MessageBroadcaster.getInstance().sendServerNotice(player, ServerNoticeType.PINK_TEXT, I18nMessage.from("NOT_WITHIN_MINI_DUNGEON"));
         PacketCreator.announce(this, new EnableActions());
         return;
      }

      String[] socket = Server.getInstance().getInetSocket(getWorld(), channel);
      if (socket == null) {
         MessageBroadcaster.getInstance().sendServerNotice(player, ServerNoticeType.POP_UP, I18nMessage.from("CHANNEL_ALREADY_DISABLED").with(channel));
         PacketCreator.announce(this, new EnableActions());
         return;
      }

      player.closePlayerInteractions();
      player.closePartySearchInteractions();

      player.unregisterChairBuff();
      server.getPlayerBuffStorage().addBuffsToStorage(player.getId(), player.getAllBuffs());
      server.getPlayerBuffStorage().addDiseasesToStorage(player.getId(), player.getAlAbnormalStatuses());
      player.setDisconnectedFromChannelWorld();
      player.notifyMapTransferToPartner(-1);
      player.removeIncomingInvites();
      player.cancelAllBuffs(true);
      player.cancelAllAbnormalStatuses();
      player.cancelBuffExpireTask();
      player.cancelDiseaseExpireTask();
      player.cancelSkillCoolDownTask();
      player.cancelQuestExpirationTask();

      player.getInventory(MapleInventoryType.EQUIPPED).checked(false); //test
      player.getMap().removePlayer(player);
      player.clearBanishPlayerData();
      player.getClient().getChannelServer().removePlayer(player);

      player.saveCharToDB();

      player.setSessionTransitionState();
      try {
         PacketCreator.announce(this, new ChangeChannel(InetAddress.getByName(socket[0]), Integer.parseInt(socket[1])));
      } catch (IOException e) {
         e.printStackTrace();
      }
   }

   public long getSessionId() {
      return this.sessionId;
   }

   public void setSessionId(long sessionId) {
      this.sessionId = sessionId;
   }

   public boolean canRequestCharacterList() {
      return lastNpcClick + 877 < Server.getInstance().getCurrentTime();
   }

   public boolean canClickNPC() {
      return lastNpcClick + 500 < Server.getInstance().getCurrentTime();
   }

   public void setClickedNPC() {
      lastNpcClick = Server.getInstance().getCurrentTime();
   }

   public void removeClickedNPC() {
      lastNpcClick = 0;
   }

   public int getVisibleWorlds() {
      return visibleWorlds;
   }

   public void requestedServerList(int worlds) {
      visibleWorlds = worlds;
      setClickedNPC();
   }

   public void closePlayerScriptInteractions() {
      this.removeClickedNPC();
      NPCScriptManager.getInstance().dispose(this);
      QuestScriptManager.getInstance().dispose(this);
   }

   public boolean attemptCsCoupon() {
      if (csAttempt > 2) {
         resetCsCoupon();
         return false;
      }

      csAttempt++;
      return true;
   }

   public void resetCsCoupon() {
      csAttempt = 0;
   }

   public void enableCSActions() {
      PacketCreator.announce(this, new ShowCash(player.getCashShop().getCash(1), player.getCashShop().getCash(2), player.getCashShop().getCash(4)));
   }

   public String getNibbleHWID() {
      return (String) session.getAttribute(MapleClient.CLIENT_NIBBLE_HWID);
   }

   public boolean cannotBypassPin() {
      return !MapleLoginBypassCoordinator.getInstance().canLoginBypass(getNibbleHWID(), accId, false);
   }

   public boolean cannotBypassPic() {
      return !MapleLoginBypassCoordinator.getInstance().canLoginBypass(getNibbleHWID(), accId, true);
   }

   public Locale getLocale() {
      return locale;
   }

   public void setLocale(Locale locale) {
      this.locale = locale;
   }
}