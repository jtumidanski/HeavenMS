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
package client.inventory.manipulator;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import client.MapleBuffStat;
import client.MapleCharacter;
import client.MapleClient;
import client.inventory.BetterItemFactory;
import client.inventory.Equip;
import client.inventory.Item;
import client.inventory.MapleInventory;
import client.inventory.MapleInventoryType;
import client.inventory.MaplePet;
import client.inventory.ModifyInventory;
import client.processor.ItemProcessor;
import client.processor.NewYearCardProcessor;
import config.YamlConfig;
import constants.inventory.ItemConstants;
import server.MapleItemInformationProvider;
import server.maps.MapleMap;
import tools.FilePrinter;
import tools.MasterBroadcaster;
import tools.PacketCreator;
import tools.packet.foreigneffect.ShowItemEffect;
import tools.packet.inventory.InventoryFull;
import tools.packet.inventory.ModifyInventoryPacket;
import tools.packet.stat.EnableActions;
import tools.packet.statusinfo.ShowInventoryFull;
import tools.packet.statusinfo.ShowItemGain;
import tools.packet.statusinfo.ShowItemUnavailable;

/**
 * @author Matze
 * @author Ronan - improved check space feature & removed redundant object calls
 */
public class MapleInventoryManipulator {

   public static boolean addById(MapleClient c, int itemId, short quantity) {
      return addById(c, itemId, quantity, null, -1, -1);
   }

   public static boolean addById(MapleClient c, int itemId, short quantity, long expiration) {
      return addById(c, itemId, quantity, null, -1, (byte) 0, expiration);
   }

   public static boolean addById(MapleClient c, int itemId, short quantity, String owner, int petid) {
      return addById(c, itemId, quantity, owner, petid, -1);
   }

   public static boolean addById(MapleClient c, int itemId, short quantity, String owner, int petid, long expiration) {
      return addById(c, itemId, quantity, owner, petid, (byte) 0, expiration);
   }

   public static boolean addById(MapleClient c, int itemId, short quantity, String owner, int petid, short flag, long expiration) {
      MapleCharacter chr = c.getPlayer();
      MapleInventoryType type = ItemConstants.getInventoryType(itemId);

      MapleInventory inv = chr.getInventory(type);
      inv.lockInventory();
      try {
         return addByIdInternal(c, chr, type, inv, itemId, quantity, owner, petid, flag, expiration);
      } finally {
         inv.unlockInventory();
      }
   }

