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

import java.util.Calendar;
import java.util.List;

import client.MapleCharacter;
import client.MapleClient;
import client.MapleRing;
import client.database.data.CharacterIdNameAccountId;
import client.inventory.Equip;
import client.inventory.Item;
import client.inventory.MapleInventory;
import client.inventory.MapleInventoryType;
import client.inventory.manipulator.MapleInventoryManipulator;
import client.processor.CharacterProcessor;
import client.processor.MapleRingProcessor;
import client.processor.NoteProcessor;
import constants.ItemConstants;
import constants.ServerConstants;
import net.AbstractMaplePacketHandler;
import net.server.Server;
import server.CashShop;
import server.CashShop.CashItem;
import server.CashShop.CashItemFactory;
import server.MapleItemInformationProvider;
import tools.FilePrinter;
import tools.MaplePacketCreator;
import tools.MessageBroadcaster;
import tools.Pair;
import tools.ServerNoticeType;
import tools.data.input.SeekableLittleEndianAccessor;

public final class CashOperationHandler extends AbstractMaplePacketHandler {

   public static boolean checkBirthday(MapleClient c, int idate) {
      int year = idate / 10000;
      int month = (idate - year * 10000) / 100;
      int day = idate - year * 10000 - month * 100;
      Calendar cal = Calendar.getInstance();
      cal.setTimeInMillis(0);
      cal.set(year, month - 1, day);
      return c.checkBirthDate(cal);
   }

   private static boolean canBuy(MapleCharacter chr, CashItem item, int cash) {
      if (item != null && item.isOnSale() && item.getPrice() <= cash) {
         FilePrinter.print(FilePrinter.CASHITEM_BOUGHT, chr + " bought " + MapleItemInformationProvider.getInstance().getName(item.getItemId()) + " (SN " + item.getSN() + ") for " + item.getPrice());
         return true;
      } else {
         return false;
      }
   }

