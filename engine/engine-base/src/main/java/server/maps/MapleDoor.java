package server.maps;

import java.awt.Point;

import client.MapleCharacter;
import config.YamlConfig;
import tools.Pair;

public class MapleDoor {
   private int ownerId;
   private MapleMap town;
   private MaplePortal townPortal;
   private MapleMap target;
   private Pair<String, Integer> posStatus = null;
   private long deployTime;
   private boolean active;

   private MapleDoorObject townDoor;
   private MapleDoorObject areaDoor;

   public MapleDoor(MapleCharacter owner, Point targetPosition) {
      this.ownerId = owner.getId();
      this.target = owner.getMap();

      if (target.canDeployDoor(targetPosition)) {
         if (YamlConfig.config.server.USE_ENFORCE_MDOOR_POSITION) {
            posStatus = target.getDoorPositionStatus(targetPosition);
         }

         if (posStatus == null) {
            this.town = this.target.getReturnMap();
            this.townPortal = getTownDoorPortal(owner.getDoorSlot());
            this.deployTime = System.currentTimeMillis();
            this.active = true;

            if (townPortal != null) {
               this.areaDoor = new MapleDoorObject(ownerId, town.getId(), target.getId(), townPortal.getId(), targetPosition, townPortal.getPosition());
               this.townDoor = new MapleDoorObject(ownerId, target.getId(), town.getId(), -1, townPortal.getPosition(), targetPosition);

               this.areaDoor.setPairOid(this.townDoor.objectId());
               this.townDoor.setPairOid(this.areaDoor.objectId());
            } else {
               this.ownerId = -1;
            }
         } else {
            this.ownerId = -3;
         }
      } else {
         this.ownerId = -2;
      }
   }

   public void updateDoorPortal(MapleCharacter owner) {
      int slot = owner.fetchDoorSlot();

      MaplePortal nextTownPortal = getTownDoorPortal(slot);
      if (nextTownPortal != null) {
         townPortal = nextTownPortal;
         areaDoor.update(nextTownPortal.getId(), nextTownPortal.getPosition());
      }
   }

   private MaplePortal getTownDoorPortal(int doorId) {
      return town.getDoorPortal(doorId);
   }

   public int getOwnerId() {
      return ownerId;
   }

   public MapleDoorObject getTownDoor() {
      return townDoor;
   }

   public MapleDoorObject getAreaDoor() {
      return areaDoor;
   }

   public MapleMap getTown() {
      return town;
   }

   public MaplePortal getTownPortal() {
      return townPortal;
   }

   public MapleMap getTarget() {
      return target;
   }

   public Pair<String, Integer> getDoorStatus() {
      return posStatus;
   }

   public long getElapsedDeployTime() {
      return System.currentTimeMillis() - deployTime;
   }

   public boolean dispose() {
      if (active) {
         active = false;
         return true;
      } else {
         return false;
      }
   }

   public boolean isActive() {
      return active;
   }
}
