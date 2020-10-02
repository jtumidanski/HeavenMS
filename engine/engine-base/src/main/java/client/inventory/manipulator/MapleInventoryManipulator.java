package client.inventory.manipulator;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import client.MapleBuffStat;
import client.MapleCharacter;
import client.MapleClient;
import client.inventory.Equip;
import client.inventory.Item;
import client.inventory.MapleInventory;
import client.inventory.MapleInventoryType;
import client.inventory.MaplePet;
import client.inventory.ModifyInventory;
import client.processor.ItemProcessor;
import client.processor.NewYearCardProcessor;
import client.processor.PetProcessor;
import config.YamlConfig;
import constants.inventory.ItemConstants;
import server.MapleItemInformationProvider;
import server.maps.MapleMap;
import tools.LoggerOriginator;
import tools.LoggerUtil;
import tools.MasterBroadcaster;
import tools.PacketCreator;
import tools.packet.foreigneffect.ShowItemEffect;
import tools.packet.inventory.InventoryFull;
import tools.packet.inventory.ModifyInventoryPacket;
import tools.packet.stat.EnableActions;
import tools.packet.statusinfo.ShowInventoryFull;
import tools.packet.statusinfo.ShowItemGain;
import tools.packet.statusinfo.ShowItemUnavailable;

public class MapleInventoryManipulator {

   public static boolean addById(MapleClient c, int itemId, short quantity) {
      return addById(c, itemId, quantity, null, -1, -1);
   }

   public static boolean addById(MapleClient c, int itemId, short quantity, long expiration) {
      return addById(c, itemId, quantity, null, -1, (byte) 0, expiration);
   }

   public static boolean addById(MapleClient c, int itemId, short quantity, String owner, int petId) {
      return addById(c, itemId, quantity, owner, petId, -1);
   }

   public static boolean addById(MapleClient c, int itemId, short quantity, String owner, int petId, long expiration) {
      return addById(c, itemId, quantity, owner, petId, (byte) 0, expiration);
   }

   public static boolean addById(MapleClient c, int itemId, short quantity, String owner, int petId, short flag, long expiration) {
      MapleCharacter chr = c.getPlayer();
      MapleInventoryType type = ItemConstants.getInventoryType(itemId);

      MapleInventory inv = chr.getInventory(type);
      inv.lockInventory();
      try {
         return addByIdInternal(c, chr, type, inv, itemId, quantity, owner, petId, flag, expiration);
      } finally {
         inv.unlockInventory();
      }
   }

