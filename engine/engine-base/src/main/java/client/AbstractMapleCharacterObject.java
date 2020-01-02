package client;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;

import config.YamlConfig;
import constants.game.GameConstants;
import net.server.audit.locks.MonitoredLockType;
import net.server.audit.locks.MonitoredReadLock;
import net.server.audit.locks.MonitoredReentrantReadWriteLock;
import net.server.audit.locks.MonitoredWriteLock;
import net.server.audit.locks.factory.MonitoredReadLockFactory;
import net.server.audit.locks.factory.MonitoredReentrantLockFactory;
import net.server.audit.locks.factory.MonitoredWriteLockFactory;
import server.maps.AbstractAnimatedMapleMapObject;
import server.maps.MapleMap;

public abstract class AbstractMapleCharacterObject extends AbstractAnimatedMapleMapObject {
   protected MapleMap map;
   protected int str, dex, luk, int_, hp, maxHp, mp, maxMp;
   protected int hpMpApUsed, remainingAp;
   protected int[] remainingSp = new int[10];
   protected transient int clientMaxHp, clientMaxMp, localMaxHp = 50, localMaxMp = 5;
   protected float transientHp = Float.NEGATIVE_INFINITY, transientMp = Float.NEGATIVE_INFINITY;
   protected Map<MapleStat, Integer> statUpdates = new HashMap<>();
   protected Lock effLock = MonitoredReentrantLockFactory.createLock(MonitoredLockType.CHARACTER_EFF, true);
   protected MonitoredReadLock statReadLock;
   protected MonitoredWriteLock statWriteLock;
   private AbstractCharacterListener listener = null;

   protected AbstractMapleCharacterObject() {
      MonitoredReentrantReadWriteLock locks = new MonitoredReentrantReadWriteLock(MonitoredLockType.CHARACTER_STA, true);
      statReadLock = MonitoredReadLockFactory.createLock(locks);
      statWriteLock = MonitoredWriteLockFactory.createLock(locks);

      Arrays.fill(remainingSp, 0);
   }

   private static long calcStatPoolNode(long v, int displacement) {
      if (v > Short.MAX_VALUE) {
         v = Short.MAX_VALUE;
      } else if (v < Short.MIN_VALUE) {
         v = Short.MIN_VALUE;
      }

      return ((v & 0x0FFFF) << displacement);
   }

   private static long calcStatPoolLong(int v1, int v2, int v3, int v4) {
      long ret = 0;

      ret |= calcStatPoolNode(v1, 48);
      ret |= calcStatPoolNode(v2, 32);
      ret |= calcStatPoolNode(v3, 16);
      ret |= calcStatPoolNode(v4, 0);

      return ret;
   }

   private static int apAssigned(int x) {
      return x != Short.MIN_VALUE ? x : 0;
   }

   protected void setListener(AbstractCharacterListener listener) {
      this.listener = listener;
   }

   public MapleMap getMap() {
      return map;
   }

   public void setMap(MapleMap map) {
      this.map = map;
   }

   public int getStr() {
      statReadLock.lock();
      try {
         return str;
      } finally {
         statReadLock.unlock();
      }
   }

   private void setStr(int str) {
      this.str = str;
   }

   public int getDex() {
      statReadLock.lock();
      try {
         return dex;
      } finally {
         statReadLock.unlock();
      }
   }

   private void setDex(int dex) {
      this.dex = dex;
   }

   public int getInt() {
      statReadLock.lock();
      try {
         return int_;
      } finally {
         statReadLock.unlock();
      }
   }

   private void setInt(int int_) {
      this.int_ = int_;
   }

   public int getLuk() {
      statReadLock.lock();
      try {
         return luk;
      } finally {
         statReadLock.unlock();
      }
   }

   private void setLuk(int luk) {
      this.luk = luk;
   }

   public int getRemainingAp() {
      statReadLock.lock();
      try {
         return remainingAp;
      } finally {
         statReadLock.unlock();
      }
   }

   public void setRemainingAp(int remainingAp) {
      this.remainingAp = remainingAp;
   }

   protected int getRemainingSp(int jobId) {
      statReadLock.lock();
      try {
         return remainingSp[GameConstants.getSkillBook(jobId)];
      } finally {
         statReadLock.unlock();
      }
   }

