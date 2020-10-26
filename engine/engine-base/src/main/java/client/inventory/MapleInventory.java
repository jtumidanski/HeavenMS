package client.inventory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import client.MapleCharacter;
import client.MapleClient;
import client.inventory.manipulator.MapleInventoryManipulator;
import constants.ItemConstants;
import constants.MapleInventoryType;
import net.server.audit.locks.MonitoredLockType;
import net.server.audit.locks.factory.MonitoredReentrantLockFactory;
import server.MapleItemInformationProvider;
import server.ThreadManager;
import tools.LogType;
import tools.LoggerOriginator;
import tools.LoggerUtil;
import tools.Pair;

public class MapleInventory implements Iterable<Item> {
   protected MapleCharacter owner;
   protected Map<Short, Item> inventory;
   protected byte slotLimit;
   protected MapleInventoryType type;
   protected boolean checked = false;
   protected Lock lock = MonitoredReentrantLockFactory.createLock(MonitoredLockType.INVENTORY, true);

   public MapleInventory(MapleCharacter mc, MapleInventoryType type, byte slotLimit) {
      this.owner = mc;
      this.inventory = new LinkedHashMap<>();
      this.type = type;
      this.slotLimit = slotLimit;
   }

   private static boolean isSameOwner(Item source, Item target) {
      return source.owner().equals(target.owner());
   }

   private static boolean checkItemRestricted(List<Pair<Item, MapleInventoryType>> items) {
      MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();

      Set<Integer> itemIds = new HashSet<>();
      for (Pair<Item, MapleInventoryType> p : items) {
         int itemId = p.getLeft().id();
         if (ii.isPickupRestricted(itemId) && (p.getLeft().quantity() > 1 || !itemIds.add(itemId))) {
            return false;
         }
      }

      return true;
   }

   public static boolean checkSpot(MapleCharacter chr, Item item) {
      return checkSpot(chr, Collections.singletonList(item));
   }

   public static boolean checkSpot(MapleCharacter chr, List<Item> items) {
      List<Pair<Item, MapleInventoryType>> listItems = items.stream()
            .map(item -> new Pair<>(item, item.inventoryType()))
            .collect(Collectors.toList());
      return checkSpotsAndOwnership(chr, listItems);
   }

   public static boolean checkSpots(MapleCharacter chr, List<Pair<Item, MapleInventoryType>> items) {
      return checkSpots(chr, items, false);
   }

   public static boolean checkSpots(MapleCharacter chr, List<Pair<Item, MapleInventoryType>> items, boolean useProofInv) {
      int invTypesSize = MapleInventoryType.values().length;
      List<Integer> zeroedList = new ArrayList<>(invTypesSize);
      for (byte i = 0; i < invTypesSize; i++) {
         zeroedList.add(0);
      }

      return checkSpots(chr, items, zeroedList, useProofInv);
   }

   public static boolean checkSpots(MapleCharacter chr, List<Pair<Item, MapleInventoryType>> items, List<Integer> typesSlotsUsed,
                                    boolean useProofInv) {
      // assumption: no "UNDEFINED" or "EQUIPPED" items shall be tested here, all counts are >= 0.

      if (!checkItemRestricted(items)) {
         return false;
      }

      Map<Integer, List<Integer>> rcvItems = new LinkedHashMap<>();
      Map<Integer, Byte> rcvTypes = new LinkedHashMap<>();

      for (Pair<Item, MapleInventoryType> item : items) {
         Integer itemId = item.left.id();
         List<Integer> qty = rcvItems.get(itemId);

         if (qty == null) {
            List<Integer> itemQtyList = new LinkedList<>();
            itemQtyList.add((int) item.left.quantity());

            rcvItems.put(itemId, itemQtyList);
            rcvTypes.put(itemId, item.right.getType());
         } else {
            if (!ItemConstants.isEquipment(itemId) && !ItemConstants.isRechargeable(itemId)) {
               qty.set(0, qty.get(0) + item.left.quantity());
            } else {
               qty.add((int) item.left.quantity());
            }
         }
      }

      MapleClient c = chr.getClient();
      for (Entry<Integer, List<Integer>> it : rcvItems.entrySet()) {
         int itemType = rcvTypes.get(it.getKey()) - 1;

         for (Integer itValue : it.getValue()) {
            int usedSlots = typesSlotsUsed.get(itemType);

            int result = MapleInventoryManipulator.checkSpaceProgressively(c, it.getKey(), itValue, "", usedSlots, useProofInv);
            boolean hasSpace = ((result % 2) != 0);

            if (!hasSpace) {
               return false;
            }
            typesSlotsUsed.set(itemType, (result >> 1));
         }
      }

      return true;
   }

