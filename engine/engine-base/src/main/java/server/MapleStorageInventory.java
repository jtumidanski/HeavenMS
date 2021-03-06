package server;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import client.MapleClient;
import client.inventory.Equip;
import client.inventory.Item;
import config.YamlConfig;
import constants.ItemConstants;

class PairedQuickSort {
   private final ArrayList<Integer> intersect;
   MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
   private int i = 0;
   private int j = 0;

   public PairedQuickSort(ArrayList<Item> A, int primarySort, int secondarySort) {
      intersect = new ArrayList<>();

      if (A.size() > 0) {
         MapleQuickSort(0, A.size() - 1, A, primarySort);
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
            MapleQuickSort(intersect.get(ind), intersect.get(ind + 1) - 1, A, secondarySort);
         }
      }
   }

   private void PartitionByItemId(int Esq, int Dir, ArrayList<Item> A) {
      Item x, w;

      i = Esq;
      j = Dir;

      x = A.get((i + j) / 2);
      do {
         while (x.id() > A.get(i).id()) {
            i++;
         }
         while (x.id() < A.get(j).id()) {
            j--;
         }

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
         while (ii.getName(x.id()).compareTo(ii.getName(A.get(i).id())) > 0) {
            i++;
         }
         while (ii.getName(x.id()).compareTo(ii.getName(A.get(j).id())) < 0) {
            j--;
         }

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
         while (x.quantity() > A.get(i).quantity()) {
            i++;
         }
         while (x.quantity() < A.get(j).quantity()) {
            j--;
         }

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
      Equip x, w, eqpI, eqpJ;

      i = Esq;
      j = Dir;

      x = (Equip) (A.get((i + j) / 2));

      do {
         eqpI = (Equip) A.get(i);
         eqpJ = (Equip) A.get(j);

         while (x.level() > eqpI.level()) {
            i++;
         }
         while (x.level() < eqpJ.level()) {
            j--;
         }

         if (i <= j) {
            w = (Equip) A.get(i);
            A.set(i, A.get(j));
            A.set(j, w);

            i++;
            j--;
         }
      } while (i <= j);
   }

   void MapleQuickSort(int Esq, int Dir, ArrayList<Item> A, int sort) {
      switch (sort) {
         case 3 -> PartitionByLevel(Esq, Dir, A);
         case 2 -> PartitionByName(Esq, Dir, A);
         case 1 -> PartitionByQuantity(Esq, Dir, A);
         default -> PartitionByItemId(Esq, Dir, A);
      }

      if (Esq < j) {
         MapleQuickSort(Esq, j, A, sort);
      }
      if (i < Dir) {
         MapleQuickSort(i, Dir, A, sort);
      }
   }
}

public class MapleStorageInventory {
   private MapleClient c;
   private Map<Short, Item> inventory;
   private byte slotLimit;

   public MapleStorageInventory(MapleClient c, List<Item> toSort) {
      this.inventory = new LinkedHashMap<>();
      this.slotLimit = (byte) toSort.size();
      this.c = c;

      for (Item item : toSort) {
         this.addItem(item);
      }
   }

   private static boolean isEquipOrCash(Item item) {
      int type = item.id() / 1000000;
      return type == 1 || type == 5;
   }

   private static boolean isSameOwner(Item source, Item target) {
      return source.owner().equals(target.owner());
   }

   private byte getSlotLimit() {
      return slotLimit;
   }

   private Collection<Item> list() {
      return Collections.unmodifiableCollection(inventory.values());
   }

   private short addItem(Item item) {
      short slotId = getNextFreeSlot();
      if (slotId < 0 || item == null) {
         return -1;
      }
      addSlot(slotId, item);
      //TODO JDT fix this
      item = Item.newBuilder(item).setPosition(slotId).build();
      return slotId;
   }

