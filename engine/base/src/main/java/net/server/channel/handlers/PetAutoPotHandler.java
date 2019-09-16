/*
	This file is part of the OdinMS Maple Story Server
    Copyright (C) 2008 Patrick Huy <patrick.huy@frz.cc>
		       Matthias Butz <matze@odinms.de>
		       Jan Christian Meyer <vimes@odinms.de>

    Copyleft (L) 2016 - 2018 RonanLana (HeavenMS)

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
package net.server.channel.handlers;

import java.util.List;

import client.MapleCharacter;
import client.MapleClient;
import client.inventory.Item;
import client.inventory.MapleInventory;
import client.inventory.MapleInventoryType;
import client.inventory.manipulator.MapleInventoryManipulator;
import constants.ServerConstants;
import net.server.AbstractPacketHandler;
import net.server.channel.packet.pet.PetAutoPotPacket;
import net.server.channel.packet.reader.PetAutoPotReader;
import server.MapleItemInformationProvider;
import server.MapleStatEffect;
import tools.MaplePacketCreator;

/**
 * @author Ronan - multi-pot consumption feature
 */
public final class PetAutoPotHandler extends AbstractPacketHandler<PetAutoPotPacket> {
   @Override
   public Class<PetAutoPotReader> getReaderClass() {
      return PetAutoPotReader.class;
   }

   @Override
   public boolean successfulProcess(MapleClient client) {
      if (!client.getPlayer().isAlive()) {
         client.announce(MaplePacketCreator.enableActions());
         return false;
      }
      return true;
   }

   @Override
   public void handlePacket(PetAutoPotPacket packet, MapleClient client) {
      MapleCharacter chr = client.getPlayer();
      MapleInventory useInv = chr.getInventory(MapleInventoryType.USE);

      short slot = packet.slot();
      int useCount = 0;
      MapleStatEffect stat = null;

      useInv.lockInventory();

      try {
         Item toUse = useInv.getItem(slot);

         if (toUse != null) {
            if (toUse.getItemId() != packet.itemId()) {
               client.announce(MaplePacketCreator.enableActions());
               return;
            }

            List<Item> toUseList = null;

            // from now on, toUse becomes the "cursor" for the current pot being used
            if (toUse.getQuantity() <= 0) {
               // depleted out the current slot, fetch for more
               SeekResult result = findNextAvailablePot(chr, packet.itemId(), toUseList);
               if (!result.isAvailable()) {
                  client.announce(MaplePacketCreator.enableActions());
                  return;
               } else {
                  toUseList = result.getToUseList();
                  toUse = result.getToUse();
                  slot = result.getSlot();
               }
            }

            stat = MapleItemInformationProvider.getInstance().getItemEffect(toUse.getItemId());
            boolean hasHpGain = stat.getHp() > 0 || stat.getHpRate() > 0.0;
            boolean hasMpGain = stat.getMp() > 0 || stat.getMpRate() > 0.0;

            int maxHp = chr.getCurrentMaxHp();
            int maxMp = chr.getCurrentMaxMp();

            int curHp = chr.getHp();
            int curMp = chr.getMp();

            double incHp = stat.getHp();
            if (incHp <= 0 && hasHpGain) {
               incHp = Math.ceil(maxHp * stat.getHpRate());
            }

            double incMp = stat.getMp();
            if (incMp <= 0 && hasMpGain) {
               incMp = Math.ceil(maxMp * stat.getMpRate());
            }

            int qtyCount = 0;
            if (ServerConstants.USE_COMPULSORY_AUTOPOT) {
               if (hasHpGain) {
                  qtyCount = (int) Math.ceil(((ServerConstants.PET_AUTOHP_RATIO * maxHp) - curHp) / incHp);
               }

               if (hasMpGain) {
                  qtyCount = Math.max(qtyCount, (int) Math.ceil(((ServerConstants.PET_AUTOMP_RATIO * maxMp) - curMp) / incMp));
               }
            } else {
               qtyCount = 1;   // non-compulsory autopot concept thanks to marcuswoon
            }

            while (true) {
               short qtyToUse = (short) Math.min(qtyCount, toUse.getQuantity());
               MapleInventoryManipulator.removeFromSlot(client, MapleInventoryType.USE, slot, qtyToUse, false);

               curHp += (incHp * qtyToUse);
               curMp += (incMp * qtyToUse);

               useCount += qtyToUse;
               qtyCount -= qtyToUse;

               if (toUse.getQuantity() == 0 && qtyCount > 0) {
                  // depleted out the current slot, fetch for more
                  SeekResult result = findNextAvailablePot(chr, packet.itemId(), toUseList);
                  if (!result.isAvailable()) {
                     break;    // no more pots available
                  } else {
                     toUseList = result.getToUseList();
                     toUse = result.getToUse();
                     slot = result.getSlot();
                  }
               } else {
                  break;    // gracefully finished it's job, quit the loop
               }
            }
         }
      } finally {
         useInv.unlockInventory();
      }

      for (int i = 0; i < useCount; i++) {
         stat.applyTo(chr);
      }

      chr.announce(MaplePacketCreator.enableActions());
   }

   private SeekResult findNextAvailablePot(MapleCharacter chr, int itemId, List<Item> toUseList) {
      if (toUseList == null) {
         toUseList = chr.getInventory(MapleInventoryType.USE).linkedListById(itemId);
      }

      while (!toUseList.isEmpty()) {
         Item it = toUseList.remove(0);
         if (it.getQuantity() > 0) {
            return new SeekResult(it, toUseList, it.getPosition());
         }
      }
      return new SeekResult();
   }

   private class SeekResult {
      private boolean available;

      private Item toUse;

      private List<Item> toUseList;

      private short slot;

      public SeekResult(Item toUse, List<Item> toUseList, short slot) {
         this.toUse = toUse;
         this.toUseList = toUseList;
         this.slot = slot;
         this.available = true;
      }

      public SeekResult() {
         available = false;
      }

      public boolean isAvailable() {
         return available;
      }

      public Item getToUse() {
         return toUse;
      }

      public List<Item> getToUseList() {
         return toUseList;
      }

      public short getSlot() {
         return slot;
      }
   }
}