   private static long fnvHash32(final String k) {
      final int FNV_32_INIT = 0x811c9dc5;
      final int FNV_32_PRIME = 0x01000193;

      int rv = FNV_32_INIT;
      final int len = k.length();
      for (int i = 0; i < len; i++) {
         rv ^= k.charAt(i);
         rv *= FNV_32_PRIME;
      }

      return rv >= 0 ? rv : (2L * Integer.MAX_VALUE) + rv;
   }

   private static Long hashKey(Integer itemId, String owner) {
      return (itemId.longValue() << 32L) + fnvHash32(owner);
   }

   public static boolean checkSpotsAndOwnership(MapleCharacter chr, List<Pair<Item, MapleInventoryType>> items) {
      return checkSpotsAndOwnership(chr, items, false);
   }

   public static boolean checkSpotsAndOwnership(MapleCharacter chr, List<Pair<Item, MapleInventoryType>> items,
                                                boolean useProofInv) {
      List<Integer> zeroedList = new ArrayList<>(5);
      for (byte i = 0; i < 5; i++) {
         zeroedList.add(0);
      }

      return checkSpotsAndOwnership(chr, items, zeroedList, useProofInv);
   }

   public static boolean checkSpotsAndOwnership(MapleCharacter chr, List<Pair<Item, MapleInventoryType>> items,
                                                List<Integer> typesSlotsUsed, boolean useProofInv) {
      //assumption: no "UNDEFINED" or "EQUIPPED" items shall be tested here, all counts are >= 0 and item list to be checked is a legal one.

      if (!checkItemRestricted(items)) {
         return false;
      }

      Map<Long, List<Integer>> rcvItems = new LinkedHashMap<>();
      Map<Long, Byte> rcvTypes = new LinkedHashMap<>();
      Map<Long, String> rcvOwners = new LinkedHashMap<>();

      for (Pair<Item, MapleInventoryType> item : items) {
         Long itemHash = hashKey(item.left.id(), item.left.owner());
         List<Integer> qty = rcvItems.get(itemHash);

         if (qty == null) {
            List<Integer> itemQtyList = new LinkedList<>();
            itemQtyList.add((int) item.left.quantity());

            rcvItems.put(itemHash, itemQtyList);
            rcvTypes.put(itemHash, item.right.getType());
            rcvOwners.put(itemHash, item.left.owner());
         } else {
            if (!ItemConstants.isEquipment(item.left.id()) && !ItemConstants.isRechargeable(item.left.id())) {
               qty.set(0, qty.get(0) + item.left.quantity());
            } else {
               qty.add((int) item.left.quantity());
            }
         }
      }

      MapleClient c = chr.getClient();
      for (Entry<Long, List<Integer>> it : rcvItems.entrySet()) {
         int itemType = rcvTypes.get(it.getKey()) - 1;
         int itemId = (int) (it.getKey() >> 32L);

         for (Integer itValue : it.getValue()) {
            int usedSlots = typesSlotsUsed.get(itemType);

            int result = MapleInventoryManipulator
                  .checkSpaceProgressively(c, itemId, itValue, rcvOwners.get(it.getKey()), usedSlots, useProofInv);
            boolean hasSpace = ((result % 2) != 0);

            if (!hasSpace) {
               return false;
            }
            typesSlotsUsed.set(itemType, (result >> 1));
         }
      }

      return true;
   }