   public int[] getRemainingSps() {
      statReadLock.lock();
      try {
         return Arrays.copyOf(remainingSp, remainingSp.length);
      } finally {
         statReadLock.unlock();
      }
   }

   public int getHpMpApUsed() {
      statReadLock.lock();
      try {
         return hpMpApUsed;
      } finally {
         statReadLock.unlock();
      }
   }

   public void setHpMpApUsed(int mpApUsed) {
      this.hpMpApUsed = mpApUsed;
   }

   public boolean isAlive() {
      statReadLock.lock();
      try {
         return hp > 0;
      } finally {
         statReadLock.unlock();
      }
   }

   public int getHp() {
      statReadLock.lock();
      try {
         return hp;
      } finally {
         statReadLock.unlock();
      }
   }

   protected void setHp(int newHp) {
      int oldHp = hp;

      int thp = newHp;
      if (thp < 0) {
         thp = 0;
      } else if (thp > localMaxHp) {
         thp = localMaxHp;
      }

      if (this.hp != thp) {
         this.transientHp = Float.NEGATIVE_INFINITY;
      }
      this.hp = thp;

      dispatchHpChanged(oldHp);
   }

   public int getMp() {
      statReadLock.lock();
      try {
         return mp;
      } finally {
         statReadLock.unlock();
      }
   }

   protected void setMp(int newMp) {
      int tmp = newMp;
      if (tmp < 0) {
         tmp = 0;
      } else if (tmp > localMaxMp) {
         tmp = localMaxMp;
      }

      if (this.mp != tmp) {
         this.transientMp = Float.NEGATIVE_INFINITY;
      }
      this.mp = tmp;
   }

   public int getMaxHp() {
      statReadLock.lock();
      try {
         return maxHp;
      } finally {
         statReadLock.unlock();
      }
   }

   public void setMaxHp(int hp_) {
      if (this.maxHp < hp_) {
         this.transientHp = Float.NEGATIVE_INFINITY;
      }
      this.maxHp = hp_;
      this.clientMaxHp = Math.min(30000, hp_);
   }

   public int getMaxMp() {
      statReadLock.lock();
      try {
         return maxMp;
      } finally {
         statReadLock.unlock();
      }
   }

   public void setMaxMp(int mp_) {
      if (this.maxMp < mp_) {
         this.transientMp = Float.NEGATIVE_INFINITY;
      }
      this.maxMp = mp_;
      this.clientMaxMp = Math.min(30000, mp_);
   }

   public int getClientMaxHp() {
      return clientMaxHp;
   }

   public int getClientMaxMp() {
      return clientMaxMp;
   }

   public int getCurrentMaxHp() {
      return localMaxHp;
   }

   public int getCurrentMaxMp() {
      return localMaxMp;
   }

   private void dispatchHpChanged(final int oldHp) {
      listener.onHpChanged(oldHp);
   }

   private void dispatchHpMpPoolUpdated() {
      listener.onHpMpPoolUpdate();
   }

   private void dispatchStatUpdated() {
      listener.onStatUpdate();
   }

   private void dispatchStatPoolUpdateAnnounced() {
      listener.onAnnounceStatPoolUpdate();
   }

   public void setRemainingSp(int remainingSp, int skillBook) {
      this.remainingSp[skillBook] = remainingSp;
   }

