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
import net.server.AbstractPacketHandler;
import net.server.Server;
import net.server.channel.packet.cash.operation.BaseCashOperationPacket;
import net.server.channel.packet.cash.operation.CrushRingPacket;
import net.server.channel.packet.cash.operation.FriendshipRingPacket;
import net.server.channel.packet.cash.operation.IncreaseCharacterSlotsPacket;
import net.server.channel.packet.cash.operation.IncreaseInventorySlotsLarge;
import net.server.channel.packet.cash.operation.IncreaseInventorySlotsSmall;
import net.server.channel.packet.cash.operation.IncreaseStorageSlotsLarge;
import net.server.channel.packet.cash.operation.IncreaseStorageSlotsSmall;
import net.server.channel.packet.cash.operation.MakePurchasePacket;
import net.server.channel.packet.cash.operation.MesoCashItemPurchase;
import net.server.channel.packet.cash.operation.ModifyWishListPacket;
import net.server.channel.packet.cash.operation.NameChangePacket;
import net.server.channel.packet.cash.operation.PutIntoCashInventoryPacket;
import net.server.channel.packet.cash.operation.SendGiftPacket;
import net.server.channel.packet.cash.operation.TakeCashFromInventoryPacket;
import net.server.channel.packet.cash.operation.WorldTransferPacket;
import net.server.channel.packet.reader.CashOperationReader;
import server.CashShop;
import server.CashShop.CashItem;
import server.CashShop.CashItemFactory;
import server.MapleItemInformationProvider;
import tools.FilePrinter;
import tools.MaplePacketCreator;
import tools.MessageBroadcaster;
import tools.Pair;
import tools.ServerNoticeType;

public final class CashOperationHandler extends AbstractPacketHandler<BaseCashOperationPacket, CashOperationReader> {

   public static boolean checkBirthday(MapleClient c, int idate) {
      int year = idate / 10000;
      int month = (idate - year * 10000) / 100;
      int day = idate - year * 10000 - month * 100;
      Calendar cal = Calendar.getInstance();
      cal.setTimeInMillis(0);
      cal.set(year, month - 1, day);
      return c.checkBirthDate(cal);
   }

   private boolean canBuy(MapleCharacter chr, CashItem item, int cash) {
      if (item != null && item.isOnSale() && item.getPrice() <= cash) {
         FilePrinter.print(FilePrinter.CASHITEM_BOUGHT, chr + " bought " + MapleItemInformationProvider.getInstance().getName(item.getItemId()) + " (SN " + item.getSN() + ") for " + item.getPrice());
         return true;
      } else {
         return false;
      }
   }

   @Override
   public Class<CashOperationReader> getReaderClass() {
      return CashOperationReader.class;
   }

