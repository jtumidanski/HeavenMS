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
package server.maps;

import java.awt.Rectangle;
import java.util.List;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.locks.Lock;

import client.MapleClient;
import config.YamlConfig;
import net.server.audit.locks.MonitoredLockType;
import net.server.audit.locks.factory.MonitoredReentrantLockFactory;
import scripting.reactor.ReactorScriptManager;
import server.TimerManager;
import server.maps.spawner.ReactorSpawnAndDestroyer;
import server.partyquest.GuardianSpawnPoint;
import tools.MasterBroadcaster;
import tools.MessageBroadcaster;
import tools.Pair;
import tools.ServerNoticeType;
import tools.packet.reactor.DestroyReactor;
import tools.packet.reactor.TriggerReactor;

/**
 * @author Lerk
 * @author Ronan
 */
public class MapleReactor extends AbstractMapleMapObject {

   private int rid;
   private MapleReactorStats stats;
   private byte state;
   private byte evstate;
   private int delay;
   private MapleMap map;
   private String name;
   private boolean alive;
   private boolean shouldCollect;
   private boolean attackHit;
   private ScheduledFuture<?> timeoutTask = null;
   private Runnable delayedRespawnRun = null;
   private GuardianSpawnPoint guardian = null;
   private byte facingDirection = 0;
   private Lock reactorLock = MonitoredReentrantLockFactory.createLock(MonitoredLockType.REACTOR, true);
   private Lock hitLock = MonitoredReentrantLockFactory.createLock(MonitoredLockType.REACTOR_HIT, true);

   public MapleReactor(MapleReactorStats stats, int rid) {
      this.evstate = (byte) 0;
      this.stats = stats;
      this.rid = rid;
      this.alive = true;
   }

   public boolean getShouldCollect() {
      return shouldCollect;
   }

   public void setShouldCollect(boolean collect) {
      this.shouldCollect = collect;
   }

   public void lockReactor() {
      reactorLock.lock();
   }

   public void unlockReactor() {
      reactorLock.unlock();
   }

   public byte getState() {
      return state;
   }

   public void setState(byte state) {
      this.state = state;
   }

   public byte getEventState() {
      return evstate;
   }

   public void setEventState(byte substate) {
      this.evstate = substate;
   }

   public MapleReactorStats getStats() {
      return stats;
   }

   public int getId() {
      return rid;
   }

   public int getDelay() {
      return delay;
   }

   public void setDelay(int delay) {
      this.delay = delay;
   }

   @Override
   public MapleMapObjectType type() {
      return MapleMapObjectType.REACTOR;
   }

   public int getReactorType() {
      return stats.getType(state);
   }

   public boolean isRecentHitFromAttack() {
      return attackHit;
   }

   public MapleMap getMap() {
      return map;
   }

   public void setMap(MapleMap map) {
      this.map = map;
   }

   public Pair<Integer, Integer> getReactItem(byte index) {
      return stats.getReactItem(state, index);
   }

   public boolean isAlive() {
      return alive;
   }

   public void setAlive(boolean alive) {
      this.alive = alive;
   }

   public boolean isActive() {
      return alive && stats.getType(state) != -1;
   }

   public void resetReactorActions(int newState) {
      setState((byte) newState);
      cancelReactorTimeout();
      setShouldCollect(true);
      refreshReactorTimeout();

      if (map != null) {
         map.searchItemReactors(this);
      }
   }

   public void forceHitReactor(final byte newState) {
      this.lockReactor();
      try {
         this.resetReactorActions(newState);
         MasterBroadcaster.getInstance().sendToAllInMap(map, new TriggerReactor(this, (short) 0));
      } finally {
         this.unlockReactor();
      }
   }

   private void tryForceHitReactor(final byte newState) {  // weak hit state signal, if already changed reactor state before timeout then drop this
      if (!reactorLock.tryLock()) {
         return;
      }

      try {
         this.resetReactorActions(newState);
         MasterBroadcaster.getInstance().sendToAllInMap(map, new TriggerReactor(this, (short) 0));
      } finally {
         reactorLock.unlock();
      }
   }

   public void cancelReactorTimeout() {
      if (timeoutTask != null) {
         timeoutTask.cancel(false);
         timeoutTask = null;
      }
   }

   private void refreshReactorTimeout() {
      int timeOut = stats.getTimeout(state);
      if (timeOut > -1) {
         final byte nextState = stats.getTimeoutState(state);

         timeoutTask = TimerManager.getInstance().schedule(new Runnable() {
            @Override
            public void run() {
               timeoutTask = null;
               tryForceHitReactor(nextState);
            }
         }, timeOut);
      }
   }

   public void delayedHitReactor(final MapleClient c, long delay) {
      TimerManager.getInstance().schedule(new Runnable() {
         @Override
         public void run() {
            hitReactor(c);
         }
      }, delay);
   }

   public void hitReactor(MapleClient c) {
      hitReactor(false, 0, (short) 0, 0, c);
   }