   private static boolean addByIdInternal(MapleClient c, MapleCharacter chr, MapleInventoryType type, MapleInventory inv, int itemId, short quantity, String owner, int petid, short flag, long expiration) {
      MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
      if (!type.equals(MapleInventoryType.EQUIP)) {
         short slotMax = ii.getSlotMax(c, itemId);
         List<Item> existing = inv.listById(itemId);
         if (!ItemConstants.isRechargeable(itemId) && petid == -1) {
            if (existing.size() > 0) { // first update all existing slots to slotMax
               Iterator<Item> i = existing.iterator();
               while (quantity > 0) {
                  if (i.hasNext()) {
                     Item eItem = i.next();
                     short oldQ = eItem.quantity();
                     if (oldQ < slotMax && ((eItem.owner().equals(owner) || owner == null) && eItem.flag() == flag)) {
                        short newQ = (short) Math.min(oldQ + quantity, slotMax);
                        quantity -= (newQ - oldQ);
                        eItem.quantity_$eq(newQ);
                        eItem.expiration_(expiration);
                        PacketCreator.announce(c, new ModifyInventoryPacket(true, Collections.singletonList(new ModifyInventory(1, eItem))));
                     }
                  } else {
                     break;
                  }
               }
            }
            boolean sandboxItem = (flag & ItemConstants.SANDBOX) == ItemConstants.SANDBOX;
            while (quantity > 0 || ItemConstants.isRechargeable(itemId)) {
               short newQ = (short) Math.min(quantity, slotMax);
               if (newQ != 0) {
                  quantity -= newQ;
                  Item nItem = BetterItemFactory.getInstance().create(itemId, (short) 0, newQ, petid);
                  ItemProcessor.getInstance().setFlag(nItem, flag);
                  nItem.expiration_(expiration);
                  short newSlot = inv.addItem(nItem);
                  if (newSlot == -1) {
                     PacketCreator.announce(c, new InventoryFull());
                     PacketCreator.announce(c, new ShowInventoryFull());
                     return false;
                  }
                  if (owner != null) {
                     nItem.owner_$eq(owner);
                  }
                  PacketCreator.announce(c, new ModifyInventoryPacket(true, Collections.singletonList(new ModifyInventory(0, nItem))));
                  if (sandboxItem) {
                     chr.setHasSandboxItem();
                  }
                  if ((ItemConstants.isRechargeable(itemId)) && quantity == 0) {
                     break;
                  }
               } else {
                  PacketCreator.announce(c, new EnableActions());
                  return false;
               }
            }
         } else {
            Item nItem = BetterItemFactory.getInstance().create(itemId, (short) 0, quantity, petid);
            ItemProcessor.getInstance().setFlag(nItem, flag);
            nItem.expiration_(expiration);
            short newSlot = inv.addItem(nItem);
            if (newSlot == -1) {
               PacketCreator.announce(c, new InventoryFull());
               PacketCreator.announce(c, new ShowInventoryFull());
               return false;
            }
            PacketCreator.announce(c, new ModifyInventoryPacket(true, Collections.singletonList(new ModifyInventory(0, nItem))));
            if (MapleInventoryManipulator.isSandboxItem(nItem)) {
               chr.setHasSandboxItem();
            }
         }
      } else if (quantity == 1) {
         Item nEquip = ii.getEquipById(itemId);
         ItemProcessor.getInstance().setFlag(nEquip, flag);
         nEquip.expiration_(expiration);
         if (owner != null) {
            nEquip.owner_$eq(owner);
         }
         short newSlot = inv.addItem(nEquip);
         if (newSlot == -1) {
            PacketCreator.announce(c, new InventoryFull());
            PacketCreator.announce(c, new ShowInventoryFull());
            return false;
         }
         PacketCreator.announce(c, new ModifyInventoryPacket(true, Collections.singletonList(new ModifyInventory(0, nEquip))));
         if (MapleInventoryManipulator.isSandboxItem(nEquip)) {
            chr.setHasSandboxItem();
         }
      } else {
         throw new RuntimeException("Trying to create equip with non-one quantity");
      }
      return true;
   }

   public static boolean addFromDrop(MapleClient c, Item item) {
      return addFromDrop(c, item, true);
   }

   public static boolean addFromDrop(MapleClient c, Item item, boolean show) {
      return addFromDrop(c, item, show, item.petId());
   }