   @Override
   public void handlePacket(BaseCashOperationPacket packet, MapleClient client) {
      MapleCharacter character = client.getPlayer();
      CashShop cashShop = character.getCashShop();

      if (!cashShop.isOpened()) {
         client.announce(MaplePacketCreator.enableActions());
         return;
      }

      if (client.tryAcquireClient()) {
         // thanks Thora for finding out an exploit within cash operations
         try {
            final int action = packet.action();
            if (packet instanceof MakePurchasePacket) {
               makePurchase(client, character, cashShop, action, ((MakePurchasePacket) packet).useNX(),
                     ((MakePurchasePacket) packet).snCS());
            } else if (packet instanceof SendGiftPacket) {
               sendGift(client, character, cashShop, ((SendGiftPacket) packet).birthday(),
                     ((SendGiftPacket) packet).sn(), ((SendGiftPacket) packet).characterName(),
                     ((SendGiftPacket) packet).message());
            } else if (packet instanceof ModifyWishListPacket) {
               modifyWishList(client, character, cashShop, ((ModifyWishListPacket) packet).sns());
            } else if (packet instanceof IncreaseInventorySlotsSmall) {
               increaseInventorySlotsSmall(client, character, cashShop, ((IncreaseInventorySlotsSmall) packet).cash(),
                     ((IncreaseInventorySlotsSmall) packet).theType());
            } else if (packet instanceof IncreaseInventorySlotsLarge) {
               increaseInventorySlotsLarge(client, character, cashShop, ((IncreaseInventorySlotsLarge) packet).cash(),
                     ((IncreaseInventorySlotsLarge) packet).itemId());
            } else if (packet instanceof IncreaseStorageSlotsSmall) {
               increaseStorageSlotsSmall(client, character, cashShop, ((IncreaseStorageSlotsSmall) packet).cash());
            } else if (packet instanceof IncreaseStorageSlotsLarge) {
               increaseStorageSlotsLarge(client, character, cashShop, ((IncreaseStorageSlotsLarge) packet).cash(),
                     ((IncreaseStorageSlotsLarge) packet).itemId());
            } else if (packet instanceof IncreaseCharacterSlotsPacket) {
               increaseCharacterSlots(client, character, cashShop, ((IncreaseCharacterSlotsPacket) packet).cash(),
                     ((IncreaseCharacterSlotsPacket) packet).itemId());
            } else if (packet instanceof TakeCashFromInventoryPacket) {
               takeCashFromInventory(client, character, cashShop, ((TakeCashFromInventoryPacket) packet).itemId());
            } else if (packet instanceof PutIntoCashInventoryPacket) {
               putIntoCashInventory(client, character, cashShop, ((PutIntoCashInventoryPacket) packet).cashId(),
                     ((PutIntoCashInventoryPacket) packet).invType());
            } else if (packet instanceof CrushRingPacket) {
               crushRingOperation(client, character, cashShop, ((CrushRingPacket) packet).birthday(),
                     ((CrushRingPacket) packet).toCharge(), ((CrushRingPacket) packet).sn(),
                     ((CrushRingPacket) packet).recipientName(), ((CrushRingPacket) packet).text());
            } else if (packet instanceof MesoCashItemPurchase) {
               purchaseMesoCashItem(client, character, ((MesoCashItemPurchase) packet).sn());
            } else if (packet instanceof FriendshipRingPacket) {
               friendshipRingOperation(client, character, cashShop, ((FriendshipRingPacket) packet).birthday(),
                     ((FriendshipRingPacket) packet).payment(), ((FriendshipRingPacket) packet).sn(),
                     ((FriendshipRingPacket) packet).sentTo(), ((FriendshipRingPacket) packet).text());
            } else if (packet instanceof NameChangePacket) {
               handleNameChange(client, character, cashShop, ((NameChangePacket) packet).itemId(),
                     ((NameChangePacket) packet).newName());
            } else if (packet instanceof WorldTransferPacket) {
               handleWorldTransfer(client, character, cashShop, ((WorldTransferPacket) packet).itemId(),
                     ((WorldTransferPacket) packet).newWorldId());
            } else {
               System.out.println("Unhandled action: " + action + "\n" + packet.toString());
            }
         } finally {
            client.releaseClient();
         }
      } else {
         client.announce(MaplePacketCreator.enableActions());
      }
   }

   private void sendGift(MapleClient c, MapleCharacter chr, CashShop cs, int birthday, int sn, String characterName, String message) {
      //TODO check for gender
      CashItem cItem = CashItemFactory.getItem(sn);
      CharacterIdNameAccountId recipient = CharacterProcessor.getInstance().getCharacterFromDatabase(characterName);
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
   }

   private void modifyWishList(MapleClient c, MapleCharacter chr, CashShop cs, int[] sns) {
      cs.clearWishList();
      for (byte i = 0; i < 10; i++) {
         int sn = sns[i];
         CashItem cItem = CashItemFactory.getItem(sn);
         if (cItem != null && cItem.isOnSale() && sn != 0) {
            cs.addToWishList(sn);
         }
      }
      c.announce(MaplePacketCreator.showWishList(chr, true));
   }

   private void purchaseMesoCashItem(MapleClient c, MapleCharacter chr, int serialNumber) {
      // thanks GabrielSin for detecting a potential exploit with 1 meso cash items.
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
   }