   public void hitReactor(boolean wHit, int charPos, short stance, int skillid, MapleClient c) {
      try {
         if (!this.isActive()) {
            return;
         }

         if (hitLock.tryLock()) {
            this.lockReactor();
            try {
               cancelReactorTimeout();
               attackHit = wHit;

               if (YamlConfig.config.server.USE_DEBUG) {
                  MessageBroadcaster.getInstance().sendServerNotice(c.getPlayer(), ServerNoticeType.PINK_TEXT, "Hitted REACTOR " + this.getId() + " with POS " + charPos + " , STANCE " + stance + " , SkillID " + skillid + " , STATE " + stats.getType(state) + " STATESIZE " + stats.getStateSize(state));
               }
               ReactorScriptManager.getInstance().onHit(c, this);

               int reactorType = stats.getType(state);
               if (reactorType < 999 && reactorType != -1) {//type 2 = only hit from right (kerning swamp plants), 00 is air left 02 is ground left
                  if (!(reactorType == 2 && (stance == 0 || stance == 2))) { //get next state
                     for (byte b = 0; b < stats.getStateSize(state); b++) {//YAY?
                        List<Integer> activeSkills = stats.getActiveSkills(state, b);
                        if (activeSkills != null) {
                           if (!activeSkills.contains(skillid)) {
                              continue;
                           }
                        }
                        state = stats.getNextState(state, b);
                        if (stats.getNextState(state, b) == -1) {//end of reactor
                           if (reactorType < 100) {//reactor broken
                              if (delay > 0) {
                                 map.destroyReactor(this.objectId());
                              } else {//trigger as normal
                                 MasterBroadcaster.getInstance().sendToAllInMap(map, new TriggerReactor(this, stance));
                              }
                           } else {//item-triggered on final step
                              MasterBroadcaster.getInstance().sendToAllInMap(map, new TriggerReactor(this, stance));
                           }

                           ReactorScriptManager.getInstance().act(c, this);
                        } else { //reactor not broken yet
                           MasterBroadcaster.getInstance().sendToAllInMap(map, new TriggerReactor(this, stance));
                           if (state == stats.getNextState(state, b)) {//current state = next state, looping reactor
                              ReactorScriptManager.getInstance().act(c, this);
                           }

                           setShouldCollect(true);     // refresh collectability on item drop-based reactors
                           refreshReactorTimeout();
                           if (stats.getType(state) == 100) {
                              map.searchItemReactors(this);
                           }
                        }
                        break;
                     }
                  }
               } else {
                  state++;
                  MasterBroadcaster.getInstance().sendToAllInMap(map, new TriggerReactor(this, stance));
                  if (this.getId() != 9980000 && this.getId() != 9980001) {
                     ReactorScriptManager.getInstance().act(c, this);
                  }

                  setShouldCollect(true);
                  refreshReactorTimeout();
                  if (stats.getType(state) == 100) {
                     map.searchItemReactors(this);
                  }
               }
            } finally {
               this.unlockReactor();
               hitLock.unlock();   // non-encapsulated unlock found thanks to MiLin
            }
         }
      } catch (Exception e) {
         e.printStackTrace();
      }
   }

   public boolean destroy() {
      if (reactorLock.tryLock()) {
         try {
            boolean alive = this.isAlive();
            // reactor neither alive nor in delayed respawn, remove map object allowed
            if (alive) {
               this.setAlive(false);
               this.cancelReactorTimeout();

               if (this.getDelay() > 0) {
                  this.delayedRespawn();
               }
            } else {
               return !this.inDelayedRespawn();
            }
         } finally {
            reactorLock.unlock();
         }
      }

      MasterBroadcaster.getInstance().sendToAllInMap(map, new DestroyReactor(this));
      return false;
   }

   private void respawn() {
      this.lockReactor();
      try {
         this.resetReactorActions(0);
         this.setAlive(true);
      } finally {
         this.unlockReactor();
      }

      MasterBroadcaster.getInstance().sendToAllInMap(map, character -> ReactorSpawnAndDestroyer.getInstance().makeSpawnData(this));
   }

   public void delayedRespawn() {
      Runnable r = new Runnable() {
         @Override
         public void run() {
            delayedRespawnRun = null;
            respawn();
         }
      };

      delayedRespawnRun = r;
      map.getChannelServer().registerOverallAction(map.getId(), r, this.getDelay());
   }

   public boolean forceDelayedRespawn() {
      Runnable r = delayedRespawnRun;

      if (r != null) {
         map.getChannelServer().forceRunOverallAction(map.getId(), r);
         return true;
      } else {
         return false;
      }
   }

   public boolean inDelayedRespawn() {
      return delayedRespawnRun != null;
   }

   public Rectangle getArea() {
      return new Rectangle(this.position().x + stats.getTL().x, this.position().y + stats.getTL().y, stats.getBR().x - stats.getTL().x, stats.getBR().y - stats.getTL().y);
   }

   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public GuardianSpawnPoint getGuardian() {
      return guardian;
   }

   public void setGuardian(GuardianSpawnPoint guardian) {
      this.guardian = guardian;
   }

   public final byte getFacingDirection() {
      return facingDirection;
   }

   public final void setFacingDirection(final byte facingDirection) {
      this.facingDirection = facingDirection;
   }
}