   public boolean isExtendableInventory() { // not sure about cash, basing this on the previous one.
      return !(type.equals(MapleInventoryType.UNDEFINED) || type.equals(MapleInventoryType.EQUIPPED) || type
            .equals(MapleInventoryType.CASH));
   }

   public boolean isEquipInventory() {
      return type.equals(MapleInventoryType.EQUIP) || type.equals(MapleInventoryType.EQUIPPED);
   }

   public byte getSlotLimit() {
      lock.lock();
      try {
         return slotLimit;
      } finally {
         lock.unlock();
      }
   }

   public void setSlotLimit(int newLimit) {
      lock.lock();
      try {
         if (newLimit < slotLimit) {
            list().stream()
                  .filter(item -> item.position() > newLimit)
                  .map(Item::position)
                  .forEach(this::removeSlot);
         }
      } finally {
         lock.unlock();
      }
   }

   public Collection<Item> list() {
      lock.lock();
      try {
         return Collections.unmodifiableCollection(inventory.values());
      } finally {
         lock.unlock();
      }
   }

   public Item findById(int itemId) {
      return list().stream()
            .filter(item -> item.id() == itemId)
            .findFirst()
            .orElse(null);
   }

   public Item findByName(String name) {
      MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
      for (Item item : list()) {
         String itemName = ii.getName(item.id());
         if (itemName == null) {
            LoggerUtil.printError(LoggerOriginator.ENGINE, LogType.EXCEPTION, "[CRITICAL] Item " + item.id() + " has no name.");
            continue;
         }

         if (name.compareToIgnoreCase(itemName) == 0) {
            return item;
         }
      }
      return null;
   }

   public int countById(int itemId) {
      return list().stream()
            .filter(item -> item.id() == itemId)
            .mapToInt(Item::quantity)
            .sum();
   }

   public int countNotOwnedById(int itemId) {
      return list().stream()
            .filter(item -> item.id() == itemId && item.owner().equals(""))
            .mapToInt(Item::quantity)
            .sum();
   }

   public int freeSlotCountById(int itemId, int required) {
      List<Item> itemList = listById(itemId);
      int openSlot = 0;

      if (!ItemConstants.isRechargeable(itemId)) {
         for (Item item : itemList) {
            required -= item.quantity();

            if (required >= 0) {
               openSlot++;
               if (required == 0) {
                  return openSlot;
               }
            } else {
               return openSlot;
            }
         }
      } else {
         for (Item item : itemList) {
            required -= 1;

            if (required >= 0) {
               openSlot++;
               if (required == 0) {
                  return openSlot;
               }
            } else {
               return openSlot;
            }
         }
      }

      return -1;
   }

   protected List<Item> listById(int itemId, Supplier<List<Item>> collector) {
      List<Item> ret = list().stream().filter(item -> item.id() == itemId).collect(Collectors.toCollection(collector));
      if (ret.size() > 1) {
         ret.sort(Comparator.comparingInt(Item::position));
      }
      return ret;
   }

   public List<Item> listById(int itemId) {
      return listById(itemId, ArrayList::new);
   }

   public List<Item> linkedListById(int itemId) {
      return listById(itemId, LinkedList::new);
   }

   public Optional<Item> addItem(Item item) {
      return addToSlot(item);
   }

   public void addItemFromDB(Item item) {
      if (item.position() < 0 && !type.equals(MapleInventoryType.EQUIPPED)) {
         return;
      }
      addSlotFromDB(item.position(), item);
   }

