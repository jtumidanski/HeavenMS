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
package client;

import client.database.administrator.InventoryEquipmentAdministrator;
import client.database.administrator.RingAdministrator;
import client.database.provider.RingProvider;
import client.inventory.manipulator.MapleCashidGenerator;
import tools.DatabaseConnection;
import tools.Pair;

/**
 * @author Danny
 */
public class MapleRing implements Comparable<MapleRing> {
   private int ringId;
   private int ringId2;
   private int partnerId;
   private int itemId;
   private String partnerName;
   private boolean equipped = false;

   public MapleRing(int id, int id2, int partnerId, int itemId, String partnerName) {
      this.ringId = id;
      this.ringId2 = id2;
      this.partnerId = partnerId;
      this.itemId = itemId;
      this.partnerName = partnerName;
   }

   public static MapleRing loadFromDb(int ringId) {
      return DatabaseConnection.withConnectionResultOpt(connection -> RingProvider.getInstance().getRingById(connection, ringId)).orElseThrow();
   }

   public static void removeRing(final MapleRing ring) {
      if (ring == null) {
         return;
      }

      DatabaseConnection.withConnection(connection -> {
         RingAdministrator.getInstance().deleteRing(connection, ring.getRingId(), ring.getPartnerRingId());

         MapleCashidGenerator.freeCashId(ring.getRingId());
         MapleCashidGenerator.freeCashId(ring.getPartnerRingId());

         InventoryEquipmentAdministrator.getInstance().updateRing(connection, ring.getRingId(), ring.getPartnerRingId());
      });
   }

   public static Pair<Integer, Integer> createRing(int itemId, final MapleCharacter partner1, final MapleCharacter partner2) {
      if (partner1 == null) {
         return new Pair<>(-3, -3);
      } else if (partner2 == null) {
         return new Pair<>(-2, -2);
      }

      int[] ringID = new int[2];
      ringID[0] = MapleCashidGenerator.generateCashId();
      ringID[1] = MapleCashidGenerator.generateCashId();

      DatabaseConnection.withConnection(connection -> {
         RingAdministrator.getInstance().addRing(connection, ringID[0], itemId, ringID[1], partner2.getId(), partner2.getName());
         RingAdministrator.getInstance().addRing(connection, ringID[1], itemId, ringID[0], partner1.getId(), partner1.getName());
      });

      return new Pair<>(ringID[0], ringID[1]);
   }

   public int getRingId() {
      return ringId;
   }

   public int getPartnerRingId() {
      return ringId2;
   }

   public int getPartnerChrId() {
      return partnerId;
   }

   public int getItemId() {
      return itemId;
   }

   public String getPartnerName() {
      return partnerName;
   }

   public boolean equipped() {
      return equipped;
   }

   public void equip() {
      this.equipped = true;
   }

   public void unequip() {
      this.equipped = false;
   }

   @Override
   public boolean equals(Object o) {
      if (o instanceof MapleRing) {
         return ((MapleRing) o).getRingId() == getRingId();
      }
      return false;
   }

   @Override
   public int hashCode() {
      int hash = 5;
      hash = 53 * hash + this.ringId;
      return hash;
   }

   @Override
   public int compareTo(MapleRing other) {
      if (ringId < other.getRingId()) {
         return -1;
      } else if (ringId == other.getRingId()) {
         return 0;
      }
      return 1;
   }
}
