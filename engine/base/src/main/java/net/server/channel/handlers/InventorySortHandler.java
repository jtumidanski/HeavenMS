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
package net.server.channel.handlers;

import java.util.ArrayList;
import java.util.List;

import client.MapleCharacter;
import client.MapleClient;
import client.inventory.Equip;
import client.inventory.Item;
import client.inventory.MapleInventory;
import client.inventory.MapleInventoryType;
import client.inventory.ModifyInventory;
import constants.ServerConstants;
import net.server.AbstractPacketHandler;
import net.server.Server;
import net.server.channel.packet.InventorySortPacket;
import net.server.channel.packet.reader.InventorySortReader;
import server.MapleItemInformationProvider;
import tools.PacketCreator;
import tools.packet.inventory.ModifyInventoryPacket;
import tools.packet.stat.EnableActions;
import tools.packet.ui.FinishedSort2;

/**
 * @author BubblesDev
 * @author Ronan
 */

class PairedQuicksort {
   private final ArrayList<Integer> intersect;
   MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
   private int i = 0;
   private int j = 0;

   public PairedQuicksort(ArrayList<Item> A, int primarySort, int secondarySort) {
      intersect = new ArrayList<>();

      if (A.size() > 0) {
         MapleQuicksort(0, A.size() - 1, A, primarySort);

         if (A.get(0).inventoryType().equals(MapleInventoryType.USE)) {   // thanks KDA & Vcoc for suggesting stronger projectiles coming before weaker ones
            reverseSortSublist(A, BinarySearchElement(A, 206));  // arrows
            reverseSortSublist(A, BinarySearchElement(A, 207));  // stars
            reverseSortSublist(A, BinarySearchElement(A, 233));  // bullets
         }
      }

      intersect.add(0);
      for (int ind = 1; ind < A.size(); ind++) {
         if (A.get(ind - 1).id() != A.get(ind).id()) {
            intersect.add(ind);
         }
      }
      intersect.add(A.size());

      for (int ind = 0; ind < intersect.size() - 1; ind++) {
         if (intersect.get(ind + 1) > intersect.get(ind)) {
            MapleQuicksort(intersect.get(ind), intersect.get(ind + 1) - 1, A, secondarySort);
         }
      }
   }

   private static int getItemSubtype(Item it) {
      return it.id() / 10000;
   }

   private void PartitionByItemId(int Esq, int Dir, ArrayList<Item> A) {
      Item x, w;

      i = Esq;
      j = Dir;

      x = A.get((i + j) / 2);
      do {
         while (x.id() > A.get(i).id()) i++;
         while (x.id() < A.get(j).id()) j--;

         if (i <= j) {
            w = A.get(i);
            A.set(i, A.get(j));
            A.set(j, w);

            i++;
            j--;
         }
      } while (i <= j);
   }

   private void PartitionByItemIdReverse(int Esq, int Dir, ArrayList<Item> A) {
      Item x, w;

      i = Esq;
      j = Dir;

      x = A.get((i + j) / 2);
      do {
         while (x.id() < A.get(i).id()) i++;
         while (x.id() > A.get(j).id()) j--;

         if (i <= j) {
            w = A.get(i);
            A.set(i, A.get(j));
            A.set(j, w);

            i++;
            j--;
         }
      } while (i <= j);
   }

   private void PartitionByName(int Esq, int Dir, ArrayList<Item> A) {
      Item x, w;

      i = Esq;
      j = Dir;

      x = A.get((i + j) / 2);
      do {
         while (ii.getName(x.id()).compareTo(ii.getName(A.get(i).id())) > 0) i++;
         while (ii.getName(x.id()).compareTo(ii.getName(A.get(j).id())) < 0) j--;

         if (i <= j) {
            w = A.get(i);
            A.set(i, A.get(j));
            A.set(j, w);

            i++;
            j--;
         }
      } while (i <= j);
   }

   private void PartitionByQuantity(int Esq, int Dir, ArrayList<Item> A) {
      Item x, w;

      i = Esq;
      j = Dir;

      x = A.get((i + j) / 2);
      do {
         while (x.quantity() > A.get(i).quantity()) i++;
         while (x.quantity() < A.get(j).quantity()) j--;

         if (i <= j) {
            w = A.get(i);
            A.set(i, A.get(j));
            A.set(j, w);

            i++;
            j--;
         }
      } while (i <= j);
   }