   /**
    * Move an item from the source slot to the destination slot, considering the slot max.
    *
    * @param sSlot   the source slot
    * @param dSlot   the destination slot
    * @param slotMax the maximum quantity for the item in a slot
    */
   public void move(short sSlot, short dSlot, short slotMax) {
      lock.lock();
      try {
         Item source = inventory.get(sSlot);
         Item target = inventory.get(dSlot);
         if (source == null) {
            return;
         }
         if (target == null) {
            source = source.setPosition(dSlot);
            inventory.put(dSlot, source);
            inventory.remove(sSlot);
         } else if (target.id() == source.id() && !ItemConstants.isRechargeable(source.id()) && isSameOwner(source, target)) {
            if (type.getType() == MapleInventoryType.EQUIP.getType() || type.getType() == MapleInventoryType.CASH.getType()) {
               swap(target, source);
            } else if (source.quantity() + target.quantity() > slotMax) {
               short rest = (short) ((source.quantity() + target.quantity()) - slotMax);
               getAndUpdateUnlocked(sSlot, item -> item.setQuantity(rest));
               getAndUpdateUnlocked(dSlot, item -> item.setQuantity(slotMax));
            } else {
               short combined = (short) (source.quantity() + target.quantity());
               getAndUpdate(dSlot, item -> item.setQuantity(combined));
               inventory.remove(sSlot);
            }
         } else {
            swap(target, source);
         }
      } finally {
         lock.unlock();
      }
   }

   private void swap(Item source, Item target) {
      inventory.remove(source.position());
      inventory.remove(target.position());
      short swapPos = source.position();
      source = source.setPosition(target.position());
      target = target.setPosition(swapPos);
      inventory.put(source.position(), source);
      inventory.put(target.position(), target);
   }

   public Item getItem(short slot) {
      lock.lock();
      try {
         return inventory.get(slot);
      } finally {
         lock.unlock();
      }
   }

   /**
    * Retrieves the item at the slot, allows modification, stores the update, and returns the updated item.
    *
    * @param slot     the slot the item to update is at
    * @param modifier a function which modifies the indicated item
    * @return the modified item
    */
   public Item getAndUpdate(short slot, Function<Item, Item> modifier) {
      lock.lock();
      try {
         return getAndUpdateUnlocked(slot, modifier);
      } finally {
         lock.unlock();
      }
   }

   /**
    * Retrieves the item at the slot, allows modification, stores the update, and returns the updated item. NOTE: Only
    * should be used inside a lock of the inventory
    *
    * @param slot     the slot the item to update is at
    * @param modifier a function which modifies the indicated item
    * @return the modified item
    */
   protected Item getAndUpdateUnlocked(short slot, Function<Item, Item> modifier) {
      Item newItem = modifier.apply(inventory.get(slot));
      inventory.put(slot, newItem);
      return newItem;
   }

   /**
    * Removes 1 of an item from the slot.
    *
    * @param slot the slot to modify
    * @return the updated item
    */
   public Optional<Item> removeItem(short slot) {
      return removeItem(slot, (short) 1, false);
   }

   /**
    * Removes a quantity of an item from the slot.
    *
    * @param slot      the slot to modify
    * @param quantity  the quantity to remove
    * @param allowZero if a quantity of zero is allowed
    * @return the updated item
    */
   public Optional<Item> removeItem(short slot, short quantity, boolean allowZero) {
      Item item = getItem(slot);
      if (item == null) {// TODO is it ok not to throw an exception here?
         return Optional.empty();
      }
      item = item.setQuantity((short) (item.quantity() - quantity));
      if (item.quantity() < 0) {
         item = item.setQuantity((short) 0);
      }
      if (item.quantity() == 0 && !allowZero) {
         removeSlot(slot);
      }
      return Optional.of(item);
   }

   public void update(final Item item) {
      if (item == null) {
         return;
      }

      lock.lock();
      try {
         short slotId = inventory.keySet().stream()
               .filter(key -> inventory.get(key).id() == item.id())
               .findFirst()
               .orElse((short) -1);
         if (slotId < 0) {
            return;
         }

         inventory.put(slotId, item);
      } finally {
         lock.unlock();
      }
   }

