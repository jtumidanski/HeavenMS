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
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Lock;
import java.util.stream.Collectors;
import javax.script.ScriptEngine;

import org.apache.mina.core.session.IoSession;

import client.database.administrator.AccountAdministrator;
import client.database.administrator.HwidBanAdministrator;
import client.database.administrator.MacBanAdministrator;
import client.database.data.AccountData;
import client.database.data.AccountLoginData;
import client.database.data.CharNameAndIdData;
import client.database.provider.AccountProvider;
import client.database.provider.BitVotingRecordProvider;
import client.database.provider.CharacterProvider;
import client.database.provider.HwidBanProvider;
import client.database.provider.IpBanProvider;
import client.database.provider.MacBanProvider;
import client.database.provider.MacFilterProvider;
import client.inventory.MapleInventoryType;
import client.processor.CharacterProcessor;
import constants.GameConstants;
import constants.ServerConstants;
import net.server.Server;
import net.server.audit.locks.MonitoredLockType;
import net.server.audit.locks.factory.MonitoredReentrantLockFactory;
import net.server.channel.Channel;
import net.server.coordinator.MapleLoginBypassCoordinator;
import net.server.coordinator.MapleSessionCoordinator;
import net.server.coordinator.MapleSessionCoordinator.AntiMulticlientResult;
import net.server.guild.MapleGuildCharacter;
import net.server.processor.MapleGuildProcessor;
import net.server.world.MapleMessenger;
import net.server.world.MapleMessengerCharacter;
import net.server.world.MapleParty;
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
import server.quest.MapleQuest;
import tools.BCrypt;
import tools.DatabaseConnection;
import tools.FilePrinter;
import tools.HexTool;
import tools.LogHelper;
import tools.MapleAESOFB;
import tools.MessageBroadcaster;
import tools.PacketCreator;
import tools.ServerNoticeType;
import tools.packet.ChangeChannel;
import tools.packet.cashshop.ShowCash;
import tools.packet.foreigneffect.ShowHint;
import tools.packet.guild.ShowGuildInfo;
import tools.packet.message.ServerMessage;
import tools.packet.stat.EnableActions;

public class MapleClient {

   public static final int LOGIN_NOTLOGGEDIN = 0;
   public static final int LOGIN_SERVER_TRANSITION = 1;
   public static final int LOGIN_LOGGEDIN = 2;
   public static final String CLIENT_KEY = "CLIENT";
   public static final String CLIENT_HWID = "HWID";
   public static final String CLIENT_NIBBLEHWID = "HWID2";
   public static final String CLIENT_REMOTE_ADDRESS = "REMOTE_IP";
   public static final String CLIENT_TRANSITION = "TRANSITION";
   private static final Lock[] loginLocks = new Lock[200];  // thanks Masterrulax & try2hack for pointing out a bottleneck issue here
   private Calendar tempBanCalendar;