   private void makePurchase(MapleClient c, MapleCharacter chr, CashShop cs, int action, int useNX, int snCS) {
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
   }

   private void putIntoCashInventory(MapleClient c, MapleCharacter chr, CashShop cs, int cashId, byte invType) {
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
   }

   private void takeCashFromInventory(MapleClient c, MapleCharacter chr, CashShop cs, int itemId) {
      Item item = cs.findByCashId(itemId);
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
   }

   private void increaseCharacterSlots(MapleClient c, MapleCharacter chr, CashShop cs, int cash, int itemId) {
      CashItem cItem = CashItemFactory.getItem(itemId);

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
      }
   }

   private void increaseStorageSlotsSmall(MapleClient c, MapleCharacter chr, CashShop cs, int cash) {
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
   }

   private void increaseStorageSlotsLarge(MapleClient c, MapleCharacter chr, CashShop cs, int cash, int itemId) {
      CashItem cItem = CashItemFactory.getItem(itemId);

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

   private void increaseInventorySlotsSmall(MapleClient c, MapleCharacter chr, CashShop cs, int cash, byte type) {
      if (cs.getCash(cash) < 4000) {
         c.enableCSActions();
         return;
      }
      if (chr.gainSlots(type, 4, false)) {
         c.announce(MaplePacketCreator.showBoughtInventorySlots(type, chr.getSlots(type)));
         cs.gainCash(cash, -4000);
         c.announce(MaplePacketCreator.showCash(chr));
      }
   }

   private void increaseInventorySlotsLarge(MapleClient c, MapleCharacter chr, CashShop cs, int cash, int itemId) {
      CashItem cItem = CashItemFactory.getItem(itemId);
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

   private void handleWorldTransfer(MapleClient c, MapleCharacter chr, CashShop cs, int itemId, int newWorldId) {
      CashItem cItem = CashItemFactory.getItem(itemId);
      if (cItem == null || !canBuy(chr, cItem, cs.getCash(4))) {
         c.announce(MaplePacketCreator.showCashShopMessage((byte) 0));
         c.enableCSActions();
         return;
      }
      if (cItem.getSN() == 50600001 && ServerConstants.ALLOW_CASHSHOP_WORLD_TRANSFER) {

         int worldTransferError = chr.checkWorldTransferEligibility();
         if (worldTransferError != 0 || newWorldId >= Server.getInstance().getWorldsSize() || Server.getInstance().getWorldsSize() <= 1) {
            c.announce(MaplePacketCreator.showCashShopMessage((byte) 0));
            return;
         } else if (newWorldId == c.getWorld()) {
            c.announce(MaplePacketCreator.showCashShopMessage((byte) 0xDC));
            return;
         } else if (c.getAvailableCharacterWorldSlots(newWorldId) < 1 || Server.getInstance().getAccountWorldCharacterCount(c.getAccID(), newWorldId) >= 3) {
            c.announce(MaplePacketCreator.showCashShopMessage((byte) 0xDF));
            return;
         } else if (chr.registerWorldTransfer(newWorldId)) {
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

   private void handleNameChange(MapleClient c, MapleCharacter chr, CashShop cs, int itemId, String newName) {
      CashItem cItem = CashItemFactory.getItem(itemId);
      if (cItem == null || !canBuy(chr, cItem, cs.getCash(4))) {
         c.announce(MaplePacketCreator.showCashShopMessage((byte) 0));
         c.enableCSActions();
         return;
      }
      if (cItem.getSN() == 50600000 && ServerConstants.ALLOW_CASHSHOP_NAME_CHANGE) {
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

   private void friendshipRingOperation(MapleClient c, MapleCharacter chr, CashShop cs, int birthday, int payment, int sn, String sentTo, String text) {
      if (checkBirthday(c, birthday)) {
         CashItem itemRing = CashItemFactory.getItem(sn);
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

   private void crushRingOperation(MapleClient c, MapleCharacter chr, CashShop cs, int birthday, int toCharge, int sn, String recipientName, String text) {
      if (checkBirthday(c, birthday)) {
         CashItem itemRing = CashItemFactory.getItem(sn);
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
