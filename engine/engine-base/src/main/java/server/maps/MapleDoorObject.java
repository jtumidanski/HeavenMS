package server.maps;

import java.awt.Point;
import java.util.Optional;

import client.MapleCharacter;
import net.server.audit.locks.MonitoredLockType;
import net.server.audit.locks.MonitoredReadLock;
import net.server.audit.locks.MonitoredReentrantReadWriteLock;
import net.server.audit.locks.MonitoredWriteLock;
import net.server.audit.locks.factory.MonitoredReadLockFactory;
import net.server.audit.locks.factory.MonitoredWriteLockFactory;
import net.server.world.MapleParty;
import tools.PacketCreator;
import tools.packet.foreigneffect.ShowBlockedMessage;
import tools.packet.showitemgaininchat.ShowSpecialEffect;
import tools.packet.stat.EnableActions;

public class MapleDoorObject extends AbstractMapleMapObject {
   private final int ownerId;
   private int destinationMapId;
   private int originMapId;
   private final MonitoredReentrantReadWriteLock locks = new MonitoredReentrantReadWriteLock(MonitoredLockType.PLAYER_DOOR, true);
   private int pairOid;
   private int linkedPortalId;
   private Point linkedPos;
   private MonitoredReadLock readLock = MonitoredReadLockFactory.createLock(locks);
   private MonitoredWriteLock writeLock = MonitoredWriteLockFactory.createLock(locks);

   public MapleDoorObject(int owner, int destinationMapId, int originMapId, int townPortalId, Point targetPosition, Point toPosition) {
      super();
      setPosition(targetPosition);

      ownerId = owner;
      linkedPortalId = townPortalId;
      this.originMapId = originMapId;
      this.destinationMapId = destinationMapId;
      linkedPos = toPosition;
   }

   public void update(int townPortalId, Point toPosition) {
      writeLock.lock();
      try {
         linkedPortalId = townPortalId;
         linkedPos = toPosition;
      } finally {
         writeLock.unlock();
      }
   }

   private int getLinkedPortalId() {
      readLock.lock();
      try {
         return linkedPortalId;
      } finally {
         readLock.unlock();
      }
   }

   private Point getLinkedPortalPosition() {
      readLock.lock();
      try {
         return linkedPos;
      } finally {
         readLock.unlock();
      }
   }

   public void warp(final MapleCharacter chr) {
      Optional<MapleParty> party = chr.getParty();
      if (chr.getId() == ownerId || party.map(reference -> reference.isMember(ownerId)).orElse(false)) {
         PacketCreator.announce(chr, new ShowSpecialEffect(7));
         if (!inTown() && party.isEmpty()) {
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