   private void move(short sSlot, short dSlot, short slotMax) {
      Item source = inventory.get(sSlot);
      Item target = inventory.get(dSlot);
      if (source == null) {
         return;
      }
      if (target == null) {
         source = Item.newBuilder(source).setPosition(dSlot).build();
         inventory.put(dSlot, source);
         inventory.remove(sSlot);
      } else if (target.id() == source.id() && !ItemConstants.isRechargeable(source.id()) && !MapleItemInformationProvider
            .getInstance().isPickupRestricted(source.id()) && isSameOwner(source, target)) {
         if (isEquipOrCash(source)) {
            swap(target, source);
         } else if (source.quantity() + target.quantity() > slotMax) {
            short rest = (short) ((source.quantity() + target.quantity()) - slotMax);
            //TODO JDT fix this
            source = Item.newBuilder(source).setQuantity(rest).build();
            target = Item.newBuilder(target).setQuantity(slotMax).build();
         } else {
            target = Item.newBuilder(target).setQuantity((short) (source.quantity() + target.quantity())).build();
            inventory.remove(sSlot);
         }
      } else {
         swap(target, source);
      }
   }

   private void moveItem(short src, short dst) {
      if (src < 0 || dst < 0) {
         return;
      }
      if (dst > this.getSlotLimit()) {
         return;
      }

      Item source = this.getItem(src);
      if (source == null) {
         return;
      }
      short slotMax = MapleItemInformationProvider.getInstance().getSlotMax(c, source.id());
      this.move(src, dst, slotMax);
   }

   private void swap(Item source, Item target) {
      inventory.remove(source.position());
      inventory.remove(target.position());
      short swapPos = source.position();
      source = Item.newBuilder(source).setPosition(target.position()).build();
      target = Item.newBuilder(target).setPosition(swapPos).build();
      inventory.put(source.position(), source);
      inventory.put(target.position(), target);
   }

   private Item getItem(short slot) {
      return inventory.get(slot);
   }

   private void addSlot(short slot, Item item) {
      inventory.put(slot, item);
   }

   private void removeSlot(short slot) {
      inventory.remove(slot);
   }

   private boolean isFull() {
      return inventory.size() >= slotLimit;
   }

   private short getNextFreeSlot() {
      if (isFull()) {
         return -1;
      }

      for (short i = 1; i <= slotLimit; i++) {
         if (!inventory.containsKey(i)) {
            return i;
         }
      }
      return -1;
   }

   public void mergeItems() {
      MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
      Item srcItem, dstItem;

      for (short dst = 1; dst <= this.getSlotLimit(); dst++) {
         dstItem = this.getItem(dst);
         if (dstItem == null) {
            continue;
         }

         for (short src = (short) (dst + 1); src <= this.getSlotLimit(); src++) {
            srcItem = this.getItem(src);
            if (srcItem == null) {
               continue;
            }

            if (dstItem.id() != srcItem.id()) {
               continue;
            }
            if (dstItem.quantity() == ii.getSlotMax(c, this.getItem(dst).id())) {
               break;
            }

            moveItem(src, dst);
         }
      }

      boolean sorted = false;

      while (!sorted) {
         short freeSlot = this.getNextFreeSlot();

         if (freeSlot != -1) {
            short itemSlot = -1;
            for (short i = (short) (freeSlot + 1); i <= this.getSlotLimit(); i = (short) (i + 1)) {
               if (this.getItem(i) != null) {
                  itemSlot = i;
                  break;
               }
            }
            if (itemSlot > 0) {
               moveItem(itemSlot, freeSlot);
            } else {
               sorted = true;
            }
         } else {
            sorted = true;
         }
      }
   }

   public List<Item> sortItems() {
      ArrayList<Item> itemArray = new ArrayList<>();

      for (short i = 1; i <= this.getSlotLimit(); i++) {
         Item item = this.getItem(i);
         if (item != null) {
            itemArray.add(item.copy());
         }
      }

      for (Item item : itemArray) {
         this.removeSlot(item.position());
      }

      int invTypeCriteria = 1;
      int sortCriteria = (YamlConfig.config.server.USE_ITEM_SORT_BY_NAME) ? 2 : 0;
      PairedQuickSort pq = new PairedQuickSort(itemArray, sortCriteria, invTypeCriteria);

      inventory.clear();
      return itemArray;
   }
}
