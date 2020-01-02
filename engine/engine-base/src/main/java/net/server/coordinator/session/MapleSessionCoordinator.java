package net.server.coordinator.session;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.persistence.EntityManager;

import org.apache.mina.core.session.IoSession;

import client.MapleClient;
import database.administrator.HwidAccountAdministrator;
import database.provider.HwidAccountProvider;
import client.processor.CharacterProcessor;
import config.YamlConfig;
import net.server.Server;
import net.server.audit.locks.MonitoredLockType;
import net.server.audit.locks.factory.MonitoredReentrantLockFactory;
import net.server.coordinator.login.LoginStorage;
import database.DatabaseConnection;
import tools.Pair;

public class MapleSessionCoordinator {

   private final static MapleSessionCoordinator instance = new MapleSessionCoordinator();
   private final LoginStorage loginStorage = new LoginStorage();
   private final Map<Integer, MapleClient> onlineClients = new HashMap<>();
   private final Set<String> onlineRemoteHwids = new HashSet<>();
   private final Map<String, Set<IoSession>> loginRemoteHosts = new HashMap<>();
   private final Set<String> pooledRemoteHosts = new HashSet<>();
   private final ConcurrentHashMap<String, String> cachedHostHwids = new ConcurrentHashMap<>();
   private final ConcurrentHashMap<String, Long> cachedHostTimeout = new ConcurrentHashMap<>();
   private final List<ReentrantLock> poolLock = new ArrayList<>(100);

   private MapleSessionCoordinator() {
      for (int i = 0; i < 100; i++) {
         poolLock.add(MonitoredReentrantLockFactory.createLock(MonitoredLockType.SERVER_LOGIN_COORD));
      }
   }

   public static MapleSessionCoordinator getInstance() {
      return instance;
   }

   private static long hwidExpirationUpdate(int relevance) {
      int degree = 1, i = relevance, subdegree;
      while ((subdegree = 5 * degree) <= i) {
         i -= subdegree;
         degree++;
      }

      degree--;
      int baseTime, subdegreeTime;
      if (degree > 2) {
         subdegreeTime = 10;
      } else {
         subdegreeTime = 1 + (3 * degree);
      }

      switch (degree) {
         case 0:
            baseTime = 2;       // 2 hours
            break;

         case 1:
            baseTime = 24;      // 1 day
            break;

         case 2:
            baseTime = 168;     // 7 days
            break;

         default:
            baseTime = 1680;    // 70 days
      }

      return 3600000 * (baseTime + subdegreeTime);
   }

   private static void updateAccessAccount(EntityManager entityManager, String remoteHwid, int accountId, int loginRelevance) {
      java.sql.Timestamp nextTimestamp = new java.sql.Timestamp(Server.getInstance().getCurrentTime() + hwidExpirationUpdate(loginRelevance));
      if (loginRelevance < Byte.MAX_VALUE) {
         loginRelevance++;
      }
      HwidAccountAdministrator.getInstance().updateByAccountId(entityManager, accountId, remoteHwid, loginRelevance, nextTimestamp);
   }

   private static void registerAccessAccount(EntityManager entityManager, String remoteHwid, int accountId) {
      HwidAccountAdministrator.getInstance().create(entityManager, accountId, remoteHwid, new java.sql.Timestamp(Server.getInstance().getCurrentTime() + hwidExpirationUpdate(0)));
   }

   private static boolean associateHwidAccountIfAbsent(String remoteHwid, int accountId) {
      return DatabaseConnection.getInstance().withConnectionResult(connection -> {
         int hwidCount = 0;
         List<String> hwids = HwidAccountProvider.getInstance().getHwidForAccount(connection, accountId);
         for (String hwid : hwids) {
            if (hwid.contentEquals(remoteHwid)) {
               return false;
            }
            hwidCount++;
         }
         if (hwidCount < YamlConfig.config.server.MAX_ALLOWED_ACCOUNT_HWID) {
            registerAccessAccount(connection, remoteHwid, accountId);
            return true;
         }
         return false;
      }).orElse(false);
   }