   private static boolean addByIdInternal(MapleClient c, MapleCharacter chr, MapleInventoryType type, MapleInventory inv, int itemId, short quantity, String owner, int petId, short flag, long expiration) {
      MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
      if (!type.equals(MapleInventoryType.EQUIP)) {
         short slotMax = ii.getSlotMax(c, itemId);
         List<Item> existing = inv.listById(itemId);
         if (!ItemConstants.isRechargeable(itemId) && petId == -1) {
            if (existing.size() > 0) { // first update all existing slots to slotMax
               Iterator<Item> i = existing.iterator();
               while (quantity > 0) {
                  if (i.hasNext()) {
                     Item eItem = i.next();
                     short oldQ = eItem.quantity();
                     if (oldQ < slotMax && ((eItem.owner().equals(owner) || owner == null) && eItem.flag() == flag)) {
                        short newQ = (short) Math.min(oldQ + quantity, slotMax);
                        quantity -= (newQ - oldQ);
                        eItem = eItem.updateQuantity(newQ);
                        eItem = eItem.expiration(expiration);
                        PacketCreator.announce(c, new ModifyInventoryPacket(true, Collections.singletonList(new ModifyInventory(1, eItem))));
                     }
                  } else {
                     break;
                  }
               }
            }
            boolean sandboxItem = (flag & ItemConstants.SANDBOX) == ItemConstants.SANDBOX;
            while (quantity > 0) {
               short newQ = (short) Math.min(quantity, slotMax);
               if (newQ != 0) {
                  quantity -= newQ;
                  MaplePet pet = PetProcessor.getInstance().loadFromDb(itemId, (short) 0, petId);
                  Item nItem = Item.newBuilder(itemId)
                        .setPosition((short) 0)
                        .setQuantity(newQ)
                        .setPet(pet)
                        .setPetId(petId)
                        .setFlag(ItemProcessor.getInstance().setFlag(itemId, flag))
                        .setExpiration(expiration)
                        .setOwner(owner)
                        .build();
                  Optional<Item> itemWithSlot = inv.addItem(nItem);
                  if (itemWithSlot.isEmpty()) {
                     PacketCreator.announce(c, new InventoryFull());
                     PacketCreator.announce(c, new ShowInventoryFull());
                     return false;
                  }
                  PacketCreator.announce(c, new ModifyInventoryPacket(true, Collections.singletonList(new ModifyInventory(0, itemWithSlot.get()))));
                  if (sandboxItem) {
                     chr.setHasSandboxItem();
                  }
               } else {
                  PacketCreator.announce(c, new EnableActions());
                  return false;
               }
            }
         } else {
            MaplePet pet = PetProcessor.getInstance().loadFromDb(itemId, (short) 0, petId);
            Item nItem = Item.newBuilder(itemId)
                  .setPosition((short) 0)
                  .setQuantity(quantity)
                  .setPet(pet)
                  .setPetId(petId)
                  .setFlag(ItemProcessor.getInstance().setFlag(itemId, flag))
                  .setExpiration(expiration)
                  .build();
            Optional<Item> itemWithSlot = inv.addItem(nItem);
            if (itemWithSlot.isEmpty()) {
               PacketCreator.announce(c, new InventoryFull());
               PacketCreator.announce(c, new ShowInventoryFull());
               return false;
            }
            PacketCreator.announce(c, new ModifyInventoryPacket(true, Collections.singletonList(new ModifyInventory(0, itemWithSlot.get()))));
            if (MapleInventoryManipulator.isSandboxItem(nItem)) {
               chr.setHasSandboxItem();
            }
         }
      } else if (quantity == 1) {
         Item nEquip = Item.newBuilder(ii.getEquipById(itemId))
               .setFlag(ItemProcessor.getInstance().setFlag(itemId, flag))
               .setExpiration(expiration)
               .setOwner(owner)
               .build();
         Optional<Item> itemWithSlot = inv.addItem(nEquip);
         if (itemWithSlot.isEmpty()) {
            PacketCreator.announce(c, new InventoryFull());
            PacketCreator.announce(c, new ShowInventoryFull());
            return false;
         }
         nEquip = itemWithSlot.get();
         PacketCreator.announce(c, new ModifyInventoryPacket(true, Collections.singletonList(new ModifyInventory(0, nEquip))));
         if (MapleInventoryManipulator.isSandboxItem(nEquip)) {
            chr.setHasSandboxItem();
         }
      } else {
         throw new RuntimeException("Trying to create equip with non-one quantity");
      }
      return true;
   }

   public static Optional<Item> addFromDrop(MapleClient c, Item item) {
      return addFromDrop(c, item, true);
   }

   public static Optional<Item> addFromDrop(MapleClient c, Item item, boolean show) {
      return addFromDrop(c, item, show, item.petId());
   }

   public static Optional<Item> addFromDrop(MapleClient c, Item item, boolean show, int petId) {
      MapleCharacter chr = c.getPlayer();
      MapleInventoryType type = item.inventoryType();

      MapleInventory inv = chr.getInventory(type);
      inv.lockInventory();
      try {
         return addFromDropInternal(c, chr, type, inv, item, show, petId);
      } finally {
         inv.unlockInventory();
      }
   }