   private void changeStatPool(Long hpMpPool, Long strDexIntLuk, Long newSp, int newAp, boolean silent) {
      effLock.lock();
      statWriteLock.lock();
      try {
         statUpdates.clear();
         boolean poolUpdate = false;
         boolean statUpdate = false;

         if (hpMpPool != null) {
            short newHp = (short) (hpMpPool >> 48);
            short newMp = (short) (hpMpPool >> 32);
            short newMaxHp = (short) (hpMpPool >> 16);
            short newMaxMp = hpMpPool.shortValue();

            if (newMaxHp != Short.MIN_VALUE) {
               if (newMaxHp < 50) {
                  newMaxHp = 50;
               }

               poolUpdate = true;
               setMaxHp(newMaxHp);
               statUpdates.put(MapleStat.MAX_HP, clientMaxHp);
               statUpdates.put(MapleStat.HP, hp);
            }

            if (newHp != Short.MIN_VALUE) {
               setHp(newHp);
               statUpdates.put(MapleStat.HP, hp);
            }

            if (newMaxMp != Short.MIN_VALUE) {
               if (newMaxMp < 5) {
                  newMaxMp = 5;
               }

               poolUpdate = true;
               setMaxMp(newMaxMp);
               statUpdates.put(MapleStat.MAX_MP, clientMaxMp);
               statUpdates.put(MapleStat.MP, mp);
            }

            if (newMp != Short.MIN_VALUE) {
               setMp(newMp);
               statUpdates.put(MapleStat.MP, mp);
            }
         }

         if (strDexIntLuk != null) {
            short newStr = (short) (strDexIntLuk >> 48);
            short newDex = (short) (strDexIntLuk >> 32);
            short newInt = (short) (strDexIntLuk >> 16);
            short newLuk = strDexIntLuk.shortValue();

            if (newStr >= 4) {
               setStr(newStr);
               statUpdates.put(MapleStat.STR, str);
            }

            if (newDex >= 4) {
               setDex(newDex);
               statUpdates.put(MapleStat.DEX, dex);
            }

            if (newInt >= 4) {
               setInt(newInt);
               statUpdates.put(MapleStat.INT, int_);
            }

            if (newLuk >= 4) {
               setLuk(newLuk);
               statUpdates.put(MapleStat.LUK, luk);
            }

            if (newAp >= 0) {
               setRemainingAp(newAp);
               statUpdates.put(MapleStat.AVAILABLE_AP, remainingAp);
            }

            statUpdate = true;
         }

         if (newSp != null) {
            short sp = (short) (newSp >> 16);
            short skillBook = newSp.shortValue();

            setRemainingSp(sp, skillBook);
            statUpdates.put(MapleStat.AVAILABLE_SP, remainingSp[skillBook]);
         }

         if (!statUpdates.isEmpty()) {
            if (poolUpdate) {
               dispatchHpMpPoolUpdated();
            }

            if (statUpdate) {
               dispatchStatUpdated();
            }

            if (!silent) {
               dispatchStatPoolUpdateAnnounced();
            }
         }
      } finally {
         statWriteLock.unlock();
         effLock.unlock();
      }
   }

   public void healHpMp() {
      updateHpMp(30000);
   }

   public void updateHpMp(int x) {
      updateHpMp(x, x);
   }

   public void updateHpMp(int newHp, int newMp) {
      changeHpMp(newHp, newMp, false);
   }

   public void changeHpMp(int newHp, int newMp, boolean silent) {
      changeHpMpPool(newHp, newMp, Short.MIN_VALUE, Short.MIN_VALUE, silent);
   }

   private void changeHpMpPool(int hp, int mp, int maxHp, int maxMp, boolean silent) {
      long hpMpPool = calcStatPoolLong(hp, mp, maxHp, maxMp);
      changeStatPool(hpMpPool, null, null, -1, silent);
   }

   public void updateHp(int hp) {
      updateHpMaxHp(hp, Short.MIN_VALUE);
   }

   public void updateMaxHp(int maxHp) {
      updateHpMaxHp(Short.MIN_VALUE, maxHp);
   }

   public void updateHpMaxHp(int hp, int maxHp) {
      changeHpMpPool(hp, Short.MIN_VALUE, maxHp, Short.MIN_VALUE, false);
   }

   public void updateMp(int mp) {
      updateMpMaxMp(mp, Short.MIN_VALUE);
   }

   public void updateMaxMp(int maxMp) {
      updateMpMaxMp(Short.MIN_VALUE, maxMp);
   }

   public void updateMpMaxMp(int mp, int maxMp) {
      changeHpMpPool(Short.MIN_VALUE, mp, Short.MIN_VALUE, maxMp, false);
   }

   public void updateMaxHpMaxMp(int maxHp, int maxMp) {
      changeHpMpPool(Short.MIN_VALUE, Short.MIN_VALUE, maxHp, maxMp, false);
   }

   protected void enforceMaxHpMp() {
      effLock.lock();
      statWriteLock.lock();
      try {
         if (mp > localMaxMp || hp > localMaxHp) {
            changeHpMp(hp, mp, false);
         }
      } finally {
         statWriteLock.unlock();
         effLock.unlock();
      }
   }