   public static boolean addFromDrop(MapleClient c, Item item, boolean show, int petId) {
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

   private static boolean addFromDropInternal(MapleClient c, MapleCharacter chr, MapleInventoryType type, MapleInventory inv, Item item, boolean show, int petId) {
      MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();

      if (ii.isPickupRestricted(item.id()) && chr.haveItemWithId(item.id(), true)) {
         PacketCreator.announce(c, new InventoryFull());
         PacketCreator.announce(c, new ShowItemUnavailable());
         return false;
      }
      short quantity = item.quantity();

      if (!type.equals(MapleInventoryType.EQUIP)) {
         short slotMax = ii.getSlotMax(c, item.id());
         List<Item> existing = inv.listById(item.id());
         if (!ItemConstants.isRechargeable(item.id()) && petId == -1) {
            if (existing.size() > 0) { // first update all existing slots to slotMax
               Iterator<Item> i = existing.iterator();
               while (quantity > 0) {
                  if (i.hasNext()) {
                     Item eItem = i.next();
                     short oldQ = eItem.quantity();
                     if (oldQ < slotMax && item.flag() == eItem.flag() && item.owner().equals(eItem.owner())) {
                        short newQ = (short) Math.min(oldQ + quantity, slotMax);
                        quantity -= (newQ - oldQ);
                        eItem.quantity_$eq(newQ);
                        item.position_(eItem.position());
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
               Item nItem = BetterItemFactory.getInstance().create(item.id(), (short) 0, newQ, petId);
               nItem.expiration_(item.expiration());
               nItem.owner_$eq(item.owner());
               ItemProcessor.getInstance().setFlag(nItem, item.flag());
               short newSlot = inv.addItem(nItem);
               if (newSlot == -1) {
                  PacketCreator.announce(c, new InventoryFull());
                  PacketCreator.announce(c, new ShowInventoryFull());
                  item.quantity_$eq((short) (quantity + newQ));
                  return false;
               }
               nItem.position_(newSlot);
               item.position_(newSlot);
               PacketCreator.announce(c, new ModifyInventoryPacket(true, Collections.singletonList(new ModifyInventory(0, nItem))));
               if (MapleInventoryManipulator.isSandboxItem(nItem)) {
                  chr.setHasSandboxItem();
               }
            }
         } else {
            Item nItem = BetterItemFactory.getInstance().create(item.id(), (short) 0, quantity, petId);
            nItem.expiration_(item.expiration());
            ItemProcessor.getInstance().setFlag(nItem, item.flag());

            short newSlot = inv.addItem(nItem);
            if (newSlot == -1) {
               PacketCreator.announce(c, new InventoryFull());
               PacketCreator.announce(c, new ShowInventoryFull());
               return false;
            }
            nItem.position_(newSlot);
            item.position_(newSlot);
            PacketCreator.announce(c, new ModifyInventoryPacket(true, Collections.singletonList(new ModifyInventory(0, nItem))));
            if (MapleInventoryManipulator.isSandboxItem(nItem)) {
               chr.setHasSandboxItem();
            }
            PacketCreator.announce(c, new EnableActions());
         }
      } else if (quantity == 1) {
         short newSlot = inv.addItem(item);
         if (newSlot == -1) {
            PacketCreator.announce(c, new InventoryFull());
            PacketCreator.announce(c, new ShowInventoryFull());
            return false;
         }
         item.position_(newSlot);
         PacketCreator.announce(c, new ModifyInventoryPacket(true, Collections.singletonList(new ModifyInventory(0, item))));
         if (MapleInventoryManipulator.isSandboxItem(item)) {
            chr.setHasSandboxItem();
         }
      } else {
         FilePrinter.printError(FilePrinter.ITEM, "Tried to pickup Equip id " + item.id() + " containing more than 1 quantity --> " + quantity);
         PacketCreator.announce(c, new InventoryFull());
         PacketCreator.announce(c, new ShowItemUnavailable());
         return false;
      }
      if (show) {
         PacketCreator.announce(c, new ShowItemGain(item.id(), item.quantity()));
      }
      return true;
   }

   private static boolean haveItemWithId(MapleInventory inv, int itemid) {
      return inv.findById(itemid) != null;
   }

   public static boolean checkSpace(MapleClient c, int itemid, int quantity, String owner) {
      MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
      MapleInventoryType type = ItemConstants.getInventoryType(itemid);
      MapleCharacter chr = c.getPlayer();
      MapleInventory inv = chr.getInventory(type);

      if (ii.isPickupRestricted(itemid)) {
         if (haveItemWithId(inv, itemid)) {
            return false;
         } else if (ItemConstants.isEquipment(itemid) && haveItemWithId(chr.getInventory(MapleInventoryType.EQUIPPED), itemid)) {
            return false;
         }
      }

      if (!type.equals(MapleInventoryType.EQUIP)) {
         short slotMax = ii.getSlotMax(c, itemid);
         List<Item> existing = inv.listById(itemid);

         final int numSlotsNeeded;
         if (ItemConstants.isRechargeable(itemid)) {
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

   public static int checkSpaceProgressively(MapleClient c, int itemid, int quantity, String owner, int usedSlots, boolean useProofInv) {
      // return value --> bit0: if has space for this one;
      //                  value after: new slots filled;
      // assumption: equipments always have slotMax == 1.

      int returnValue;

      MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
      MapleInventoryType type = !useProofInv ? ItemConstants.getInventoryType(itemid) : MapleInventoryType.CANHOLD;
      MapleCharacter chr = c.getPlayer();
      MapleInventory inv = chr.getInventory(type);

      if (ii.isPickupRestricted(itemid)) {
         if (haveItemWithId(inv, itemid)) {
            return 0;
         } else if (ItemConstants.isEquipment(itemid) && haveItemWithId(chr.getInventory(MapleInventoryType.EQUIPPED), itemid)) {
            return 0;   // thanks Captain & Aika & Vcoc for pointing out inventory checkup on player trades missing out one-of-a-kind items.
         }
      }

      if (!type.equals(MapleInventoryType.EQUIP)) {
         short slotMax = ii.getSlotMax(c, itemid);
         final int numSlotsNeeded;

         if (ItemConstants.isRechargeable(itemid)) {
            numSlotsNeeded = 1;
         } else {
            List<Item> existing = inv.listById(itemid);

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
         //System.out.print(" needed " + numSlotsNeeded + " used " + usedSlots + " rval " + returnValue);
      } else {
         returnValue = ((quantity + usedSlots) << 1);
         returnValue += (!inv.isFullAfterSomeItems(0, usedSlots)) ? 1 : 0;
         //System.out.print(" eqpneeded " + 1 + " used " + usedSlots + " rval " + returnValue);
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
            inv.removeItem(slot, quantity, allowZero);
         } finally {
            inv.unlockInventory();
         }

         announceModifyInventory(c, item, fromDrop, allowZero);
      } else {
         int petid = item.petId();
         if (petid > -1) { // thanks Vcoc for finding a d/c issue with equipped pets & pets remaining on DB here
            int petIdx = chr.getPetIndex(petid);
            if (petIdx > -1) {
               MaplePet pet = chr.getPet(petIdx);
               chr.unequipPet(pet, true);
            }

            inv.removeItem(slot, quantity, allowZero);
            if (type != MapleInventoryType.CANHOLD) {
               announceModifyInventory(c, item, fromDrop, allowZero);
            }

            // thanks Robin Schulz for noticing pet issues when moving pets out of inventory
         } else {
            inv.removeItem(slot, quantity, allowZero);
            if (type != MapleInventoryType.CANHOLD) {
               announceModifyInventory(c, item, fromDrop, allowZero);
            }
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
      if (removeQuantity > 0 && type != MapleInventoryType.CANHOLD) {
         throw new RuntimeException("[Hack] Not enough items available of Item:" + itemId + ", Quantity (After Quantity/Over Current Quantity): " + (quantity - removeQuantity) + "/" + quantity);
      }
   }

   private static boolean isSameOwner(Item source, Item target) {
      return source.owner().equals(target.owner());
   }

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
      short olddstQ = -1;
      if (initialTarget != null) {
         olddstQ = initialTarget.quantity();
      }
      short oldsrcQ = source.quantity();
      short slotMax = ii.getSlotMax(c, source.id());
      inv.move(src, dst, slotMax);
      final List<ModifyInventory> mods = new ArrayList<>();
      if (!(type.equals(MapleInventoryType.EQUIP) || type.equals(MapleInventoryType.CASH)) && initialTarget != null && initialTarget.id() == source.id() && !ItemConstants.isRechargeable(source.id()) && isSameOwner(source, initialTarget)) {
         if ((olddstQ + oldsrcQ) > slotMax) {
            mods.add(new ModifyInventory(1, source));
            mods.add(new ModifyInventory(1, initialTarget));
         } else {
            mods.add(new ModifyInventory(3, source));
            mods.add(new ModifyInventory(1, initialTarget));
         }
      } else {
         mods.add(new ModifyInventory(2, source, src));
      }
      PacketCreator.announce(c, new ModifyInventoryPacket(true, mods));
   }

   public static void equip(MapleClient c, short src, short dst) {
      MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();

      MapleCharacter chr = c.getPlayer();
      MapleInventory eqpInv = chr.getInventory(MapleInventoryType.EQUIP);
      MapleInventory eqpdInv = chr.getInventory(MapleInventoryType.EQUIPPED);

      Equip source = (Equip) eqpInv.getItem(src);
      if (source == null || !ii.canWearEquipment(chr, source, dst)) {
         PacketCreator.announce(c, new EnableActions());
         return;
      } else if ((((source.id() >= 1902000 && source.id() <= 1902002) || source.id() == 1912000) && chr.isCygnus()) || ((source.id() >= 1902005 && source.id() <= 1902007) || source.id() == 1912005) && !chr.isCygnus()) {// Adventurer taming equipment
         return;
      }
      boolean itemChanged = false;
      if (ii.isUntradeableOnEquip(source.id())) {
         ItemProcessor.getInstance().setFlag(source, (byte) ItemConstants.UNTRADEABLE);
         itemChanged = true;
      }
      if (dst == -6) { // unequip the overall
         Item top = eqpdInv.getItem((short) -5);
         if (top != null && ItemConstants.isOverall(top.id())) {
            if (eqpInv.isFull()) {
               PacketCreator.announce(c, new InventoryFull());
               PacketCreator.announce(c, new ShowInventoryFull());
               return;
            }
            unequip(c, (byte) -5, eqpInv.getNextFreeSlot());
         }
      } else if (dst == -5) {
         final Item bottom = eqpdInv.getItem((short) -6);
         if (bottom != null && ItemConstants.isOverall(source.id())) {
            if (eqpInv.isFull()) {
               PacketCreator.announce(c, new InventoryFull());
               PacketCreator.announce(c, new ShowInventoryFull());
               return;
            }
            unequip(c, (byte) -6, eqpInv.getNextFreeSlot());
         }
      } else if (dst == -10) {// check if weapon is two-handed
         Item weapon = eqpdInv.getItem((short) -11);
         if (weapon != null && ii.isTwoHanded(weapon.id())) {
            if (eqpInv.isFull()) {
               PacketCreator.announce(c, new InventoryFull());
               PacketCreator.announce(c, new ShowInventoryFull());
               return;
            }
            unequip(c, (byte) -11, eqpInv.getNextFreeSlot());
         }
      } else if (dst == -11) {
         Item shield = eqpdInv.getItem((short) -10);
         if (shield != null && ii.isTwoHanded(source.id())) {
            if (eqpInv.isFull()) {
               PacketCreator.announce(c, new InventoryFull());
               PacketCreator.announce(c, new ShowInventoryFull());
               return;
            }
            unequip(c, (byte) -10, eqpInv.getNextFreeSlot());
         }
      }
      if (dst == -18) {
         if (chr.getMount() != null) {
            chr.getMount().itemId_$eq(source.id());
         }
      }

      //1112413, 1112414, 1112405 (Lilin's Ring)
      source = (Equip) eqpInv.getItem(src);
      eqpInv.removeSlot(src);

      Equip target;
      eqpdInv.lockInventory();
      try {
         target = (Equip) eqpdInv.getItem(dst);
         if (target != null) {
            chr.unequippedItem(target);
            eqpdInv.removeSlot(dst);
         }
      } finally {
         eqpdInv.unlockInventory();
      }

      final List<ModifyInventory> mods = new ArrayList<>();
      if (itemChanged) {
         mods.add(new ModifyInventory(3, source));
         mods.add(new ModifyInventory(0, source.copy()));//to prevent crashes
      }

      source.position_(dst);

      eqpdInv.lockInventory();
      try {
         if (source.ringId() > -1) {
            chr.getRingById(source.ringId()).equip();
         }
         chr.equippedItem(source);
         eqpdInv.addItemFromDB(source);
      } finally {
         eqpdInv.unlockInventory();
      }

      if (target != null) {
         target.position_(src);
         eqpInv.addItemFromDB(target);
      }
      if (chr.getBuffedValue(MapleBuffStat.BOOSTER) != null && ItemConstants.isWeapon(source.id())) {
         chr.cancelBuffStats(MapleBuffStat.BOOSTER);
      }

      mods.add(new ModifyInventory(2, source, src));
      PacketCreator.announce(c, new ModifyInventoryPacket(true, mods));
      chr.equipChanged();
   }

   public static void unequip(MapleClient c, short src, short dst) {
      MapleCharacter chr = c.getPlayer();
      MapleInventory eqpInv = chr.getInventory(MapleInventoryType.EQUIP);
      MapleInventory eqpdInv = chr.getInventory(MapleInventoryType.EQUIPPED);

      Equip source = (Equip) eqpdInv.getItem(src);
      Equip target = (Equip) eqpInv.getItem(dst);
      if (dst < 0) {
         return;
      }
      if (source == null) {
         return;
      }
      if (target != null && src <= 0) {
         PacketCreator.announce(c, new InventoryFull());
         return;
      }

      eqpdInv.lockInventory();
      try {
         if (source.ringId() > -1) {
            chr.getRingById(source.ringId()).unequip();
         }
         chr.unequippedItem(source);
         eqpdInv.removeSlot(src);
      } finally {
         eqpdInv.unlockInventory();
      }

      if (target != null) {
         eqpInv.removeSlot(dst);
      }
      source.position_(dst);
      eqpInv.addItemFromDB(source);
      if (target != null) {
         target.position_(src);
         eqpdInv.addItemFromDB(target);
      }
      PacketCreator.announce(c, new ModifyInventoryPacket(true, Collections.singletonList(new ModifyInventory(2, source, src))));
      chr.equipChanged();
   }


   private static boolean isDisappearingItemDrop(Item it) {
      MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
      if (ii.isDropRestricted(it.id())) {
         return true;
      } else if (ii.isCash(it.id())) {
         if (YamlConfig.config.server.USE_ENFORCE_UNMERCHABLE_CASH) {     // thanks Ari for noticing cash drops not available server-side
            return true;
         } else if (ItemConstants.isPet(it.id()) && YamlConfig.config.server.USE_ENFORCE_UNMERCHABLE_PET) {
            return true;
         }
      } else if (isDroppedItemRestricted(it)) {
         return true;
      } else if (ItemConstants.isWeddingRing(it.id())) {
         return true;
      }

      return false;
   }

   public static void drop(MapleClient c, MapleInventoryType type, short src, short quantity) {
      if (src < 0) {
         type = MapleInventoryType.EQUIPPED;
      }

      MapleCharacter chr = c.getPlayer();
      MapleInventory inv = chr.getInventory(type);
      Item source = inv.getItem(src);

      if (chr.getTrade().isPresent() || chr.getMiniGame() != null || source == null) { //Only check needed would prob be merchants (to see if the player is in one)
         return;
      }
      int itemId = source.id();

      MapleMap map = chr.getMap();
      if ((!ItemConstants.isRechargeable(itemId) && source.quantity() < quantity) || quantity < 0) {
         return;
      }

      int petid = source.petId();
      if (petid > -1) {
         int petIdx = chr.getPetIndex(petid);
         if (petIdx > -1) {
            MaplePet pet = chr.getPet(petIdx);
            chr.unequipPet(pet, true);
         }
      }

      Point dropPos = new Point(chr.position());
      if (quantity < source.quantity() && !ItemConstants.isRechargeable(itemId)) {
         Item target = source.copy();
         target.quantity_$eq(quantity);
         source.quantity_$eq((short) (source.quantity() - quantity));
         PacketCreator.announce(c, new ModifyInventoryPacket(true, Collections.singletonList(new ModifyInventory(1, source))));

         if (ItemConstants.isNewYearCardEtc(itemId)) {
            if (itemId == 4300000) {
               NewYearCardProcessor.getInstance().removeAllNewYearCard(true, chr);
               c.getAbstractPlayerInteraction().removeAll(4300000);
            } else {
               NewYearCardProcessor.getInstance().removeAllNewYearCard(false, chr);
               c.getAbstractPlayerInteraction().removeAll(4301000);
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
               inv.removeSlot(src);
            } finally {
               inv.unlockInventory();
            }
         } else {
            inv.removeSlot(src);
         }

         PacketCreator.announce(c, new ModifyInventoryPacket(true, Collections.singletonList(new ModifyInventory(3, source))));
         if (src < 0) {
            chr.equipChanged();
         } else if (ItemConstants.isNewYearCardEtc(itemId)) {
            if (itemId == 4300000) {
               NewYearCardProcessor.getInstance().removeAllNewYearCard(true, chr);
               c.getAbstractPlayerInteraction().removeAll(4300000);
            } else {
               NewYearCardProcessor.getInstance().removeAllNewYearCard(false, chr);
               c.getAbstractPlayerInteraction().removeAll(4301000);
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
      return YamlConfig.config.server.USE_ERASE_UNTRADEABLE_DROP && ItemProcessor.getInstance().isUntradeable(it);
   }

   public static boolean isSandboxItem(Item it) {
      return (it.flag() & ItemConstants.SANDBOX) == ItemConstants.SANDBOX;
   }
}