   //TODO JDT revisit
   private static Optional<Item> addFromDropInternal(MapleClient c, MapleCharacter chr, MapleInventoryType type, MapleInventory inv, Item item, boolean show, int petId) {
      MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();

      int itemId = item.id();
      if (ii.isPickupRestricted(itemId) && chr.haveItemWithId(itemId, true)) {
         PacketCreator.announce(c, new InventoryFull());
         PacketCreator.announce(c, new ShowItemUnavailable());
         return Optional.empty();
      }
      short quantity = item.quantity();

      if (!type.equals(MapleInventoryType.EQUIP)) {
         short slotMax = ii.getSlotMax(c, itemId);
         List<Item> existing = inv.listById(itemId);
         if (!ItemConstants.isRechargeable(itemId) && petId == -1) {
            if (existing.size() > 0) { // first update all existing slots to slotMax
               Iterator<Item> i = existing.iterator();
               while (quantity > 0) {
                  if (i.hasNext()) {
                     Item eItem = i.next();
                     short oldQ = eItem.quantity();
                     if (oldQ < slotMax && item.flag() == eItem.flag() && item.owner().equals(eItem.owner())) {
                        short newQ = (short) Math.min(oldQ + quantity, slotMax);
                        quantity -= (newQ - oldQ);
                        eItem = inv.getAndUpdate(eItem.position(), a -> a.setQuantity(newQ));
                        PacketCreator.announce(c, new ModifyInventoryPacket(true, Collections.singletonList(new ModifyInventory(1, eItem))));
                     }
                  } else {
                     break;
                  }
               }
            }
            while (quantity > 0) {
               short newQ = (short) Math.min(quantity, slotMax);
               quantity -= newQ;
               MaplePet pet = PetProcessor.getInstance().loadFromDb(itemId, (short) 0, petId);
               Item nItem = Item.newBuilder(itemId)
                     .setPosition((short) 0)
                     .setQuantity(newQ)
                     .setPet(pet)
                     .setPetId(petId)
                     .setExpiration(item.expiration())
                     .setOwner(item.owner())
                     .setFlag(ItemProcessor.getInstance().setFlag(itemId, item.flag()))
                     .build();
               Optional<Item> itemWithSlot = inv.addItem(nItem);
               if (itemWithSlot.isEmpty()) {
                  PacketCreator.announce(c, new InventoryFull());
                  PacketCreator.announce(c, new ShowInventoryFull());
                  //item = item.setQuantity((short) (quantity + newQ));
                  return Optional.empty();
               }
               // TODO JDT revisit this, this looks wonky
               nItem = itemWithSlot.get();
               item = nItem;
               PacketCreator.announce(c, new ModifyInventoryPacket(true, Collections.singletonList(new ModifyInventory(0, nItem))));
               if (MapleInventoryManipulator.isSandboxItem(nItem)) {
                  chr.setHasSandboxItem();
               }
            }
         } else {
            MaplePet pet = PetProcessor.getInstance().loadFromDb(itemId, (short) 0, petId);
            Item nItem = Item.newBuilder(itemId)
                  .setPosition((short) 0)
                  .setQuantity(quantity)
                  .setPet(pet)
                  .setPetId(petId)
                  .setExpiration(item.expiration())
                  .setFlag(ItemProcessor.getInstance().setFlag(itemId, item.flag()))
                  .build();
            Optional<Item> itemWithSlot = inv.addItem(nItem);
            if (itemWithSlot.isEmpty()) {
               PacketCreator.announce(c, new InventoryFull());
               PacketCreator.announce(c, new ShowInventoryFull());
               return Optional.empty();
            }
            nItem = itemWithSlot.get();
            item = nItem;
            PacketCreator.announce(c, new ModifyInventoryPacket(true, Collections.singletonList(new ModifyInventory(0, nItem))));
            if (MapleInventoryManipulator.isSandboxItem(nItem)) {
               chr.setHasSandboxItem();
            }
            PacketCreator.announce(c, new EnableActions());
         }
      } else if (quantity == 1) {
         Optional<Item> itemWithSlot = inv.addItem(item);
         if (itemWithSlot.isEmpty()) {
            PacketCreator.announce(c, new InventoryFull());
            PacketCreator.announce(c, new ShowInventoryFull());
            return Optional.empty();
         }
         item = itemWithSlot.get();
         PacketCreator.announce(c, new ModifyInventoryPacket(true, Collections.singletonList(new ModifyInventory(0, item))));
         if (MapleInventoryManipulator.isSandboxItem(item)) {
            chr.setHasSandboxItem();
         }
      } else {
         LoggerUtil.printError(LoggerOriginator.ITEM, "Tried to pickup Equip id " + itemId + " containing more than 1 quantity --> " + quantity);
         PacketCreator.announce(c, new InventoryFull());
         PacketCreator.announce(c, new ShowItemUnavailable());
         return Optional.empty();
      }
      if (show) {
         PacketCreator.announce(c, new ShowItemGain(itemId, item.quantity()));
      }
      return Optional.of(item);
   }

