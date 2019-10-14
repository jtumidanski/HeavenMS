/*
    This file is part of the HeavenMS MapleStory Server
    Copyleft (L) 2016 - 2018 RonanLana

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

import java.awt.Point;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import client.MapleCharacter;
import net.server.audit.locks.MonitoredLockType;
import net.server.audit.locks.MonitoredReentrantReadWriteLock;
import net.server.world.MapleParty;
import tools.PacketCreator;
import tools.packet.foreigneffect.ShowBlockedMessage;
import tools.packet.showitemgaininchat.ShowSpecialEffect;
import tools.packet.stat.EnableActions;

/**
 * @author Ronan
 */
public class MapleDoorObject extends AbstractMapleMapObject {
   private final int ownerId;
   private int destinationMapId;
   private int originMapId;
   private final ReentrantReadWriteLock locks = new MonitoredReentrantReadWriteLock(MonitoredLockType.PLAYER_DOOR, true);
   private int pairOid;
   private int linkedPortalId;
   private Point linkedPos;
   private ReentrantReadWriteLock.ReadLock rlock = locks.readLock();
   private ReentrantReadWriteLock.WriteLock wlock = locks.writeLock();

   public MapleDoorObject(int owner, int destinationMapId, int originMapId, int townPortalId, Point targetPosition, Point toPosition) {
      super();
      position_$eq(targetPosition);

      ownerId = owner;
      linkedPortalId = townPortalId;
      this.originMapId = originMapId;
      this.destinationMapId = destinationMapId;
      linkedPos = toPosition;
   }

   public void update(int townPortalId, Point toPosition) {
      wlock.lock();
      try {
         linkedPortalId = townPortalId;
         linkedPos = toPosition;
      } finally {
         wlock.unlock();
      }
   }

   private int getLinkedPortalId() {
      rlock.lock();
      try {
         return linkedPortalId;
      } finally {
         rlock.unlock();
      }
   }

   private Point getLinkedPortalPosition() {
      rlock.lock();
      try {
         return linkedPos;
      } finally {
         rlock.unlock();
      }
   }

   public void warp(final MapleCharacter chr) {
      MapleParty party = chr.getParty();
      if (chr.getId() == ownerId || (party != null && party.getMemberById(ownerId) != null)) {
         PacketCreator.announce(chr, new ShowSpecialEffect(7));
         if (!inTown() && party == null) {
            chr.changeMap(destinationMapId, getLinkedPortalId());
         } else {
            chr.changeMap(destinationMapId, getLinkedPortalPosition());
         }
      } else {
         PacketCreator.announce(chr, new ShowBlockedMessage(6));
         PacketCreator.announce(chr, new EnableActions());
      }
   }

   public int getOwnerId() {
      return ownerId;
   }

   public int getPairOid() {
      return pairOid;
   }

   public void setPairOid(int oid) {
      this.pairOid = oid;
   }

   public boolean inTown() {
      return getLinkedPortalId() == -1;
   }

   public int getFrom() {
      return originMapId;
   }

   public int getTo() {
      return destinationMapId;
   }

   public int getTown() {
      return inTown() ? originMapId : destinationMapId;
   }

   public int getArea() {
      return !inTown() ? originMapId : destinationMapId;
   }

   public Point getAreaPosition() {
      return !inTown() ? this.position() : getLinkedPortalPosition();
   }

   public Point toPosition() {
      return getLinkedPortalPosition();
   }

   public int getOriginMapId() {
      return originMapId;
   }

   @Override
   public MapleMapObjectType type() {
      return MapleMapObjectType.DOOR;
   }
}