   private void PartitionByLevel(int Esq, int Dir, ArrayList<Item> A) {
      Equip x, w;

      i = Esq;
      j = Dir;

      x = (Equip) (A.get((i + j) / 2));

      do {

         while (x.level() > ((Equip) A.get(i)).level()) i++;
         while (x.level() < ((Equip) A.get(j)).level()) j--;

         if (i <= j) {
            w = (Equip) A.get(i);
            A.set(i, A.get(j));
            A.set(j, w);

            i++;
            j--;
         }
      } while (i <= j);
   }

   void MapleQuicksort(int Esq, int Dir, ArrayList<Item> A, int sort) {
      switch (sort) {
         case 3:
            PartitionByLevel(Esq, Dir, A);
            break;

         case 2:
            PartitionByName(Esq, Dir, A);
            break;

         case 1:
            PartitionByQuantity(Esq, Dir, A);
            break;

         default:
            PartitionByItemId(Esq, Dir, A);
      }


      if (Esq < j) {
         MapleQuicksort(Esq, j, A, sort);
      }
      if (i < Dir) {
         MapleQuicksort(i, Dir, A, sort);
      }
   }

   private int[] BinarySearchElement(ArrayList<Item> A, int rangeId) {
      int st = 0, en = A.size() - 1;

      int mid = -1, idx = -1;
      while (en >= st) {
         idx = (st + en) / 2;
         mid = getItemSubtype(A.get(idx));

         if (mid == rangeId) {
            break;
         } else if (mid < rangeId) {
            st = idx + 1;
         } else {
            en = idx - 1;
         }
      }

      if (en < st) {
         return null;
      }

      st = idx - 1;
      en = idx + 1;
      while (st >= 0 && getItemSubtype(A.get(st)) == rangeId) {
         st -= 1;
      }
      st += 1;

      while (en < A.size() && getItemSubtype(A.get(en)) == rangeId) {
         en += 1;
      }
      en -= 1;

      return new int[]{st, en};
   }

   public void reverseSortSublist(ArrayList<Item> A, int[] range) {
      if (range != null) {
         PartitionByItemIdReverse(range[0], range[1], A);
      }
   }
}

public final class InventorySortHandler extends AbstractPacketHandler<InventorySortPacket> {
   @Override
   public Class<InventorySortReader> getReaderClass() {
      return InventorySortReader.class;
   }

   @Override
   public void handlePacket(InventorySortPacket packet, MapleClient client) {
      MapleCharacter chr = client.getPlayer();
      chr.getAutobanManager().setTimestamp(3, Server.getInstance().getCurrentTimestamp(), 4);

      if (!ServerConstants.USE_ITEM_SORT) {
         PacketCreator.announce(client, new EnableActions());
         return;
      }

      if (packet.inventoryType() < 1 || packet.inventoryType() > 5) {
         client.disconnect(false, false);
         return;
      }

      ArrayList<Item> itemarray = new ArrayList<>();
      List<ModifyInventory> mods = new ArrayList<>();

      MapleInventory inventory = chr.getInventory(MapleInventoryType.getByType(packet.inventoryType()));
      inventory.lockInventory();
      try {
         for (short i = 1; i <= inventory.getSlotLimit(); i++) {
            Item item = inventory.getItem(i);
            if (item != null) {
               itemarray.add(item.copy());
            }
         }

         for (Item item : itemarray) {
            inventory.removeSlot(item.position());
            mods.add(new ModifyInventory(3, item));
         }

         int invTypeCriteria = (MapleInventoryType.getByType(packet.inventoryType()) == MapleInventoryType.EQUIP) ? 3 : 1;
         int sortCriteria = (ServerConstants.USE_ITEM_SORT_BY_NAME) ? 2 : 0;
         PairedQuicksort pq = new PairedQuicksort(itemarray, sortCriteria, invTypeCriteria);

         for (Item item : itemarray) {
            inventory.addItem(item);
            mods.add(new ModifyInventory(0, item.copy()));//to prevent crashes
         }
         itemarray.clear();
      } finally {
         inventory.unlockInventory();
      }

      PacketCreator.announce(client, new ModifyInventoryPacket(true, mods));
      PacketCreator.announce(client, new FinishedSort2(packet.inventoryType()));
      PacketCreator.announce(client, new EnableActions());
   }
}