   private static boolean haveItemWithId(MapleInventory inv, int itemId) {
      return inv.findById(itemId) != null;
   }

   public static boolean checkSpace(MapleClient c, int itemId, int quantity, String owner) {
      MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
      MapleInventoryType type = ItemConstants.getInventoryType(itemId);
      MapleCharacter chr = c.getPlayer();
      MapleInventory inv = chr.getInventory(type);

      if (ii.isPickupRestricted(itemId)) {
         if (haveItemWithId(inv, itemId)) {
            return false;
         } else if (ItemConstants.isEquipment(itemId) && haveItemWithId(chr.getInventory(MapleInventoryType.EQUIPPED), itemId)) {
            return false;
         }
      }

      if (!type.equals(MapleInventoryType.EQUIP)) {
         final int numSlotsNeeded;
         short slotMax = ii.getSlotMax(c, itemId);
         List<Item> existing = inv.listById(itemId);

         if (ItemConstants.isRechargeable(itemId)) {
            numSlotsNeeded = 1;
         } else {
            if (existing.size() > 0) // first update all existing slots to slotMax
            {
               for (Item eItem : existing) {
                  short oldQ = eItem.quantity();
                  if (oldQ < slotMax && owner.equals(eItem.owner())) {
                     short newQ = (short) Math.min(oldQ + quantity, slotMax);
                     quantity -= (newQ - oldQ);
                  }
                  if (quantity <= 0) {
                     break;
                  }
               }
            }

            if (slotMax > 0) {
               numSlotsNeeded = (int) (Math.ceil(((double) quantity) / slotMax));
            } else {
               numSlotsNeeded = 1;
            }
         }

         return !inv.isFull(numSlotsNeeded - 1);
      } else {
         return !inv.isFull();
      }
   }

   public static int checkSpaceProgressively(MapleClient c, int itemId, int quantity, String owner, int usedSlots, boolean useProofInv) {
      // return value --> bit0: if has space for this one;
      //                  value after: new slots filled;
      // assumption: equipments always have slotMax == 1.

      int returnValue;

      MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
      MapleInventoryType type = !useProofInv ? ItemConstants.getInventoryType(itemId) : MapleInventoryType.CAN_HOLD;
      MapleCharacter chr = c.getPlayer();
      MapleInventory inv = chr.getInventory(type);

      if (ii.isPickupRestricted(itemId)) {
         if (haveItemWithId(inv, itemId)) {
            return 0;
         } else if (ItemConstants.isEquipment(itemId) && haveItemWithId(chr.getInventory(MapleInventoryType.EQUIPPED), itemId)) {
            return 0;
         }
      }

      if (!type.equals(MapleInventoryType.EQUIP)) {
         short slotMax = ii.getSlotMax(c, itemId);
         final int numSlotsNeeded;

         if (ItemConstants.isRechargeable(itemId)) {
            numSlotsNeeded = 1;
         } else {
            List<Item> existing = inv.listById(itemId);

            if (existing.size() > 0) // first update all existing slots to slotMax
            {
               for (Item eItem : existing) {
                  short oldQ = eItem.quantity();
                  if (oldQ < slotMax && owner.equals(eItem.owner())) {
                     short newQ = (short) Math.min(oldQ + quantity, slotMax);
                     quantity -= (newQ - oldQ);
                  }
                  if (quantity <= 0) {
                     break;
                  }
               }
            }

            if (slotMax > 0) {
               numSlotsNeeded = (int) (Math.ceil(((double) quantity) / slotMax));
            } else {
               numSlotsNeeded = 1;
            }
         }

         returnValue = ((numSlotsNeeded + usedSlots) << 1);
         returnValue += (numSlotsNeeded == 0 || !inv.isFullAfterSomeItems(numSlotsNeeded - 1, usedSlots)) ? 1 : 0;
         //System.out.print(" needed " + numSlotsNeeded + " used " + usedSlots + " returnValue " + returnValue);
      } else {
         returnValue = ((quantity + usedSlots) << 1);
         returnValue += (!inv.isFullAfterSomeItems(0, usedSlots)) ? 1 : 0;
         //System.out.print(" equip needed " + 1 + " used " + usedSlots + " returnValue " + returnValue);
      }

      return returnValue;
   }