   @Override
   public final void handlePacket(SeekableLittleEndianAccessor slea, MapleClient c) {
      MapleCharacter chr = c.getPlayer();
      CashShop cs = chr.getCashShop();

      if (!cs.isOpened()) {
         c.announce(MaplePacketCreator.enableActions());
         return;
      }

      if (c.tryAcquireClient()) {     // thanks Thora for finding out an exploit within cash operations
         try {
            final int action = slea.readByte();
            if (action == 0x03 || action == 0x1E) {
               slea.readByte();
               final int useNX = slea.readInt();
               final int snCS = slea.readInt();
               CashItem cItem = CashItemFactory.getItem(snCS);
               if (!canBuy(chr, cItem, cs.getCash(useNX))) {
                  FilePrinter.printError(FilePrinter.ITEM, "Denied to sell cash item with SN " + snCS);   // preventing NPE here thanks to MedicOP
                  c.enableCSActions();
                  return;
               }

               if (action == 0x03) { // Item
                  if (ItemConstants.isCashStore(cItem.getItemId()) && chr.getLevel() < 16) {
                     c.enableCSActions();
                     return;
                  } else if (ItemConstants.isRateCoupon(cItem.getItemId()) && !ServerConstants.USE_SUPPLY_RATE_COUPONS) {
                     MessageBroadcaster.getInstance().sendServerNotice(chr, ServerNoticeType.POP_UP, "Rate coupons are currently unavailable to purchase.");
                     c.enableCSActions();
                     return;
                  } else if (ItemConstants.isMapleLife(cItem.getItemId()) && chr.getLevel() < 30) {
                     c.enableCSActions();
                     return;
                  }

                  Item item = cItem.toItem();
                  cs.addToInventory(item);
                  c.announce(MaplePacketCreator.showBoughtCashItem(item, c.getAccID()));
               } else { // Package
                  List<Item> cashPackage = CashItemFactory.getPackage(cItem.getItemId());
                  for (Item item : cashPackage) {
                     cs.addToInventory(item);
                  }
                  c.announce(MaplePacketCreator.showBoughtCashPackage(cashPackage, c.getAccID()));
               }
               cs.gainCash(useNX, cItem, chr.getWorld());
               c.announce(MaplePacketCreator.showCash(chr));
            } else if (action == 0x04) {//TODO check for gender
               int birthday = slea.readInt();
               CashItem cItem = CashItemFactory.getItem(slea.readInt());
               CharacterIdNameAccountId recipient = CharacterProcessor.getInstance().getCharacterFromDatabase(slea.readMapleAsciiString());
               String message = slea.readMapleAsciiString();
               if (!canBuy(chr, cItem, cs.getCash(4)) || message.length() < 1 || message.length() > 73) {
                  c.enableCSActions();
                  return;
               }
               if (!checkBirthday(c, birthday)) {
                  c.announce(MaplePacketCreator.showCashShopMessage((byte) 0xC4));
                  return;
               } else if (recipient == null) {
                  c.announce(MaplePacketCreator.showCashShopMessage((byte) 0xA9));
                  return;
               } else if (recipient.getAccountId() == c.getAccID()) {
                  c.announce(MaplePacketCreator.showCashShopMessage((byte) 0xA8));
                  return;
               }
               cs.gift(recipient.getId(), chr.getName(), message, cItem.getSN());
               c.announce(MaplePacketCreator.showGiftSucceed(recipient.getName(), cItem));
               cs.gainCash(4, cItem, chr.getWorld());
               c.announce(MaplePacketCreator.showCash(chr));
               NoteProcessor.getInstance().sendNote(recipient.getName(), chr.getName(), chr.getName() + " has sent you a gift! Go check out the Cash Shop.", (byte) 0); //fame or not
               c.getChannelServer().getPlayerStorage().getCharacterByName(recipient.getName()).ifPresent(MapleCharacter::showNote);
            } else if (action == 0x05) { // Modify wish list
               cs.clearWishList();
               for (byte i = 0; i < 10; i++) {
                  int sn = slea.readInt();
                  CashItem cItem = CashItemFactory.getItem(sn);
                  if (cItem != null && cItem.isOnSale() && sn != 0) {
                     cs.addToWishList(sn);
                  }
               }
               c.announce(MaplePacketCreator.showWishList(chr, true));
            } else if (action == 0x06) { // Increase Inventory Slots
               slea.skip(1);
               int cash = slea.readInt();
               byte mode = slea.readByte();
               if (mode == 0) {
                  byte type = slea.readByte();
                  if (cs.getCash(cash) < 4000) {
                     c.enableCSActions();
                     return;
                  }
                  if (chr.gainSlots(type, 4, false)) {
                     c.announce(MaplePacketCreator.showBoughtInventorySlots(type, chr.getSlots(type)));
                     cs.gainCash(cash, -4000);
                     c.announce(MaplePacketCreator.showCash(chr));
                  }
               } else {
                  CashItem cItem = CashItemFactory.getItem(slea.readInt());
                  int type = (cItem.getItemId() - 9110000) / 1000;
                  if (!canBuy(chr, cItem, cs.getCash(cash))) {
                     c.enableCSActions();
                     return;
                  }
                  if (chr.gainSlots(type, 8, false)) {
                     c.announce(MaplePacketCreator.showBoughtInventorySlots(type, chr.getSlots(type)));
                     cs.gainCash(cash, cItem, chr.getWorld());
                     c.announce(MaplePacketCreator.showCash(chr));
                  }
               }
            } else if (action == 0x07) { // Increase Storage Slots
               slea.skip(1);
               int cash = slea.readInt();
               byte mode = slea.readByte();
               if (mode == 0) {
                  if (cs.getCash(cash) < 4000) {
                     c.enableCSActions();
                     return;
                  }
                  if (chr.getStorage().gainSlots(4)) {
                     FilePrinter.print(FilePrinter.STORAGE + c.getAccountName() + ".txt", c.getPlayer().getName() + " bought 4 slots to their account storage.");
                     chr.setUsedStorage();

                     c.announce(MaplePacketCreator.showBoughtStorageSlots(chr.getStorage().getSlots()));
                     cs.gainCash(cash, -4000);
                     c.announce(MaplePacketCreator.showCash(chr));
                  }
               } else {
                  CashItem cItem = CashItemFactory.getItem(slea.readInt());

                  if (!canBuy(chr, cItem, cs.getCash(cash))) {
                     c.enableCSActions();
                     return;
                  }
                  if (chr.getStorage().gainSlots(8)) {    // thanks ABaldParrot & Thora for detecting storage issues here
                     FilePrinter.print(FilePrinter.STORAGE + c.getAccountName() + ".txt", c.getPlayer().getName() + " bought 8 slots to their account storage.");
                     chr.setUsedStorage();

                     c.announce(MaplePacketCreator.showBoughtStorageSlots(chr.getStorage().getSlots()));
                     cs.gainCash(cash, cItem, chr.getWorld());
                     c.announce(MaplePacketCreator.showCash(chr));
                  }
               }
            } else if (action == 0x08) { // Increase Character Slots
               slea.skip(1);
               int cash = slea.readInt();
               CashItem cItem = CashItemFactory.getItem(slea.readInt());

               if (!canBuy(chr, cItem, cs.getCash(cash))) {
                  c.enableCSActions();
                  return;
               }

               if (c.gainCharacterSlot()) {
                  c.announce(MaplePacketCreator.showBoughtCharacterSlot(c.getCharacterSlots()));
                  cs.gainCash(cash, cItem, chr.getWorld());
                  c.announce(MaplePacketCreator.showCash(chr));
               } else {
                  MessageBroadcaster.getInstance().sendServerNotice(chr, ServerNoticeType.POP_UP, "You have already used up all 12 extra character slots.");
                  c.enableCSActions();
                  return;
               }
            } else if (action == 0x0D) { // Take from Cash Inventory
               Item item = cs.findByCashId(slea.readInt());
               if (item == null) {
                  c.enableCSActions();
                  return;
               }
               if (chr.getInventory(item.getInventoryType()).addItem(item) != -1) {
                  cs.removeFromInventory(item);
                  c.announce(MaplePacketCreator.takeFromCashInventory(item));

                  if (item instanceof Equip) {
                     Equip equip = (Equip) item;
                     if (equip.getRingId() >= 0) {
                        MapleRing ring = MapleRingProcessor.getInstance().loadFromDb(equip.getRingId());
                        chr.addPlayerRing(ring);
                     }
                  }
               }
            } else if (action == 0x0E) { // Put into Cash Inventory
               int cashId = slea.readInt();
               slea.skip(4);

               byte invType = slea.readByte();
               if (invType < 1 || invType > 5) {
                  c.disconnect(false, false);
                  return;
               }

               MapleInventory mi = chr.getInventory(MapleInventoryType.getByType(invType));
               Item item = mi.findByCashId(cashId);
               if (item == null) {
                  c.enableCSActions();
                  return;
               } else if (c.getPlayer().getPetIndex(item.getPetId()) > -1) {
                  MessageBroadcaster.getInstance().sendServerNotice(chr, ServerNoticeType.POP_UP, "You cannot put the pet you currently equip into the Cash Shop inventory.");
                  c.enableCSActions();
                  return;
               } else if (ItemConstants.isWeddingRing(item.getItemId()) || ItemConstants.isWeddingToken(item.getItemId())) {
                  MessageBroadcaster.getInstance().sendServerNotice(chr, ServerNoticeType.POP_UP, "You cannot put relationship items into the Cash Shop inventory.");
                  c.enableCSActions();
                  return;
               }
               cs.addToInventory(item);
               mi.removeSlot(item.getPosition());
               c.announce(MaplePacketCreator.putIntoCashInventory(item, c.getAccID()));
            } else if (action == 0x1D) { //crush ring (action 28)
               crushRingOperation(slea, c, chr, cs);
            } else if (action == 0x20) {
               int serialNumber = slea.readInt();  // thanks GabrielSin for detecting a potential exploit with 1 meso cash items.
               if (serialNumber / 10000000 != 8) {
                  c.announce(MaplePacketCreator.showCashShopMessage((byte) 0xC0));
                  return;
               }

               CashItem item = CashItemFactory.getItem(serialNumber);
               if (item == null || !item.isOnSale()) {
                  c.announce(MaplePacketCreator.showCashShopMessage((byte) 0xC0));
                  return;
               }

               int itemId = item.getItemId();
               int itemPrice = item.getPrice();
               if (itemPrice <= 0) {
                  c.announce(MaplePacketCreator.showCashShopMessage((byte) 0xC0));
                  return;
               }

               if (chr.getMeso() >= itemPrice) {
                  if (chr.canHold(itemId)) {
                     chr.gainMeso(-itemPrice, false);
                     MapleInventoryManipulator.addById(c, itemId, (short) 1, "", -1);
                     c.announce(MaplePacketCreator.showBoughtQuestItem(itemId));
                  }
               }
               c.announce(MaplePacketCreator.showCash(c.getPlayer()));
            } else if (action == 0x23) { //Friendship :3
               friendshipRingOperation(slea, c, chr, cs);
            } else if (action == 0x2E) { //name change
               handleNameChange(slea, c, chr, cs);
            } else if (action == 0x31) { //world transfer
               handleWorldTransfer(slea, c, chr, cs);
            } else {
               System.out.println("Unhandled action: " + action + "\n" + slea);
            }
         } finally {
            c.releaseClient();
         }
      } else {
         c.announce(MaplePacketCreator.enableActions());
      }
   }

