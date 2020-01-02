package server.maps;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.locks.Lock;

import client.MapleCharacter;
import net.server.audit.locks.MonitoredLockType;
import net.server.audit.locks.factory.MonitoredReentrantLockFactory;
import server.TimerManager;
import tools.PacketCreator;
import tools.packet.ui.GetClock;
import tools.packet.ui.StopClock;

public class MapleMiniDungeon {
   List<MapleCharacter> players = new ArrayList<>();
   ScheduledFuture<?> timeoutTask;
   Lock lock = MonitoredReentrantLockFactory.createLock(MonitoredLockType.MINI_DUNGEON, true);

   int baseMap;
   long expireTime;

   public MapleMiniDungeon(int base, long timeLimit) {
      baseMap = base;
      expireTime = timeLimit * 1000;

      timeoutTask = TimerManager.getInstance().schedule(this::close, expireTime);

      expireTime += System.currentTimeMillis();
   }

   public boolean registerPlayer(MapleCharacter chr) {
      int time = (int) ((expireTime - System.currentTimeMillis()) / 1000);
      if (time > 0) {
         PacketCreator.announce(chr, new GetClock(time));
      }

      lock.lock();
      try {
         if (timeoutTask == null) {
            return false;
         }

         players.add(chr);
      } finally {
         lock.unlock();
      }

      return true;
   }

   public boolean unregisterPlayer(MapleCharacter chr) {
      PacketCreator.announce(chr, new StopClock());

      lock.lock();
      try {
         players.remove(chr);

         if (players.isEmpty()) {
            dispose();
            return false;
         }
      } finally {
         lock.unlock();
      }

      if (chr.isPartyLeader()) {
         close();
      }

      return true;
   }

   public void close() {
      lock.lock();
      try {
         List<MapleCharacter> characters = new ArrayList<>(players);

         for (MapleCharacter chr : characters) {
            chr.changeMap(baseMap);
         }

         dispose();
         timeoutTask = null;
      } finally {
         lock.unlock();
      }
   }

   public void dispose() {
      lock.lock();
      try {
         players.clear();

         if (timeoutTask != null) {
            timeoutTask.cancel(false);
            timeoutTask = null;
         }
      } finally {
         lock.unlock();
      }
   }
}