   private static boolean attemptAccessAccount(String nibbleHwid, int accountId, boolean routineCheck) {
      return DatabaseConnection.getInstance().withConnectionResult(connection -> {
         int hwidCount = 0;
         List<Pair<String, Integer>> results = HwidAccountProvider.getInstance().getForAccount(connection, accountId);
         for (Pair<String, Integer> pair : results) {
            if (pair.getLeft().endsWith(nibbleHwid)) {
               if (!routineCheck) {
                  // better update HWID relevance as soon as the login is authenticated
                  updateAccessAccount(connection, pair.getLeft(), accountId, pair.getRight());
               }
               return true;
            }
            hwidCount++;
         }
         return hwidCount < YamlConfig.config.server.MAX_ALLOWED_ACCOUNT_HWID;
      }).orElse(false);
   }

   public static String getSessionRemoteAddress(IoSession session) {
      return (String) session.getAttribute(MapleClient.CLIENT_REMOTE_ADDRESS);
   }

   public static String getSessionRemoteHost(IoSession session) {
      String nibbleHwid = (String) session.getAttribute(MapleClient.CLIENT_NIBBLE_HWID);

      if (nibbleHwid != null) {
         return getSessionRemoteAddress(session) + "-" + nibbleHwid;
      } else {
         return getSessionRemoteAddress(session);
      }
   }

   private static MapleClient getSessionClient(IoSession session) {
      return (MapleClient) session.getAttribute(MapleClient.CLIENT_KEY);
   }

   private static MapleClient fetchInTransitionSessionClient(IoSession session) {
      String remoteHwid = MapleSessionCoordinator.getInstance().getGameSessionHwid(session);

      if (remoteHwid != null) {   // maybe this session was currently in-transition?
         int hwidLen = remoteHwid.length();
         if (hwidLen <= 8) {
            session.setAttribute(MapleClient.CLIENT_NIBBLE_HWID, remoteHwid);
         } else {
            session.setAttribute(MapleClient.CLIENT_HWID, remoteHwid);
            session.setAttribute(MapleClient.CLIENT_NIBBLE_HWID, remoteHwid.substring(hwidLen - 8, hwidLen));
         }

         MapleClient client = new MapleClient(null, null, session);
         Integer cid = Server.getInstance().freeCharacterIdInTransition(client);
         if (cid != null) {
            client.setAccID(CharacterProcessor.getInstance().loadCharFromDB(cid, client, false).getAccountID());
         }

         session.setAttribute(MapleClient.CLIENT_KEY, client);
         return client;
      }

      return null;
   }

   private Lock getCoordinatorLock(String remoteHost) {
      return poolLock.get(Math.abs(remoteHost.hashCode()) % 100);
   }

   public void updateOnlineSession(IoSession session) {
      MapleClient client = getSessionClient(session);

      if (client != null) {
         int accountId = client.getAccID();
         MapleClient inGameClient = onlineClients.get(accountId);
         if (inGameClient != null) {
            inGameClient.forceDisconnect();
         }

         onlineClients.put(accountId, client);
      }
   }

   public boolean canStartLoginSession(IoSession session) {
      if (!YamlConfig.config.server.DETERRED_MULTICLIENT) {
         return true;
      }

      String remoteHost = getSessionRemoteHost(session);
      Lock lock = getCoordinatorLock(remoteHost);

      try {
         int tries = 0;
         while (true) {
            if (lock.tryLock()) {
               try {
                  if (pooledRemoteHosts.contains(remoteHost)) {
                     return false;
                  }

                  pooledRemoteHosts.add(remoteHost);
               } finally {
                  lock.unlock();
               }

               break;
            } else {
               if (tries == 2) {
                  return true;
               }
               tries++;

               Thread.sleep(1777);
            }
         }
      } catch (Exception e) {
         e.printStackTrace();
         return true;
      }

      try {
         String knownHwid = cachedHostHwids.get(remoteHost);
         if (knownHwid != null) {
            if (onlineRemoteHwids.contains(knownHwid)) {
               return false;
            }
         }

         if (loginRemoteHosts.containsKey(remoteHost)) {
            return false;
         }

         Set<IoSession> lrh = new HashSet<>(2);
         lrh.add(session);
         loginRemoteHosts.put(remoteHost, lrh);

         return true;
      } finally {
         lock.lock();
         try {
            pooledRemoteHosts.remove(remoteHost);
         } finally {
            lock.unlock();
         }
      }
   }