   private void handleWorldTransfer(SeekableLittleEndianAccessor slea, MapleClient c, MapleCharacter chr, CashShop cs) {
      CashItem cItem = CashItemFactory.getItem(slea.readInt());
      if (cItem == null || !canBuy(chr, cItem, cs.getCash(4))) {
         c.announce(MaplePacketCreator.showCashShopMessage((byte) 0));
         c.enableCSActions();
         return;
      }
      if (cItem.getSN() == 50600001 && ServerConstants.ALLOW_CASHSHOP_WORLD_TRANSFER) {
         int newWorldSelection = slea.readInt();

         int worldTransferError = chr.checkWorldTransferEligibility();
         if (worldTransferError != 0 || newWorldSelection >= Server.getInstance().getWorldsSize() || Server.getInstance().getWorldsSize() <= 1) {
            c.announce(MaplePacketCreator.showCashShopMessage((byte) 0));
            return;
         } else if (newWorldSelection == c.getWorld()) {
            c.announce(MaplePacketCreator.showCashShopMessage((byte) 0xDC));
            return;
         } else if (c.getAvailableCharacterWorldSlots(newWorldSelection) < 1 || Server.getInstance().getAccountWorldCharacterCount(c.getAccID(), newWorldSelection) >= 3) {
            c.announce(MaplePacketCreator.showCashShopMessage((byte) 0xDF));
            return;
         } else if (chr.registerWorldTransfer(newWorldSelection)) {
            Item item = cItem.toItem();
            c.announce(MaplePacketCreator.showWorldTransferSuccess(item, c.getAccID()));
            cs.addToInventory(item);
            cs.gainCash(4, cItem, chr.getWorld());
         } else {
            c.announce(MaplePacketCreator.showCashShopMessage((byte) 0));
         }
      }
      c.enableCSActions();
   }