   public static void removeFromSlot(MapleClient c, MapleInventoryType type, short slot, short quantity, boolean fromDrop) {
      removeFromSlot(c, type, slot, quantity, fromDrop, false);
   }

   public static void removeFromSlot(MapleClient c, MapleInventoryType type, short slot, short quantity, boolean fromDrop, boolean consume) {
      MapleCharacter chr = c.getPlayer();
      MapleInventory inv = chr.getInventory(type);
      Item item = inv.getItem(slot);
      boolean allowZero = consume && ItemConstants.isRechargeable(item.id());

      if (type == MapleInventoryType.EQUIPPED) {
         inv.lockInventory();
         try {
            chr.unequippedItem((Equip) item);
            item = inv.removeItem(slot, quantity, allowZero).orElseThrow();
         } finally {
            inv.unlockInventory();
         }

         announceModifyInventory(c, item, fromDrop, allowZero);
      } else {
         int petId = item.petId();
         if (petId > -1) {
            int petIdx = chr.getPetIndex(petId);
            if (petIdx > -1) {
               PetProcessor.getInstance().unequipPet(chr, (byte) petIdx, true);
            }
         }
         item = inv.removeItem(slot, quantity, allowZero).orElseThrow();
         if (type != MapleInventoryType.CAN_HOLD) {
            announceModifyInventory(c, item, fromDrop, allowZero);
         }
      }
   }

   private static void announceModifyInventory(MapleClient c, Item item, boolean fromDrop, boolean allowZero) {
      if (item.quantity() == 0 && !allowZero) {
         PacketCreator.announce(c, new ModifyInventoryPacket(fromDrop, Collections.singletonList(new ModifyInventory(3, item))));
      } else {
         PacketCreator.announce(c, new ModifyInventoryPacket(fromDrop, Collections.singletonList(new ModifyInventory(1, item))));
      }
   }

   public static void removeById(MapleClient c, MapleInventoryType type, int itemId, int quantity, boolean fromDrop, boolean consume) {
      int removeQuantity = quantity;
      MapleInventory inv = c.getPlayer().getInventory(type);
      int slotLimit = type == MapleInventoryType.EQUIPPED ? 128 : inv.getSlotLimit();

      for (short i = 0; i <= slotLimit; i++) {
         Item item = inv.getItem((short) (type == MapleInventoryType.EQUIPPED ? -i : i));
         if (item != null) {
            if (item.id() == itemId || item.cashId() == itemId) {
               if (removeQuantity <= item.quantity()) {
                  removeFromSlot(c, type, item.position(), (short) removeQuantity, fromDrop, consume);
                  removeQuantity = 0;
                  break;
               } else {
                  removeQuantity -= item.quantity();
                  removeFromSlot(c, type, item.position(), item.quantity(), fromDrop, consume);
               }
            }
         }
      }
      if (removeQuantity > 0 && type != MapleInventoryType.CAN_HOLD) {
         throw new RuntimeException("[Hack] Not enough items available of Item:" + itemId + ", Quantity (After Quantity/Over Current Quantity): " + (quantity - removeQuantity) + "/" + quantity);
      }
   }

   private static boolean isSameOwner(Item source, Item target) {
      return source.owner().equals(target.owner());
   }