   public int safeAddHP(int delta) {
      effLock.lock();
      statWriteLock.lock();
      try {
         if (hp + delta <= 0) {
            delta = -hp + 1;
         }

         addHP(delta);
         return delta;
      } finally {
         statWriteLock.unlock();
         effLock.unlock();
      }
   }

   public void addHP(int delta) {
      effLock.lock();
      statWriteLock.lock();
      try {
         updateHp(hp + delta);
      } finally {
         statWriteLock.unlock();
         effLock.unlock();
      }
   }

   public void addMP(int delta) {
      effLock.lock();
      statWriteLock.lock();
      try {
         updateMp(mp + delta);
      } finally {
         statWriteLock.unlock();
         effLock.unlock();
      }
   }

   public void addHpMp(int hpDelta, int mpDelta) {
      effLock.lock();
      statWriteLock.lock();
      try {
         updateHpMp(hp + hpDelta, mp + mpDelta);
      } finally {
         statWriteLock.unlock();
         effLock.unlock();
      }
   }

   protected void addMaxMPMaxHP(int hpDelta, int mpDelta, boolean silent) {
      effLock.lock();
      statWriteLock.lock();
      try {
         changeHpMpPool(Short.MIN_VALUE, Short.MIN_VALUE, maxHp + hpDelta, maxMp + mpDelta, silent);
      } finally {
         statWriteLock.unlock();
         effLock.unlock();
      }
   }

   public void addMaxHP(int delta) {
      effLock.lock();
      statWriteLock.lock();
      try {
         updateMaxHp(maxHp + delta);
      } finally {
         statWriteLock.unlock();
         effLock.unlock();
      }
   }

   public void addMaxMP(int delta) {
      effLock.lock();
      statWriteLock.lock();
      try {
         updateMaxMp(maxMp + delta);
      } finally {
         statWriteLock.unlock();
         effLock.unlock();
      }
   }

   public boolean assignStr(int x) {
      return assignStrDexIntLuk(x, Short.MIN_VALUE, Short.MIN_VALUE, Short.MIN_VALUE);
   }

   public boolean assignDex(int x) {
      return assignStrDexIntLuk(Short.MIN_VALUE, x, Short.MIN_VALUE, Short.MIN_VALUE);
   }

   public boolean assignInt(int x) {
      return assignStrDexIntLuk(Short.MIN_VALUE, Short.MIN_VALUE, x, Short.MIN_VALUE);
   }

   public boolean assignLuk(int x) {
      return assignStrDexIntLuk(Short.MIN_VALUE, Short.MIN_VALUE, Short.MIN_VALUE, x);
   }

   public boolean assignHP(int deltaHP, int deltaAp) {
      effLock.lock();
      statWriteLock.lock();
      try {
         if (remainingAp - deltaAp < 0 || hpMpApUsed + deltaAp < 0 || maxHp >= 30000) {
            return false;
         }

         long hpMpPool = calcStatPoolLong(Short.MIN_VALUE, Short.MIN_VALUE, maxHp + deltaHP, maxMp);
         long strDexIntLuk = calcStatPoolLong(str, dex, int_, luk);

         changeStatPool(hpMpPool, strDexIntLuk, null, remainingAp - deltaAp, false);
         setHpMpApUsed(hpMpApUsed + deltaAp);
         return true;
      } finally {
         statWriteLock.unlock();
         effLock.unlock();
      }
   }

   public boolean assignMP(int deltaMP, int deltaAp) {
      effLock.lock();
      statWriteLock.lock();
      try {
         if (remainingAp - deltaAp < 0 || hpMpApUsed + deltaAp < 0 || maxMp >= 30000) {
            return false;
         }

         long hpMpPool = calcStatPoolLong(Short.MIN_VALUE, Short.MIN_VALUE, maxHp, maxMp + deltaMP);
         long strDexIntLuk = calcStatPoolLong(str, dex, int_, luk);

         changeStatPool(hpMpPool, strDexIntLuk, null, remainingAp - deltaAp, false);
         setHpMpApUsed(hpMpApUsed + deltaAp);
         return true;
      } finally {
         statWriteLock.unlock();
         effLock.unlock();
      }
   }

