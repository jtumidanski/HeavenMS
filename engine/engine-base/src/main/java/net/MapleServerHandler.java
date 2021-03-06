package net;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.atomic.AtomicLong;

import com.ms.logs.LogType;
import com.ms.logs.LoggerOriginator;
import com.ms.logs.LoggerUtil;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;

import client.MapleClient;
import config.YamlConfig;
import constants.ServerConstants;
import net.server.Server;
import net.server.audit.LockCollector;
import net.server.audit.locks.MonitoredLockType;
import net.server.audit.locks.MonitoredReentrantLock;
import net.server.audit.locks.factory.MonitoredReentrantLockFactory;
import net.server.coordinator.session.MapleSessionCoordinator;
import server.TimerManager;
import tools.MapleAESOFB;
import tools.MapleLogger;
import tools.PacketCreator;
import tools.data.input.ByteArrayByteStream;
import tools.data.input.GenericSeekableLittleEndianAccessor;
import tools.data.input.SeekableLittleEndianAccessor;
import tools.packet.Ping;

public class MapleServerHandler extends IoHandlerAdapter {
   private final static Set<Short> ignoredDebugRecvPackets = new HashSet<>(Arrays.asList((short) 167, (short) 197, (short) 89, (short) 91, (short) 41, (short) 188, (short) 107));
   private static final SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm");
   private static AtomicLong sessionId = new AtomicLong(7777);
   private PacketProcessor processor;
   private int world = -1, channel = -1;
   private MonitoredReentrantLock idleLock = MonitoredReentrantLockFactory.createLock(MonitoredLockType.SERVER_HANDLER_IDLE, true);
   private MonitoredReentrantLock tempLock = MonitoredReentrantLockFactory.createLock(MonitoredLockType.SERVER_HANDLER_TEMP, true);
   private Map<MapleClient, Long> idleSessions = new HashMap<>(100);
   private Map<MapleClient, Long> tempIdleSessions = new HashMap<>();
   private ScheduledFuture<?> idleManager = null;

   public MapleServerHandler() {
      this.processor = PacketProcessor.getProcessor(-1, -1);

      idleManagerTask();
   }

   public MapleServerHandler(int world, int channel) {
      this.processor = PacketProcessor.getProcessor(world, channel);
      this.world = world;
      this.channel = channel;

      idleManagerTask();
   }

   @Override
   public void exceptionCaught(IoSession session, Throwable cause) {
      if (cause instanceof IOException) {
         closeMapleSession(session);
      } else {
         MapleClient client = (MapleClient) session.getAttribute(MapleClient.CLIENT_KEY);

         if (client != null && client.getPlayer() != null) {
            LoggerUtil.printError(LoggerOriginator.ENGINE, LogType.EXCEPTION_CAUGHT, cause, "Exception caught by: " + client.getPlayer());
         }
      }
   }

   private boolean isLoginServerHandler() {
      return channel == -1 && world == -1;
   }

   @Override
   public void sessionOpened(IoSession session) {
      String remoteHost;
      try {
         remoteHost = ((InetSocketAddress) session.getRemoteAddress()).getAddress().getHostAddress();

         if (remoteHost == null) {
            remoteHost = "null";
         }
      } catch (NullPointerException npe) {
         remoteHost = "null";
      }

      session.setAttribute(MapleClient.CLIENT_REMOTE_ADDRESS, remoteHost);

      if (!Server.getInstance().isOnline()) {
         MapleSessionCoordinator.getInstance().closeSession(session, true);
         return;
      }

      if (!isLoginServerHandler()) {
         if (Server.getInstance().getChannel(world, channel) == null) {
            MapleSessionCoordinator.getInstance().closeSession(session, true);
            return;
         }
      } else {
         if (!MapleSessionCoordinator.getInstance().canStartLoginSession(session)) {
            return;
         }
         LoggerUtil.printInfo(LoggerOriginator.ENGINE, LogType.SESSION, "IoSession with " + session.getRemoteAddress() + " opened on " + sdf.format(Calendar.getInstance().getTime()));
      }

      byte[] ivRecv = {70, 114, 122, 82};
      byte[] ivSend = {82, 48, 120, 115};
      ivRecv[3] = (byte) (Math.random() * 255);
      ivSend[3] = (byte) (Math.random() * 255);
      MapleAESOFB sendCypher = new MapleAESOFB(ivSend, (short) (0xFFFF - ServerConstants.VERSION));
      MapleAESOFB recvCypher = new MapleAESOFB(ivRecv, ServerConstants.VERSION);
      MapleClient client = new MapleClient(sendCypher, recvCypher, session);
      client.setWorld(world);
      client.setChannel(channel);
      client.setSessionId(sessionId.getAndIncrement()); // Generates a reasonable session id.
      session.write(PacketCreator.getHello(ServerConstants.VERSION, ivSend, ivRecv));
      session.setAttribute(MapleClient.CLIENT_KEY, client);
   }