   /**
    * Move an item from one slot to another.
    *
    * @param c    the client performing the action
    * @param type the type of inventory being moved
    * @param src  the source inventory slot
    * @param dst  the destination inventory slot
    */
   public static void move(MapleClient c, MapleInventoryType type, short src, short dst) {
      MapleInventory inv = c.getPlayer().getInventory(type);

      if (src < 0 || dst < 0) {
         return;
      }
      if (dst > inv.getSlotLimit()) {
         return;
      }
      MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
      Item source = inv.getItem(src);
      Item initialTarget = inv.getItem(dst);
      if (source == null) {
         return;
      }
      short oldDestinationQuantity = -1;
      if (initialTarget != null) {
         oldDestinationQuantity = initialTarget.quantity();
      }
      short oldSourceQuantity = source.quantity();
      short slotMax = ii.getSlotMax(c, source.id());

      inv.move(src, dst, slotMax);
      source = inv.getItem(src);
      initialTarget = inv.getItem(dst);

      final List<ModifyInventory> mods = new ArrayList<>();
      if (!(type.equals(MapleInventoryType.EQUIP) || type.equals(MapleInventoryType.CASH)) && initialTarget != null && initialTarget.id() == source.id() && !ItemConstants.isRechargeable(source.id()) && isSameOwner(source, initialTarget)) {
         if ((oldDestinationQuantity + oldSourceQuantity) > slotMax) {
            mods.add(new ModifyInventory(1, source));
         } else {
            mods.add(new ModifyInventory(3, source));
         }
         mods.add(new ModifyInventory(1, initialTarget));
      } else {
         mods.add(new ModifyInventory(2, source, src));
      }
      PacketCreator.announce(c, new ModifyInventoryPacket(true, mods));
   }

   /**
    * Add an item to the active equipment for a character.
    *
    * @param chr the character performing the action
    * @param src the source inventory slot
    * @param dst the destination inventory slot
    */
   public static void equip(MapleCharacter chr, short src, short dst) {
      MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();

      MapleInventory equipInventory = chr.getInventory(MapleInventoryType.EQUIP);
      MapleInventory equippedInventory = chr.getInventory(MapleInventoryType.EQUIPPED);

      Equip source = (Equip) equipInventory.getItem(src);

      if (source == null || !ii.canWearEquipment(chr, source, dst)) {
         PacketCreator.announce(chr, new EnableActions());
         return;
      } else if ((((source.id() >= 1902000 && source.id() <= 1902002) || source.id() == 1912000) && chr.isCygnus()) || ((source.id() >= 1902005 && source.id() <= 1902007) || source.id() == 1912005) && !chr.isCygnus()) {// Adventurer taming equipment
         return;
      }

      int itemId = source.id();
      boolean itemChanged = false;
      if (ii.isUntradeableOnEquip(itemId)) {
         equipInventory.getAndUpdate(src, item -> item.setFlag(ItemProcessor.getInstance().setFlag(itemId, (byte) ItemConstants.UNTRADEABLE)));
         itemChanged = true;
      }
      if (dst == -6) { // unequip the overall
         Item top = equippedInventory.getItem((short) -5);
         if (top != null && ItemConstants.isOverall(top.id())) {
            if (equipInventory.isFull()) {
               PacketCreator.announce(chr, new InventoryFull());
               PacketCreator.announce(chr, new ShowInventoryFull());
               return;
            }
            unequip(chr, (byte) -5, equipInventory.getNextFreeSlot());
         }
      } else if (dst == -5) {
         final Item bottom = equippedInventory.getItem((short) -6);
         if (bottom != null && ItemConstants.isOverall(itemId)) {
            if (equipInventory.isFull()) {
               PacketCreator.announce(chr, new InventoryFull());
               PacketCreator.announce(chr, new ShowInventoryFull());
               return;
            }
            unequip(chr, (byte) -6, equipInventory.getNextFreeSlot());
         }
      } else if (dst == -10) {// check if weapon is two-handed
         Item weapon = equippedInventory.getItem((short) -11);
         if (weapon != null && ii.isTwoHanded(weapon.id())) {
            if (equipInventory.isFull()) {
               PacketCreator.announce(chr, new InventoryFull());
               PacketCreator.announce(chr, new ShowInventoryFull());
               return;
            }
            unequip(chr, (byte) -11, equipInventory.getNextFreeSlot());
         }
      } else if (dst == -11) {
         Item shield = equippedInventory.getItem((short) -10);
         if (shield != null && ii.isTwoHanded(itemId)) {
            if (equipInventory.isFull()) {
               PacketCreator.announce(chr, new InventoryFull());
               PacketCreator.announce(chr, new ShowInventoryFull());
               return;
            }
            unequip(chr, (byte) -10, equipInventory.getNextFreeSlot());
         }
      }
      if (dst == -18) {
         if (chr.getMount() != null) {
            chr.modifyMount(mapleMount -> mapleMount.updateItemId(itemId));
         }
      }

      //1112413, 1112414, 1112405 (Lilin's Ring)
      source = (Equip) equipInventory.getItem(src);
      equipInventory.removeSlot(src);

      Equip target;
      equippedInventory.lockInventory();
      try {
         target = (Equip) equippedInventory.getItem(dst);
         if (target != null) {
            chr.unequippedItem(target);
            equippedInventory.removeSlot(dst);
         }
      } finally {
         equippedInventory.unlockInventory();
      }

      final List<ModifyInventory> mods = new ArrayList<>();
      if (itemChanged) {
         mods.add(new ModifyInventory(3, source));
         mods.add(new ModifyInventory(0, source.copy()));//to prevent crashes
      }

      source = (Equip) equipInventory.getAndUpdate(src, item -> item.setPosition(dst));

      equippedInventory.lockInventory();
      try {
         if (source.ringId() > -1) {
            chr.updatePlayerRing(chr.getRingById(source.ringId()).equip());
         }
         chr.equippedItem(source);
         equippedInventory.addItemFromDB(source);
      } finally {
         equippedInventory.unlockInventory();
      }

      if (target != null) {
         target = (Equip) target.setPosition(src);
         equipInventory.addItemFromDB(target);
      }
      if (chr.getBuffedValue(MapleBuffStat.BOOSTER) != null && ItemConstants.isWeapon(itemId)) {
         chr.cancelBuffStats(MapleBuffStat.BOOSTER);
      }

      mods.add(new ModifyInventory(2, source, src));
      PacketCreator.announce(chr, new ModifyInventoryPacket(true, mods));
      chr.equipChanged();
   }