   public boolean assignStrDexIntLuk(int deltaStr, int deltaDex, int deltaInt, int deltaLuk) {
      effLock.lock();
      statWriteLock.lock();
      try {
         int apUsed = apAssigned(deltaStr) + apAssigned(deltaDex) + apAssigned(deltaInt) + apAssigned(deltaLuk);
         if (apUsed > remainingAp) {
            return false;
         }

         int newStr = str + deltaStr, newDex = dex + deltaDex, newInt = int_ + deltaInt, newLuk = luk + deltaLuk;
         if (outOfRange(newStr, deltaStr)) {
            return false;
         }

         if (outOfRange(newDex, deltaDex)) {
            return false;
         }

         if (outOfRange(newInt, deltaInt)) {
            return false;
         }

         if (outOfRange(newLuk, deltaLuk)) {
            return false;
         }

         int newAp = remainingAp - apUsed;
         updateStrDexIntLuk(newStr, newDex, newInt, newLuk, newAp);
         return true;
      } finally {
         statWriteLock.unlock();
         effLock.unlock();
      }
   }

   public boolean outOfRange(int newAp, int deltaAp) {
      return newAp < 4 && deltaAp != Short.MIN_VALUE || newAp > YamlConfig.config.server.MAX_AP;
   }

   public void updateStrDexIntLuk(int x) {
      updateStrDexIntLuk(x, x, x, x, -1);
   }

   public void changeRemainingAp(int x, boolean silent) {
      effLock.lock();
      statWriteLock.lock();
      try {
         changeStrDexIntLuk(str, dex, int_, luk, x, silent);
      } finally {
         statWriteLock.unlock();
         effLock.unlock();
      }
   }

   public void gainAp(int deltaAp, boolean silent) {
      effLock.lock();
      statWriteLock.lock();
      try {
         changeRemainingAp(Math.max(0, remainingAp + deltaAp), silent);
      } finally {
         statWriteLock.unlock();
         effLock.unlock();
      }
   }

   protected void updateStrDexIntLuk(int str, int dex, int int_, int luk, int remainingAp) {
      changeStrDexIntLuk(str, dex, int_, luk, remainingAp, false);
   }

   private void changeStrDexIntLuk(int str, int dex, int int_, int luk, int remainingAp, boolean silent) {
      long strDexIntLuk = calcStatPoolLong(str, dex, int_, luk);
      changeStatPool(null, strDexIntLuk, null, remainingAp, silent);
   }

   private void changeStrDexIntLukSp(int str, int dex, int int_, int luk, int remainingAp, int remainingSp, int skillBook, boolean silent) {
      long strDexIntLuk = calcStatPoolLong(str, dex, int_, luk);
      long sp = calcStatPoolLong(0, 0, remainingSp, skillBook);
      changeStatPool(null, strDexIntLuk, sp, remainingAp, silent);
   }

   protected void updateStrDexIntLukSp(int str, int dex, int int_, int luk, int remainingAp, int remainingSp, int skillBook) {
      changeStrDexIntLukSp(str, dex, int_, luk, remainingAp, remainingSp, skillBook, false);
   }

   protected void setRemainingSp(int[] sps) {
      effLock.lock();
      statWriteLock.lock();
      try {
         System.arraycopy(sps, 0, remainingSp, 0, sps.length);
      } finally {
         statWriteLock.unlock();
         effLock.unlock();
      }
   }

   protected void updateRemainingSp(int remainingSp, int skillBook) {
      changeRemainingSp(remainingSp, skillBook, false);
   }

   protected void changeRemainingSp(int remainingSp, int skillBook, boolean silent) {
      long sp = calcStatPoolLong(0, 0, remainingSp, skillBook);
      changeStatPool(null, null, sp, Short.MIN_VALUE, silent);
   }

   public void gainSp(int deltaSp, int skillBook, boolean silent) {
      effLock.lock();
      statWriteLock.lock();
      try {
         changeRemainingSp(Math.max(0, remainingSp[skillBook] + deltaSp), skillBook, silent);
      } finally {
         statWriteLock.unlock();
         effLock.unlock();
      }
   }
}