   public void closeLoginSession(IoSession session) {
      String nibbleHwid = (String) session.removeAttribute(MapleClient.CLIENT_NIBBLE_HWID);
      String remoteHost = getSessionRemoteHost(session);
      Set<IoSession> lrh = loginRemoteHosts.get(remoteHost);
      if (lrh != null) {
         lrh.remove(session);
         if (lrh.isEmpty()) {
            loginRemoteHosts.remove(remoteHost);
         }
      }

      if (nibbleHwid != null) {
         onlineRemoteHwids.remove(nibbleHwid);

         MapleClient client = getSessionClient(session);
         if (client != null) {
            MapleClient loggedClient = onlineClients.get(client.getAccID());

            // do not remove an online game session here, only login session
            if (loggedClient != null && loggedClient.getSessionId() == client.getSessionId()) {
               onlineClients.remove(client.getAccID());
            }
         }
      }
   }

   public AntiMultiClientResult attemptLoginSession(IoSession session, String nibbleHwid, int accountId, boolean routineCheck) {
      if (!YamlConfig.config.server.DETERRED_MULTICLIENT) {
         session.setAttribute(MapleClient.CLIENT_NIBBLE_HWID, nibbleHwid);
         return AntiMultiClientResult.SUCCESS;
      }

      String remoteHost = getSessionRemoteHost(session);
      Lock lock = getCoordinatorLock(remoteHost);

      try {
         int tries = 0;
         while (true) {
            if (lock.tryLock()) {
               try {
                  if (pooledRemoteHosts.contains(remoteHost)) {
                     return AntiMultiClientResult.REMOTE_PROCESSING;
                  }

                  pooledRemoteHosts.add(remoteHost);
               } finally {
                  lock.unlock();
               }

               break;
            } else {
               if (tries == 2) {
                  return AntiMultiClientResult.COORDINATOR_ERROR;
               }
               tries++;

               Thread.sleep(1777);
            }
         }
      } catch (Exception e) {
         e.printStackTrace();
         return AntiMultiClientResult.COORDINATOR_ERROR;
      }

      try {
         if (!loginStorage.registerLogin(accountId)) {
            return AntiMultiClientResult.MANY_ACCOUNT_ATTEMPTS;
         }

         if (!routineCheck) {
            if (onlineRemoteHwids.contains(nibbleHwid)) {
               return AntiMultiClientResult.REMOTE_LOGGEDIN;
            }

            if (!attemptAccessAccount(nibbleHwid, accountId, routineCheck)) {
               return AntiMultiClientResult.REMOTE_REACHED_LIMIT;
            }

            session.setAttribute(MapleClient.CLIENT_NIBBLE_HWID, nibbleHwid);
            onlineRemoteHwids.add(nibbleHwid);
         } else {
            if (!attemptAccessAccount(nibbleHwid, accountId, routineCheck)) {
               return AntiMultiClientResult.REMOTE_REACHED_LIMIT;
            }
         }

         return AntiMultiClientResult.SUCCESS;
      } finally {
         lock.lock();
         try {
            pooledRemoteHosts.remove(remoteHost);
         } finally {
            lock.unlock();
         }
      }
   }