   /**
    * Remove an item from the active equipment for a character.
    *
    * @param chr the character performing the action
    * @param src the source inventory slot
    * @param dst the destination inventory slot
    */
   public static void unequip(MapleCharacter chr, short src, short dst) {
      MapleInventory equipInventory = chr.getInventory(MapleInventoryType.EQUIP);
      MapleInventory equippedInventory = chr.getInventory(MapleInventoryType.EQUIPPED);

      Equip source = (Equip) equippedInventory.getItem(src);
      Equip target = (Equip) equipInventory.getItem(dst);
      if (dst < 0) {
         return;
      }
      if (source == null) {
         return;
      }
      if (target != null && src <= 0) {
         PacketCreator.announce(chr, new InventoryFull());
         return;
      }

      equippedInventory.lockInventory();
      try {
         if (source.ringId() > -1) {
            chr.updatePlayerRing(chr.getRingById(source.ringId()).equip());
         }
         chr.unequippedItem(source);
         equippedInventory.removeSlot(src);
      } finally {
         equippedInventory.unlockInventory();
      }

      if (target != null) {
         equipInventory.removeSlot(dst);
      }
      source = (Equip) equippedInventory.getAndUpdate(src, item -> item.setPosition(dst));
      equipInventory.addItemFromDB(source);
      if (target != null) {
         target = (Equip) equipInventory.getAndUpdate(dst, item -> item.setPosition(src));
         equippedInventory.addItemFromDB(target);
      }
      PacketCreator.announce(chr, new ModifyInventoryPacket(true, Collections.singletonList(new ModifyInventory(2, source, src))));
      chr.equipChanged();
   }


   private static boolean isDisappearingItemDrop(Item it) {
      MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
      if (ii.isDropRestricted(it.id())) {
         return true;
      } else if (ii.isCash(it.id())) {
         if (YamlConfig.config.server.USE_ENFORCE_UNMERCHABLE_CASH) {
            return true;
         } else {
            return ItemConstants.isPet(it.id()) && YamlConfig.config.server.USE_ENFORCE_UNMERCHABLE_PET;
         }
      } else if (isDroppedItemRestricted(it)) {
         return true;
      } else {
         return ItemConstants.isWeddingRing(it.id());
      }

   }