   private void handleNameChange(SeekableLittleEndianAccessor slea, MapleClient c, MapleCharacter chr, CashShop cs) {
      CashItem cItem = CashItemFactory.getItem(slea.readInt());
      if (cItem == null || !canBuy(chr, cItem, cs.getCash(4))) {
         c.announce(MaplePacketCreator.showCashShopMessage((byte) 0));
         c.enableCSActions();
         return;
      }
      if (cItem.getSN() == 50600000 && ServerConstants.ALLOW_CASHSHOP_NAME_CHANGE) {
         slea.readMapleAsciiString(); //old name
         String newName = slea.readMapleAsciiString();
         if (!CharacterProcessor.getInstance().canCreateChar(newName) || chr.getLevel() < 10) { //(longest ban duration isn't tracked currently)
            c.announce(MaplePacketCreator.showCashShopMessage((byte) 0));
            c.enableCSActions();
            return;
         } else if (c.getTempBanCalendar() != null && c.getTempBanCalendar().getTimeInMillis() + (30 * 24 * 60 * 60 * 1000) > Calendar.getInstance().getTimeInMillis()) {
            c.announce(MaplePacketCreator.showCashShopMessage((byte) 0));
            c.enableCSActions();
            return;
         }
         if (chr.registerNameChange(newName)) { //success
            Item item = cItem.toItem();
            c.announce(MaplePacketCreator.showNameChangeSuccess(item, c.getAccID()));
            cs.addToInventory(item);
            cs.gainCash(4, cItem, chr.getWorld());
         } else {
            c.announce(MaplePacketCreator.showCashShopMessage((byte) 0));
         }
      }
      c.enableCSActions();
   }