   protected Optional<Item> addToSlot(final Item item) {
      if (item == null) {
         return Optional.empty();
      }

      short slotId;
      Item itemWithSlot;
      lock.lock();
      try {
         slotId = getNextFreeSlot();
         if (slotId < 0) {
            return Optional.empty();
         }

         itemWithSlot = item.updatePosition(slotId);
         inventory.put(slotId, itemWithSlot);
      } finally {
         lock.unlock();
      }

      if (ItemConstants.isRateCoupon(itemWithSlot.id())) {
         ThreadManager.getInstance().newTask(() -> owner.updateCouponRates());
      }

      return Optional.of(itemWithSlot);
   }

   protected void addSlotFromDB(short slot, Item item) {
      lock.lock();
      try {
         inventory.put(slot, item);
      } finally {
         lock.unlock();
      }

      if (ItemConstants.isRateCoupon(item.id())) {
         ThreadManager.getInstance().newTask(() -> owner.updateCouponRates());
      }
   }

   /**
    * Removes an item from the slot.
    *
    * @param slot the slot to remove from
    * @return the item removed
    */
   public void removeSlot(short slot) {
      Item item;
      lock.lock();
      try {
         item = inventory.remove(slot);
      } finally {
         lock.unlock();
      }

      if (item != null && ItemConstants.isRateCoupon(item.id())) {
         ThreadManager.getInstance().newTask(() -> owner.updateCouponRates());
      }
   }

   public boolean isFull() {
      lock.lock();
      try {
         return inventory.size() >= slotLimit;
      } finally {
         lock.unlock();
      }
   }

   public boolean isFull(int margin) {
      lock.lock();
      try {
         return inventory.size() + margin >= slotLimit;
      } finally {
         lock.unlock();
      }
   }

   public boolean isFullAfterSomeItems(int margin, int used) {
      lock.lock();
      try {
         return inventory.size() + margin >= slotLimit - used;
      } finally {
         lock.unlock();
      }
   }

   public short getNextFreeSlot() {
      if (isFull()) {
         return -1;
      }

      lock.lock();
      try {
         for (short i = 1; i <= slotLimit; i++) {
            if (!inventory.containsKey(i)) {
               return i;
            }
         }
         return -1;
      } finally {
         lock.unlock();
      }
   }

   public short getNumFreeSlot() {
      if (isFull()) {
         return 0;
      }

      lock.lock();
      try {
         short free = 0;
         for (short i = 1; i <= slotLimit; i++) {
            if (!inventory.containsKey(i)) {
               free++;
            }
         }
         return free;
      } finally {
         lock.unlock();
      }
   }

   public MapleInventoryType getType() {
      return type;
   }

   @Override
   public Iterator<Item> iterator() {
      return Collections.unmodifiableCollection(list()).iterator();
   }

   public Collection<MapleInventory> allInventories() {
      return Collections.singletonList(this);
   }

   public Item findByCashId(int cashId) {
      boolean isRing = false;
      Equip equip = null;
      for (Item item : list()) {
         if (item.inventoryType().equals(MapleInventoryType.EQUIP)) {
            equip = (Equip) item;
            isRing = equip.ringId() > -1;
         }
         if ((item.petId() > -1 ? item.petId() : isRing ? equip.ringId() : item.cashId()) == cashId) {
            return item;
         }
      }

      return null;
   }

   public boolean checked() {
      lock.lock();
      try {
         return checked;
      } finally {
         lock.unlock();
      }
   }

   public void checked(boolean yes) {
      lock.lock();
      try {
         checked = yes;
      } finally {
         lock.unlock();
      }
   }

   public void lockInventory() {
      lock.lock();
   }

   public void unlockInventory() {
      lock.unlock();
   }
}