   static {
      for (int i = 0; i < 200; i++) {
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
   private int lang = 0;

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

   public int getVoteTime() {
      if (voteTime != -1) {
         return voteTime;
      }
      voteTime = DatabaseConnection.getInstance().withConnectionResult(connection -> BitVotingRecordProvider.getInstance().getVoteDate(connection, accountName)).orElse(-1);
      return voteTime;
   }

   public void resetVoteTime() {
      voteTime = -1;
   }

   public boolean hasVotedAlready() {
      Date currentDate = new Date();
      int timeNow = (int) (currentDate.getTime() / 1000);
      int difference = (timeNow - getVoteTime());
      return difference < 86400 && difference > 0;
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
         hwid = DatabaseConnection.getInstance().withConnectionResult(connection -> AccountProvider.getInstance().getHwid(connection, accId)).orElse(null);
      }
   }

   private void loadMacsIfNecessary() {
      if (macs.isEmpty()) {
         DatabaseConnection.getInstance().withConnectionResult(connection -> AccountProvider.getInstance().getMacs(connection, accId)).ifPresent(result -> macs.addAll(result));
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
      Lock loginLock = loginLocks[this.getAccID() % 200];
      loginLock.lock();
      try {
         if (getLoginState() > LOGIN_NOTLOGGEDIN) { // 0 = LOGIN_NOTLOGGEDIN, 1= LOGIN_SERVER_TRANSITION, 2 = LOGIN_LOGGEDIN
            loggedIn = false;
            return 7;
         }
         updateLoginState(LOGIN_LOGGEDIN);
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
      if (!(ServerConstants.ENABLE_PIN && cannotBypassPin())) {
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
      if (!(ServerConstants.ENABLE_PIC && cannotBypassPic())) {
         return true;
      }

      picAttempt++;
      if (picAttempt > 5) {
         MapleSessionCoordinator.getInstance().closeSession(session, false);
      }
      if (pic.equals(other)) {    // thanks ryantpayton (HeavenClient) for noticing null pics being checked here
         picAttempt = 0;
         MapleLoginBypassCoordinator.getInstance().registerLoginBypassEntry(getNibbleHWID(), accId, true);
         return true;
      }
      return false;
   }

   public int login(String login, String pwd, String nibbleHwid) {
      int loginok = 5;

      loginAttempt++;
      if (loginAttempt > 4) {
         loggedIn = false;
         MapleSessionCoordinator.getInstance().closeSession(session, false);
         return 6;   // thanks Survival_Project for finding out an issue with AUTOMATIC_REGISTER here
      }

      Optional<AccountData> accountData = DatabaseConnection.getInstance().withConnectionResultOpt(connection -> AccountProvider.getInstance().getAccountDataByName(connection, login));
      if (accountData.isEmpty()) {
         accId = -2;
      } else {
         accId = accountData.get().id();
         if (accId <= 0) {
            FilePrinter.printError(FilePrinter.LOGIN_EXCEPTION, "Tried to login with accid " + accId);
            return 15;
         }

         gmLevel = 0;
         pin = accountData.get().pin();
         pic = accountData.get().pic();
         gender = accountData.get().gender();
         characterSlots = accountData.get().characterSlots();
         lang = accountData.get().language();
         String passwordHash = accountData.get().password();
         byte tos = accountData.get().tos();

         if (accountData.get().banned()) {
            return 3;
         }

         if (getLoginState() > LOGIN_NOTLOGGEDIN) { // already loggedin
            loggedIn = false;
            loginok = 7;
         } else if (passwordHash.charAt(0) == '$' && passwordHash.charAt(1) == '2' && BCrypt.checkpw(pwd, passwordHash)) {
            loginok = (tos == 0) ? 23 : 0;
         } else if (pwd.equals(passwordHash) || checkHash(passwordHash, "SHA-1", pwd) || checkHash(passwordHash, "SHA-512", pwd)) {
            // thanks GabrielSin for detecting some no-bcrypt inconsistencies here
            loginok = (tos == 0) ? (!ServerConstants.BCRYPT_MIGRATION ? 23 : -23) : (!ServerConstants.BCRYPT_MIGRATION ? 0 : -10); // migrate to bcrypt
         } else {
            loggedIn = false;
            loginok = 4;
         }
      }

      if (loginok == 0 || loginok == 4) {
         AntiMulticlientResult res = MapleSessionCoordinator.getInstance().attemptLoginSession(session, nibbleHwid, accId, loginok == 4);

         switch (res) {
            case SUCCESS:
               if (loginok == 0) {
                  loginAttempt = 0;
               }
               return loginok;
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
         return loginok;
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
      if (newState == LOGIN_LOGGEDIN) {
         MapleSessionCoordinator.getInstance().updateOnlineSession(this.getSession());
      }

      DatabaseConnection.getInstance().withConnection(connection -> AccountAdministrator.getInstance().setLoggedInStatus(connection, accId, newState));

      if (newState == LOGIN_NOTLOGGEDIN) {
         loggedIn = false;
         serverTransition = false;
         setAccID(0);
      } else {
         serverTransition = (newState == LOGIN_SERVER_TRANSITION);
         loggedIn = !serverTransition;
      }
   }

   public int getLoginState() {  // 0 = LOGIN_NOTLOGGEDIN, 1= LOGIN_SERVER_TRANSITION, 2 = LOGIN_LOGGEDIN
      Optional<AccountLoginData> loginData = DatabaseConnection.getInstance().withConnectionResult(connection -> AccountProvider.getInstance().getLoginData(connection, accId));
      if (loginData.isEmpty()) {
         return LOGIN_NOTLOGGEDIN;
      }

      birthday = Calendar.getInstance();
      birthday.setTime(loginData.get().birthday());

      int state = loginData.get().loggedIn();
      if (state == LOGIN_SERVER_TRANSITION) {
         if (loginData.get().lastLogin().getTime() + 30000 < Server.getInstance().getCurrentTime()) {
            int accountId = accId;
            state = LOGIN_NOTLOGGEDIN;
            updateLoginState(LOGIN_NOTLOGGEDIN);   // ACCID = 0, issue found thanks to Tochi & K u ssss o & Thora & Omo Oppa
            this.setAccID(accountId);
         }
      }

      if (state == LOGIN_LOGGEDIN) {
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

   private void removePartyPlayer(World world) {
      MapleMap map = player.getMap();
      final MapleParty party = player.getParty();
      final int idz = player.getId();

      if (party != null) {
         final MaplePartyCharacter maplePartyCharacter = new MaplePartyCharacter(player);
         maplePartyCharacter.setOnline(false);
         world.updateParty(party.getId(), PartyOperation.LOG_ONOFF, maplePartyCharacter);
         if (party.getLeader().getId() == idz && map != null) {
            MaplePartyCharacter lchr = null;
            for (MaplePartyCharacter partyMember : party.getMembers()) {
               if (partyMember != null && partyMember.getId() != idz && (lchr == null || lchr.getLevel() <= partyMember.getLevel()) && map.getCharacterById(partyMember.getId()) != null) {
                  lchr = partyMember;
               }
            }
            if (lchr != null) {
               world.updateParty(party.getId(), PartyOperation.CHANGE_LEADER, lchr);
            }
         }
      }
   }

   private void removePlayer(World world, boolean serverTransition) {
      try {
         player.setDisconnectedFromChannelWorld();
         player.notifyMapTransferToPartner(-1);
         player.removeIncomingInvites();
         player.cancelAllBuffs(true);

         player.closePlayerInteractions();
         player.closePartySearchInteractions();

         if (!serverTransition) {    // thanks MedicOP for detecting an issue with party leader change on changing channels
            removePartyPlayer(world);

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
      if (player != null && player.isLoggedin() && player.getClient() != null) {
         //final int fid = player.getFamilyId();
         final BuddyList bl = player.getBuddylist();

         player.cancelMagicDoor();

         final World world = getWorldServer();   // obviously wserv is NOT null if this player was online on it
         try {
            removePlayer(world, this.serverTransition);

            if (!(channel == -1 || shutdown)) {
               if (!inCashShop) {
                  if (!this.serverTransition) { // meaning not changing channels
                     int messengerId = player.getMessenger().map(MapleMessenger::getId).orElse(0);
                     if (messengerId > 0) {
                        world.leaveMessenger(messengerId, new MapleMessengerCharacter(player, 0));
                     }
                                                        /*
                                                        if (fid > 0) {
                                                                final MapleFamily family = worlda.getFamily(fid);
                                                                family.
                                                        }
                                                        */
                     for (MapleQuestStatus status : player.getStartedQuests()) { //This is for those quests that you have to stay logged in for a certain amount of time
                        MapleQuest quest = status.getQuest();
                        if (quest.getTimeLimit() > 0) {
                           MapleQuestStatus newStatus = new MapleQuestStatus(quest, MapleQuestStatus.Status.NOT_STARTED);
                           newStatus.setForfeited(player.getQuest(quest).getForfeited() + 1);
                           player.updateQuest(newStatus);
                        }
                     }
                     player.getGuild().ifPresent(guild -> {
                        MapleGuildProcessor.getInstance().setMemberOnline(player, false, player.getClient().getChannel());
                        PacketCreator.announce(player, new ShowGuildInfo(player));

                     });
                     if (bl != null) {
                        world.loggedOff(player.getName(), player.getId(), channel, player.getBuddylist().getBuddyIds());
                     }
                  }
               } else {
                  if (!this.serverTransition) { // if dc inside of cash shop.
                     if (bl != null) {
                        world.loggedOff(player.getName(), player.getId(), channel, player.getBuddylist().getBuddyIds());
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

               player.saveCooldowns();
               player.cancelAllDebuffs();
               player.saveCharToDB(true);

               player.logOff();
               if (ServerConstants.INSTANT_NAME_CHANGE) {
                  player.doPendingNameChange();
               }
               clear();
            } else {
               getChannelServer().removePlayer(player);

               player.saveCooldowns();
               player.cancelAllDebuffs();
               player.saveCharToDB();
            }
         }
      }
      if (!serverTransition && isLoggedIn()) {
         MapleSessionCoordinator.getInstance().closeSession(session, false);
         updateLoginState(MapleClient.LOGIN_NOTLOGGEDIN);
         session.removeAttribute(MapleClient.CLIENT_KEY); // prevents double dcing during login

         clear();
      } else {
         if (session.containsAttribute(MapleClient.CLIENT_KEY)) {
            MapleSessionCoordinator.getInstance().closeSession(session, false);
            session.removeAttribute(MapleClient.CLIENT_KEY);
         }
         if (!Server.getInstance().hasCharacteridInTransition(session)) {
            updateLoginState(MapleClient.LOGIN_NOTLOGGEDIN);
         }

         engines = null; // thanks Tochi for pointing out a NPE here
      }
   }

   private void clear() {
      // player hard reference removal thanks to Steve (kaito1410)
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

      Integer partyId = chr.getWorldServer().getCharacterPartyid(cid);
      if (partyId != null) {
         this.setPlayer(chr);

         MapleParty party = chr.getWorldServer().getParty(partyId);
         chr.setParty(party);
         chr.getMPC();
         chr.leaveParty();   // thanks Vcoc for pointing out deleted characters would still stay in a party

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
               updateLoginState(MapleClient.LOGIN_NOTLOGGEDIN);
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
      boolean disconnectForBeingAFaggot = false;
      if (accountName == null) {
         return true;
      }

      boolean tosStatus = DatabaseConnection.getInstance().withConnectionResult(connection -> AccountProvider.getInstance().getTosStatus(connection, accId)).orElse(false);
      if (tosStatus) {
         disconnectForBeingAFaggot = true;
      }
      DatabaseConnection.getInstance().withConnection(connection -> AccountAdministrator.getInstance().acceptTos(connection, accId));
      return disconnectForBeingAFaggot;
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
      if (!player.isAlive() || FieldLimit.CANNOTMIGRATE.check(player.getMap().getFieldLimit())) {
         PacketCreator.announce(this, new EnableActions());
         return;
      } else if (MapleMiniDungeonInfo.isDungeonMap(player.getMapId())) {
         MessageBroadcaster.getInstance().sendServerNotice(player, ServerNoticeType.PINK_TEXT, "Changing channels or entering Cash Shop or MTS are disabled when inside a Mini-Dungeon.");
         PacketCreator.announce(this, new EnableActions());
         return;
      }

      String[] socket = Server.getInstance().getInetSocket(getWorld(), channel);
      if (socket == null) {
         MessageBroadcaster.getInstance().sendServerNotice(player, ServerNoticeType.POP_UP, "Channel " + channel + " is currently disabled. Try another channel.");
         PacketCreator.announce(this, new EnableActions());
         return;
      }

      player.closePlayerInteractions();
      player.closePartySearchInteractions();

      player.unregisterChairBuff();
      server.getPlayerBuffStorage().addBuffsToStorage(player.getId(), player.getAllBuffs());
      server.getPlayerBuffStorage().addDiseasesToStorage(player.getId(), player.getAllDiseases());
      player.setDisconnectedFromChannelWorld();
      player.notifyMapTransferToPartner(-1);
      player.removeIncomingInvites();
      player.cancelAllBuffs(true);
      player.cancelAllDebuffs();
      player.cancelBuffExpireTask();
      player.cancelDiseaseExpireTask();
      player.cancelSkillCooldownTask();
      player.cancelQuestExpirationTask();
      //Cancelling magicdoor? Nope
      //Cancelling mounts? Noty

      player.getInventory(MapleInventoryType.EQUIPPED).checked(false); //test
      player.getMap().removePlayer(player);
      player.clearBanishPlayerData();
      player.getClient().getChannelServer().removePlayer(player);

      player.saveCharToDB();

      player.getClient().updateLoginState(MapleClient.LOGIN_SERVER_TRANSITION);
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

   public boolean canRequestCharacterlist() {
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
      return (String) session.getAttribute(MapleClient.CLIENT_NIBBLEHWID);
   }

   public boolean cannotBypassPin() {
      return !MapleLoginBypassCoordinator.getInstance().canLoginBypass(getNibbleHWID(), accId, false);
   }

   public boolean cannotBypassPic() {
      return !MapleLoginBypassCoordinator.getInstance().canLoginBypass(getNibbleHWID(), accId, true);
   }

   public int getLanguage() {
      return lang;
   }

   public void setLanguage(int language) {
      this.lang = language;
   }
}