   public AntiMultiClientResult attemptGameSession(IoSession session, int accountId, String remoteHwid) {
      String remoteHost = getSessionRemoteHost(session);
      if (!YamlConfig.config.server.DETERRED_MULTICLIENT) {
         associateRemoteHostHwid(remoteHost, remoteHwid);
         associateRemoteHostHwid(getSessionRemoteAddress(session), remoteHwid);  // no HWID information on the logged in newcomer session...
         return AntiMultiClientResult.SUCCESS;
      }

      Lock lock = getCoordinatorLock(remoteHost);
      try {
         int tries = 0;
         while (true) {
            if (lock.tryLock()) {
               try {
                  if (pooledRemoteHosts.contains(remoteHost)) {
                     return AntiMultiClientResult.REMOTE_PROCESSING;
                  }

                  pooledRemoteHosts.add(remoteHost);
               } finally {
                  lock.unlock();
               }

               break;
            } else {
               if (tries == 2) {
                  return AntiMultiClientResult.COORDINATOR_ERROR;
               }
               tries++;

               Thread.sleep(1777);
            }
         }
      } catch (Exception e) {
         e.printStackTrace();
         return AntiMultiClientResult.COORDINATOR_ERROR;
      }

      try {
         String nibbleHwid = (String) session.getAttribute(MapleClient.CLIENT_NIBBLE_HWID);
         if (nibbleHwid != null) {
            onlineRemoteHwids.remove(nibbleHwid);

            if (remoteHwid.endsWith(nibbleHwid)) {
               if (!onlineRemoteHwids.contains(remoteHwid)) {
                  // assumption: after a SUCCESSFUL login attempt, the incoming client WILL receive a new IoSession from the game server

                  // updated session CLIENT_HWID attribute will be set when the player log in the game
                  onlineRemoteHwids.add(remoteHwid);
                  associateRemoteHostHwid(remoteHost, remoteHwid);
                  associateRemoteHostHwid(getSessionRemoteAddress(session), remoteHwid);
                  associateHwidAccountIfAbsent(remoteHwid, accountId);

                  return AntiMultiClientResult.SUCCESS;
               } else {
                  return AntiMultiClientResult.REMOTE_LOGGEDIN;
               }
            } else {
               return AntiMultiClientResult.REMOTE_NO_MATCH;
            }
         } else {
            return AntiMultiClientResult.REMOTE_NO_MATCH;
         }
      } finally {
         lock.lock();
         try {
            pooledRemoteHosts.remove(remoteHost);
         } finally {
            lock.unlock();
         }
      }
   }

   public void closeSession(IoSession session, Boolean immediately) {
      MapleClient client = getSessionClient(session);
      if (client == null) {
         client = fetchInTransitionSessionClient(session);
      }

      String hwid = (String) session.removeAttribute(MapleClient.CLIENT_NIBBLE_HWID); // making sure to clean up calls to this function on login phase
      onlineRemoteHwids.remove(hwid);

      hwid = (String) session.removeAttribute(MapleClient.CLIENT_HWID);
      onlineRemoteHwids.remove(hwid);

      if (client != null) {
         if (hwid != null) { // is a game session
            onlineClients.remove(client.getAccID());
         } else {
            MapleClient loggedClient = onlineClients.get(client.getAccID());

            // do not remove an online game session here, only login session
            if (loggedClient != null && loggedClient.getSessionId() == client.getSessionId()) {
               onlineClients.remove(client.getAccID());
            }
         }
      }

      if (immediately != null) {
         if (immediately) {
            session.closeNow();
         } else {
            session.closeOnFlush();
         }
      }

      // session.removeAttribute(MapleClient.CLIENT_REMOTE_ADDRESS); No real need for removing String property on closed sessions
   }

   public String pickLoginSessionHwid(IoSession session) {
      String remoteHost = getSessionRemoteAddress(session);
      return cachedHostHwids.remove(remoteHost);
   }

   public String getGameSessionHwid(IoSession session) {
      String remoteHost = getSessionRemoteHost(session);
      return cachedHostHwids.get(remoteHost);
   }