   /**
    * Drops an item of a set quantity from the characters inventory.
    *
    * @param chr      the character dropping
    * @param type     the inventory type being modified
    * @param slot     the slot the item is being dropped from
    * @param quantity the quantity to drop
    */
   public static void drop(MapleCharacter chr, MapleInventoryType type, short slot, short quantity) {
      if (slot < 0) {
         type = MapleInventoryType.EQUIPPED;
      }

      MapleInventory inv = chr.getInventory(type);
      Item source = inv.getItem(slot);

      //Only check needed would prob be merchants (to see if the player is in one)
      if (chr.getTrade().isPresent() || chr.getMiniGame() != null || source == null) {
         return;
      }
      int itemId = source.id();

      MapleMap map = chr.getMap();
      if ((!ItemConstants.isRechargeable(itemId) && source.quantity() < quantity) || quantity < 0) {
         return;
      }

      int petId = source.petId();
      if (petId > -1) {
         int petIdx = chr.getPetIndex(petId);
         if (petIdx > -1) {
            PetProcessor.getInstance().unequipPet(chr, (byte) petIdx, true);
         }
      }

      Point dropPos = new Point(chr.position());
      if (quantity < source.quantity() && !ItemConstants.isRechargeable(itemId)) {
         Item target = source.copy().setQuantity(quantity);
         short remainder = (short) (source.quantity() - quantity);
         source = inv.getAndUpdate(slot, item -> item.setQuantity(remainder));
         PacketCreator.announce(chr, new ModifyInventoryPacket(true, Collections.singletonList(new ModifyInventory(1, source))));

         if (ItemConstants.isNewYearCardEtc(itemId)) {
            if (itemId == 4300000) {
               NewYearCardProcessor.getInstance().removeAllNewYearCard(true, chr);
               chr.getAbstractPlayerInteraction().removeAll(4300000);
            } else {
               NewYearCardProcessor.getInstance().removeAllNewYearCard(false, chr);
               chr.getAbstractPlayerInteraction().removeAll(4301000);
            }
         }

         if (isDisappearingItemDrop(target)) {
            map.disappearingItemDrop(chr, chr, target, dropPos);
         } else {
            map.spawnItemDrop(chr, chr, target, dropPos, true, true);
         }
      } else {
         if (type == MapleInventoryType.EQUIPPED) {
            inv.lockInventory();
            try {
               chr.unequippedItem((Equip) source);
               inv.removeSlot(slot);
            } finally {
               inv.unlockInventory();
            }
         } else {
            inv.removeSlot(slot);
         }

         PacketCreator.announce(chr, new ModifyInventoryPacket(true, Collections.singletonList(new ModifyInventory(3, source))));
         if (slot < 0) {
            chr.equipChanged();
         } else if (ItemConstants.isNewYearCardEtc(itemId)) {
            if (itemId == 4300000) {
               NewYearCardProcessor.getInstance().removeAllNewYearCard(true, chr);
               chr.getAbstractPlayerInteraction().removeAll(4300000);
            } else {
               NewYearCardProcessor.getInstance().removeAllNewYearCard(false, chr);
               chr.getAbstractPlayerInteraction().removeAll(4301000);
            }
         }

         if (isDisappearingItemDrop(source)) {
            map.disappearingItemDrop(chr, chr, source, dropPos);
         } else {
            map.spawnItemDrop(chr, chr, source, dropPos, true, true);
         }
      }

      int quantityNow = chr.getItemQuantity(itemId, false);
      if (itemId == chr.getItemEffect()) {
         if (quantityNow <= 0) {
            chr.setItemEffect(0);
            MasterBroadcaster.getInstance().sendToAllInMap(map, new ShowItemEffect(chr.getId(), 0));
         }
      } else if (itemId == 5370000 || itemId == 5370001) {
         if (source.quantity() <= 0) {
            chr.setChalkboard(null);
         }
      } else if (itemId == 4031868) {
         chr.updateAriantScore(quantityNow);
      }
   }

   private static boolean isDroppedItemRestricted(Item it) {
      return YamlConfig.config.server.USE_ERASE_UNTRADEABLE_DROP && ItemProcessor.getInstance().isUnableToBeTraded(it);
   }

   public static boolean isSandboxItem(Item it) {
      return (it.flag() & ItemConstants.SANDBOX) == ItemConstants.SANDBOX;
   }
}