   private void closeMapleSession(IoSession session) {
      if (isLoginServerHandler()) {
         MapleSessionCoordinator.getInstance().closeLoginSession(session);
      } else {
         MapleSessionCoordinator.getInstance().closeSession(session, null);
      }

      MapleClient client = (MapleClient) session.getAttribute(MapleClient.CLIENT_KEY);
      if (client != null) {
         try {
            if (!session.containsAttribute(MapleClient.CLIENT_TRANSITION)) {
               client.disconnect(false, false);
            }
         } catch (Throwable t) {
            LoggerUtil.printError(LoggerOriginator.ENGINE, LogType.ACCOUNT_STUCK, t);
         } finally {
            session.closeNow();
            session.removeAttribute(MapleClient.CLIENT_KEY);
         }
      }
   }

   @Override
   public void sessionClosed(IoSession session) throws Exception {
      closeMapleSession(session);
      super.sessionClosed(session);
   }

   @Override
   public void messageReceived(IoSession session, Object message) {
      byte[] content = (byte[]) message;
      SeekableLittleEndianAccessor accessor = new GenericSeekableLittleEndianAccessor(new ByteArrayByteStream(content));
      short packetId = accessor.readShort();
      MapleClient client = (MapleClient) session.getAttribute(MapleClient.CLIENT_KEY);

      final MaplePacketHandler packetHandler = processor.getHandler(packetId);

      if (YamlConfig.config.server.USE_DEBUG_SHOW_RCVD_PACKET && !ignoredDebugRecvPackets.contains(packetId)) {
         LoggerUtil.printDebug(LoggerOriginator.ENGINE, "Received packet id " + packetId);
      }

      if (packetHandler != null && packetHandler.validateState(client)) {
         try {
            MapleLogger.logRecv(client, packetId, message);
            packetHandler.handlePacket(accessor, client);
         } catch (final Throwable t) {
            LoggerUtil.printError(LoggerOriginator.ENGINE, LogType.PACKET_HANDLER, t, "Error for " + (client.getPlayer() == null ? "" : "player ; " + client.getPlayer() + " on map ; " + client.getPlayer().getMapId() + " - ") + "account ; " + client.getAccountName() + "\r\n" + accessor.toString());
            //client.announce(MaplePacketCreator.enableActions());//bugs sometimes
         }
         client.updateLastPacket();
      }
   }

   @Override
   public void messageSent(IoSession session, Object message) {
      byte[] content = (byte[]) message;
      SeekableLittleEndianAccessor accessor = new GenericSeekableLittleEndianAccessor(new ByteArrayByteStream(content));
      accessor.readShort(); //packetId
   }

   @Override
   public void sessionIdle(final IoSession session, final IdleStatus status) throws Exception {
      MapleClient client = (MapleClient) session.getAttribute(MapleClient.CLIENT_KEY);
      if (client != null) {
         registerIdleSession(client);
      }
      super.sessionIdle(session, status);
   }

   private void registerIdleSession(MapleClient c) {
      if (idleLock.tryLock()) {
         try {
            idleSessions.put(c, Server.getInstance().getCurrentTime());
            PacketCreator.announce(c, new Ping());
         } finally {
            idleLock.unlock();
         }
      } else {
         tempLock.lock();
         try {
            tempIdleSessions.put(c, Server.getInstance().getCurrentTime());
            PacketCreator.announce(c, new Ping());
         } finally {
            tempLock.unlock();
         }
      }
   }

   private void manageIdleSessions() {
      long timeNow = Server.getInstance().getCurrentTime();
      long timeThen = timeNow - 15000;

      Set<MapleClient> pingClients = new HashSet<>();
      idleLock.lock();
      try {
         for (Entry<MapleClient, Long> mc : idleSessions.entrySet()) {
            if (timeNow - mc.getValue() >= 15000) {
               pingClients.add(mc.getKey());
            }
         }

         idleSessions.clear();

         if (!tempIdleSessions.isEmpty()) {
            tempLock.lock();
            try {
               for (Entry<MapleClient, Long> mc : tempIdleSessions.entrySet()) {
                  idleSessions.put(mc.getKey(), mc.getValue());
               }

               tempIdleSessions.clear();
            } finally {
               tempLock.unlock();
            }
         }
      } finally {
         idleLock.unlock();
      }

      for (MapleClient c : pingClients) {
         c.testPing(timeThen);
      }
   }

   private void idleManagerTask() {
      this.idleManager = TimerManager.getInstance().register(this::manageIdleSessions, 10000);
   }

   private void cancelIdleManagerTask() {
      this.idleManager.cancel(false);
      this.idleManager = null;
   }

   private void disposeLocks() {
      LockCollector.getInstance().registerDisposeAction(this::emptyLocks);
   }

   private void emptyLocks() {
      idleLock.dispose();
      tempLock.dispose();
   }

   public void dispose() {
      cancelIdleManagerTask();

      idleLock.lock();
      try {
         idleSessions.clear();
      } finally {
         idleLock.unlock();
      }

      tempLock.lock();
      try {
         tempIdleSessions.clear();
      } finally {
         tempLock.unlock();
      }

      disposeLocks();
   }
}