   private void associateRemoteHostHwid(String remoteHost, String remoteHwid) {
      cachedHostHwids.put(remoteHost, remoteHwid);
      cachedHostTimeout.put(remoteHost, Server.getInstance().getCurrentTime() + 604800000);   // 1 week-time entry
   }

   public void runUpdateHwidHistory() {
      DatabaseConnection.getInstance().withConnection(connection -> HwidAccountAdministrator.getInstance().deleteExpired(connection));

      long timeNow = Server.getInstance().getCurrentTime();
      List<String> toRemove = new LinkedList<>();
      for (Entry<String, Long> cht : cachedHostTimeout.entrySet()) {
         if (cht.getValue() < timeNow) {
            toRemove.add(cht.getKey());
         }
      }

      if (!toRemove.isEmpty()) {
         for (String s : toRemove) {
            cachedHostHwids.remove(s);
            cachedHostTimeout.remove(s);
         }
      }
   }

   public void runUpdateLoginHistory() {
      loginStorage.updateLoginHistory();
   }

   public void printSessionTrace() {
      if (!onlineClients.isEmpty()) {
         List<Entry<Integer, MapleClient>> elist = new ArrayList<>(onlineClients.entrySet());
         elist.sort(Entry.comparingByKey());

         System.out.println("Current online clients: ");
         for (Entry<Integer, MapleClient> e : elist) {
            System.out.println("  " + e.getKey());
         }
      }

      if (!onlineRemoteHwids.isEmpty()) {
         List<String> slist = new ArrayList<>(onlineRemoteHwids);
         Collections.sort(slist);

         System.out.println("Current online HWIDs: ");
         for (String s : slist) {
            System.out.println("  " + s);
         }
      }

      if (!loginRemoteHosts.isEmpty()) {
         List<Entry<String, Set<IoSession>>> elist = new ArrayList<>(loginRemoteHosts.entrySet());

         elist.sort(Entry.comparingByKey());

         System.out.println("Current login sessions: ");
         for (Entry<String, Set<IoSession>> e : elist) {
            System.out.println("  " + e.getKey() + ", size: " + e.getValue().size());
         }
      }
   }

   public void printSessionTrace(MapleClient c) {
      StringBuilder str = new StringBuilder("Opened server sessions:\r\n\r\n");

      if (!onlineClients.isEmpty()) {
         List<Entry<Integer, MapleClient>> elist = new ArrayList<>(onlineClients.entrySet());
         elist.sort(Entry.comparingByKey());

         str.append("Current online clients:\r\n");
         for (Entry<Integer, MapleClient> e : elist) {
            str.append("  ").append(e.getKey()).append("\r\n");
         }
      }

      if (!onlineRemoteHwids.isEmpty()) {
         List<String> slist = new ArrayList<>(onlineRemoteHwids);
         Collections.sort(slist);

         str.append("Current online HWIDs:\r\n");
         for (String s : slist) {
            str.append("  ").append(s).append("\r\n");
         }
      }

      if (!loginRemoteHosts.isEmpty()) {
         List<Entry<String, Set<IoSession>>> elist = new ArrayList<>(loginRemoteHosts.entrySet());

         elist.sort(Entry.comparingByKey());

         str.append("Current login sessions:\r\n");
         for (Entry<String, Set<IoSession>> e : elist) {
            str.append("  ").append(e.getKey()).append(", IP: ").append(e.getValue()).append("\r\n");
         }
      }

      c.getAbstractPlayerInteraction().npcTalk(2140000, str.toString());
   }

   public enum AntiMultiClientResult {
      SUCCESS,
      REMOTE_LOGGEDIN,
      REMOTE_REACHED_LIMIT,
      REMOTE_PROCESSING,
      REMOTE_NO_MATCH,
      MANY_ACCOUNT_ATTEMPTS,
      COORDINATOR_ERROR
   }
}
