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
package client.inventory;

import java.awt.Point;
import java.util.List;

import client.MapleCharacter;
import client.database.administrator.PetAdministrator;
import client.database.provider.PetProvider;
import client.inventory.manipulator.MapleCashIdGenerator;
import constants.ExpTable;
import server.MapleItemInformationProvider;
import server.movement.AbsoluteLifeMovement;
import server.movement.LifeMovement;
import server.movement.LifeMovementFragment;
import tools.DatabaseConnection;
import tools.MaplePacketCreator;
import tools.Pair;

/**
 * @author Matze
 */
public class MaplePet extends Item {
   private String name;
   private int uniqueid;
   private int closeness = 0;
   private byte level = 1;
   private int fullness = 100;
   private int Fh;
   private Point pos;
   private int stance;
   private boolean summoned;
   private int petFlag = 0;

   private MaplePet(int id, short position, int uniqueid) {
      super(id, position, (short) 1);
      this.uniqueid = uniqueid;
      this.pos = new Point(0, 0);
   }

   public static MaplePet loadFromDb(int itemid, short position, int petid) {
      MaplePet ret = new MaplePet(itemid, position, petid);
      DatabaseConnection.getInstance().withConnectionResult(connection -> PetProvider.getInstance().loadPet(connection, petid)).ifPresent(petData -> {
         ret.setName(petData.name());
         ret.setCloseness(petData.closeness());
         ret.setLevel(petData.level());
         ret.setFullness(petData.fullness());
         ret.setSummoned(petData.summoned());
         ret.setPetFlag(petData.flag());
      });

      return ret;
   }

   public static void deleteFromDb(MapleCharacter owner, int petid) {
      DatabaseConnection.getInstance().withConnection(connection -> PetAdministrator.getInstance().deleteAllPetData(connection, petid));
      owner.resetExcluded(petid);
      MapleCashIdGenerator.getInstance().freeCashId(petid);
   }

   public static int createPet(int itemid) {
      return createPet(itemid, Byte.valueOf("1"), 0, 100);
   }

   public static int createPet(int itemid, byte level, int closeness, int fullness) {
      return DatabaseConnection.getInstance().withConnectionResult(connection -> PetAdministrator.getInstance().createPet(connection, itemid, level, closeness, fullness)).orElse(-1);
   }

   public void saveToDb() {
      DatabaseConnection.getInstance().withConnection(connection -> PetAdministrator.getInstance().updatePet(connection, getName(), getLevel(), getCloseness(), getFullness(), isSummoned(), getPetFlag(), getUniqueId()));
   }

   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public int getUniqueId() {
      return uniqueid;
   }

   public void setUniqueId(int id) {
      this.uniqueid = id;
   }

   public int getCloseness() {
      return closeness;
   }

   public void setCloseness(int closeness) {
      this.closeness = closeness;
   }

   public byte getLevel() {
      return level;
   }

   public void setLevel(byte level) {
      this.level = level;
   }

   public void gainClosenessFullness(MapleCharacter owner, int incCloseness, int incFullness, int type) {
      byte slot = owner.getPetIndex(this);
      boolean enjoyed;

      //will NOT increase pet's closeness if tried to feed pet with 100% fullness
      if (fullness < 100 || incFullness == 0) {   //incFullness == 0: command given
         int newFullness = fullness + incFullness;
         if (newFullness > 100) {
            newFullness = 100;
         }
         fullness = newFullness;

         if (incCloseness > 0 && closeness < 30000) {
            int newCloseness = closeness + incCloseness;
            if (newCloseness > 30000) {
               newCloseness = 30000;
            }

            closeness = newCloseness;
            while (newCloseness >= ExpTable.getClosenessNeededForLevel(level)) {
               level += 1;
               owner.getClient().announce(MaplePacketCreator.showOwnPetLevelUp(slot));
               owner.getMap().broadcastMessage(MaplePacketCreator.showPetLevelUp(owner, slot));
            }
         }

         enjoyed = true;
      } else {
         int newCloseness = closeness - 1;
         if (newCloseness < 0) {
            newCloseness = 0;
         }

         closeness = newCloseness;
         if (level > 1 && newCloseness < ExpTable.getClosenessNeededForLevel(level - 1)) {
            level -= 1;
         }

         enjoyed = false;
      }

      owner.getMap().broadcastMessage(MaplePacketCreator.petFoodResponse(owner.getId(), slot, enjoyed, false));
      saveToDb();

      Item petz = owner.getInventory(MapleInventoryType.CASH).getItem(getPosition());
      if (petz != null) {
         owner.forceUpdateItem(petz);
      }
   }

   public int getFullness() {
      return fullness;
   }

   public void setFullness(int fullness) {
      this.fullness = fullness;
   }

   public int getFh() {
      return Fh;
   }

   public void setFh(int Fh) {
      this.Fh = Fh;
   }

   public Point getPos() {
      return pos;
   }

   public void setPos(Point pos) {
      this.pos = pos;
   }

   public int getStance() {
      return stance;
   }

   public void setStance(int stance) {
      this.stance = stance;
   }

   public boolean isSummoned() {
      return summoned;
   }

   public void setSummoned(boolean yes) {
      this.summoned = yes;
   }

   public int getPetFlag() {
      return this.petFlag;
   }

   private void setPetFlag(int flag) {
      this.petFlag = flag;
   }

   public void addPetFlag(MapleCharacter owner, PetFlag flag) {
      this.petFlag |= flag.getValue();
      saveToDb();

      Item petz = owner.getInventory(MapleInventoryType.CASH).getItem(getPosition());
      if (petz != null) {
         owner.forceUpdateItem(petz);
      }
   }

   public void removePetFlag(MapleCharacter owner, PetFlag flag) {
      this.petFlag &= 0xFFFFFFFF ^ flag.getValue();
      saveToDb();

      Item petz = owner.getInventory(MapleInventoryType.CASH).getItem(getPosition());
      if (petz != null) {
         owner.forceUpdateItem(petz);
      }
   }

   public Pair<Integer, Boolean> canConsume(int itemId) {
      return MapleItemInformationProvider.getInstance().canPetConsume(this.getItemId(), itemId);
   }

   public void updatePosition(List<LifeMovementFragment> movement) {
      for (LifeMovementFragment move : movement) {
         if (move instanceof LifeMovement) {
            if (move instanceof AbsoluteLifeMovement) {
               this.setPos(move.position());
            }
            this.setStance(((LifeMovement) move).newState());
         }
      }
   }

   public enum PetFlag {
      OWNER_SPEED(0x01);

      private int i;

      PetFlag(int i) {
         this.i = i;
      }

      public int getValue() {
         return i;
      }
   }
}