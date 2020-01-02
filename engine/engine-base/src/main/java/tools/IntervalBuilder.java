package tools;

import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.List;

import net.server.audit.locks.MonitoredLockType;
import net.server.audit.locks.MonitoredReadLock;
import net.server.audit.locks.MonitoredReentrantReadWriteLock;
import net.server.audit.locks.MonitoredWriteLock;
import net.server.audit.locks.factory.MonitoredReadLockFactory;
import net.server.audit.locks.factory.MonitoredWriteLockFactory;

public class IntervalBuilder {

   protected MonitoredReadLock monitoredReadLock;
   protected MonitoredWriteLock monitoredWriteLock;
   private List<Line2D> intervalLimits = new ArrayList<>();

   public IntervalBuilder() {
      MonitoredReentrantReadWriteLock locks = new MonitoredReentrantReadWriteLock(MonitoredLockType.INTERVAL, true);
      monitoredReadLock = MonitoredReadLockFactory.createLock(locks);
      monitoredWriteLock = MonitoredWriteLockFactory.createLock(locks);
   }

   private void refitOverlappedIntervals(int st, int en, int newFrom, int newTo) {
      List<Line2D> checkLimits = new ArrayList<>(intervalLimits.subList(st, en));

      float newLimitX1, newLimitX2;
      if (!checkLimits.isEmpty()) {
         Line2D firstLimit = checkLimits.get(0);
         Line2D lastLimit = checkLimits.get(checkLimits.size() - 1);

         newLimitX1 = (float) ((newFrom < firstLimit.getX1()) ? newFrom : firstLimit.getX1());
         newLimitX2 = (float) ((newTo > lastLimit.getX2()) ? newTo : lastLimit.getX2());

         for (Line2D limit : checkLimits) {
            intervalLimits.remove(st);
         }
      } else {
         newLimitX1 = newFrom;
         newLimitX2 = newTo;
      }

      intervalLimits.add(st, new Line2D.Float(newLimitX1, 0, newLimitX2, 0));
   }

   private int bsearchInterval(int point) {
      int st = 0, en = intervalLimits.size() - 1;

      int mid, idx;
      while (en >= st) {
         idx = (st + en) / 2;
         mid = (int) intervalLimits.get(idx).getX1();

         if (mid == point) {
            return idx;
         } else if (mid < point) {
            st = idx + 1;
         } else {
            en = idx - 1;
         }
      }

      return en;
   }

   public void addInterval(int from, int to) {
      monitoredWriteLock.lock();
      try {
         int st = bsearchInterval(from);
         if (st < 0) {
            st = 0;
         } else if (intervalLimits.get(st).getX2() < from) {
            st += 1;
         }

         int en = bsearchInterval(to);
         if (en < st) {
            en = st - 1;
         }

         refitOverlappedIntervals(st, en + 1, from, to);
      } finally {
         monitoredWriteLock.unlock();
      }
   }

   public boolean inInterval(int point) {
      return inInterval(point, point);
   }

   public boolean inInterval(int from, int to) {
      monitoredReadLock.lock();
      try {
         int idx = bsearchInterval(from);
         return idx >= 0 && to <= intervalLimits.get(idx).getX2();
      } finally {
         monitoredReadLock.unlock();
      }
   }

   public void clear() {
      monitoredWriteLock.lock();
      try {
         intervalLimits.clear();
      } finally {
         monitoredWriteLock.unlock();
      }
   }

}