   private void friendshipRingOperation(SeekableLittleEndianAccessor slea, MapleClient c, MapleCharacter chr, CashShop cs) {
      int birthday = slea.readInt();
      if (checkBirthday(c, birthday)) {
         int payment = slea.readByte();
         slea.skip(3); //0s
         int snID = slea.readInt();
         CashItem itemRing = CashItemFactory.getItem(snID);
         String sentTo = slea.readMapleAsciiString();
         int available = slea.readShort() - 1;
         String text = slea.readAsciiString(available);
         slea.readByte();
         c.getChannelServer().getPlayerStorage().getCharacterByName(sentTo).ifPresentOrElse(partner -> {
            // Need to check to make sure its actually an equip and the right SN...
            if (itemRing.toItem() instanceof Equip) {
               Equip eqp = (Equip) itemRing.toItem();
               Pair<Integer, Integer> rings = MapleRingProcessor.getInstance().createRing(itemRing.getItemId(), chr, partner);
               eqp.setRingId(rings.getLeft());
               cs.addToInventory(eqp);
               c.announce(MaplePacketCreator.showBoughtCashRing(eqp, partner.getName(), c.getAccID()));
               cs.gift(partner.getId(), chr.getName(), text, eqp.getSN(), rings.getRight());
               cs.gainCash(payment, -itemRing.getPrice());
               chr.addFriendshipRing(MapleRingProcessor.getInstance().loadFromDb(rings.getLeft()));
               NoteProcessor.getInstance().sendNote(partner.getName(), chr.getName(), text, (byte) 1);
               partner.showNote();
            }
         }, () -> c.announce(MaplePacketCreator.showCashShopMessage((byte) 0xBE)));
      } else {
         c.announce(MaplePacketCreator.showCashShopMessage((byte) 0xC4));
      }

      c.announce(MaplePacketCreator.showCash(c.getPlayer()));
   }

   private void crushRingOperation(SeekableLittleEndianAccessor slea, MapleClient c, MapleCharacter chr, CashShop cs) {
      int birthday = slea.readInt();
      if (checkBirthday(c, birthday)) {
         int toCharge = slea.readInt();
         int SN = slea.readInt();
         String recipientName = slea.readMapleAsciiString();
         String text = slea.readMapleAsciiString();
         CashItem itemRing = CashItemFactory.getItem(SN);
         c.getChannelServer().getPlayerStorage().getCharacterByName(recipientName).ifPresentOrElse(partner -> {
            if (itemRing.toItem() instanceof Equip) {
               Equip eqp = (Equip) itemRing.toItem();
               Pair<Integer, Integer> rings = MapleRingProcessor.getInstance().createRing(itemRing.getItemId(), chr, partner);
               eqp.setRingId(rings.getLeft());
               cs.addToInventory(eqp);
               c.announce(MaplePacketCreator.showBoughtCashItem(eqp, c.getAccID()));
               cs.gift(partner.getId(), chr.getName(), text, eqp.getSN(), rings.getRight());
               cs.gainCash(toCharge, itemRing, chr.getWorld());
               chr.addCrushRing(MapleRingProcessor.getInstance().loadFromDb(rings.getLeft()));
               NoteProcessor.getInstance().sendNote(partner.getName(), chr.getName(), text, (byte) 1);
               partner.showNote();
            }
         }, () -> MessageBroadcaster.getInstance().sendServerNotice(chr, ServerNoticeType.POP_UP, "The partner you specified cannot be found.\r\nPlease make sure your partner is online and in the same channel."));
      } else {
         c.announce(MaplePacketCreator.showCashShopMessage((byte) 0xC4));
      }
      c.announce(MaplePacketCreator.showCash(c.getPlayer()));
   }
}