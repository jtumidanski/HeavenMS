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

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;

import client.MapleCharacter;
import client.MapleClient;
import client.Ring;
import client.database.data.CharacterIdNameAccountId;
import client.inventory.Equip;
import client.inventory.Item;
import client.inventory.MapleInventory;
import client.inventory.MapleInventoryType;
import client.inventory.manipulator.MapleInventoryManipulator;
import client.processor.CashShopProcessor;
import client.processor.CharacterProcessor;
import client.processor.MapleRingProcessor;
import client.processor.NoteProcessor;
import config.YamlConfig;
import constants.inventory.ItemConstants;
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
import tools.MessageBroadcaster;
import tools.PacketCreator;
import tools.Pair;
import tools.ServerNoticeType;
import tools.packet.cashshop.CashShopMessage;
import tools.packet.cashshop.ShowCash;
import tools.packet.cashshop.operation.PutIntoCashInventory;
import tools.packet.cashshop.operation.ShowBoughtCashItem;
import tools.packet.cashshop.operation.ShowBoughtCashPackageSuccess;
import tools.packet.cashshop.operation.ShowBoughtCashRing;
import tools.packet.cashshop.operation.ShowBoughtCharacterSlots;
import tools.packet.cashshop.operation.ShowBoughtInventorySlots;
import tools.packet.cashshop.operation.ShowBoughtQuestItem;
import tools.packet.cashshop.operation.ShowBoughtStorageSlots;
import tools.packet.cashshop.operation.ShowCashShopMessage;
import tools.packet.cashshop.operation.ShowGiftSucceed;
import tools.packet.cashshop.operation.ShowNameChangeSuccess;
import tools.packet.cashshop.operation.ShowWishListUpdate;
import tools.packet.cashshop.operation.ShowWorldTransferSuccess;
import tools.packet.cashshop.operation.TakeFromCashInventory;
import tools.packet.stat.EnableActions;

public final class CashOperationHandler extends AbstractPacketHandler<BaseCashOperationPacket> {

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
         PacketCreator.announce(client, new EnableActions());
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
         PacketCreator.announce(client, new EnableActions());
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
         PacketCreator.announce(c, new ShowCashShopMessage(CashShopMessage.CHECK_BIRTHDAY_CODE));
         return;
      } else if (recipient == null) {
         PacketCreator.announce(c, new ShowCashShopMessage(CashShopMessage.INCORRECT_CHARACTER_NAME));
         return;
      } else if (recipient.accountId() == c.getAccID()) {
         PacketCreator.announce(c, new ShowCashShopMessage(CashShopMessage.CANNOT_GIFT_TO_OWN_CHARACTER));
         return;
      }

      CashShopProcessor.getInstance().gift(recipient.id(), chr.getName(), message, cItem.getSN());
      PacketCreator.announce(c, new ShowGiftSucceed(recipient.name(), cItem.getItemId(), cItem.getCount(), cItem.getPrice()));
      cs.gainCash(4, cItem, chr.getWorld());
      PacketCreator.announce(c, new ShowCash(chr.getCashShop().getCash(1), chr.getCashShop().getCash(2), chr.getCashShop().getCash(4)));
      NoteProcessor.getInstance().sendNote(recipient.name(), chr.getName(), chr.getName() + " has sent you a gift! Go check out the Cash Shop.", (byte) 0); //fame or not
      c.getChannelServer().getPlayerStorage().getCharacterByName(recipient.name()).ifPresent(MapleCharacter::showNote);
   }

   private void modifyWishList(MapleClient c, MapleCharacter chr, CashShop cs, int[] sns) {
      cs.clearWishList();

      List<Integer> items = Arrays.stream(sns).limit(10)
            .mapToObj(CashItemFactory::getItem)
            .filter(cashItem -> cashItem != null && cashItem.isOnSale() && cashItem.getSN() != 0)
            .map(CashItem::getSN)
            .collect(Collectors.toList());

      CashShopProcessor.getInstance().setWishListItems(chr.getId(), items);
      items.forEach(cs::addToWishList);

      PacketCreator.announce(c, new ShowWishListUpdate(chr.getCashShop().getWishList(), true));
   }

   private void purchaseMesoCashItem(MapleClient c, MapleCharacter chr, int serialNumber) {
      // thanks GabrielSin for detecting a potential exploit with 1 meso cash items.
      if (serialNumber / 10000000 != 8) {
         PacketCreator.announce(c, new ShowCashShopMessage(CashShopMessage.NOT_ENOUGH_ITEMS_IN_STOCK));
         return;
      }

      CashItem item = CashItemFactory.getItem(serialNumber);
      if (item == null || !item.isOnSale()) {
         PacketCreator.announce(c, new ShowCashShopMessage(CashShopMessage.NOT_ENOUGH_ITEMS_IN_STOCK));
         return;
      }

      int itemId = item.getItemId();
      int itemPrice = item.getPrice();
      if (itemPrice <= 0) {
         PacketCreator.announce(c, new ShowCashShopMessage(CashShopMessage.NOT_ENOUGH_ITEMS_IN_STOCK));
         return;
      }

      if (chr.getMeso() >= itemPrice) {
         if (chr.canHold(itemId)) {
            chr.gainMeso(-itemPrice, false);
            MapleInventoryManipulator.addById(c, itemId, (short) 1, "", -1);
            PacketCreator.announce(c, new ShowBoughtQuestItem(itemId));
         }
      }
      PacketCreator.announce(c, new ShowCash(chr.getCashShop().getCash(1), c.getPlayer().getCashShop().getCash(2), chr.getCashShop().getCash(4)));
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
         } else if (ItemConstants.isRateCoupon(cItem.getItemId()) && !YamlConfig.config.server.USE_SUPPLY_RATE_COUPONS) {
            MessageBroadcaster.getInstance().sendServerNotice(chr, ServerNoticeType.POP_UP, "Rate coupons are currently unavailable to purchase.");
            c.enableCSActions();
            return;
         } else if (ItemConstants.isMapleLife(cItem.getItemId()) && chr.getLevel() < 30) {
            c.enableCSActions();
            return;
         }

         Item item = cItem.toItem();
         cs.addToInventory(item);
         PacketCreator.announce(c, new ShowBoughtCashItem(item, c.getAccID()));
      } else { // Package
         List<Item> cashPackage = CashItemFactory.getPackage(cItem.getItemId());
         for (Item item : cashPackage) {
            cs.addToInventory(item);
         }
         PacketCreator.announce(c, new ShowBoughtCashPackageSuccess(cashPackage, c.getAccID()));
      }
      cs.gainCash(useNX, cItem, chr.getWorld());
      PacketCreator.announce(c, new ShowCash(chr.getCashShop().getCash(1), chr.getCashShop().getCash(2), chr.getCashShop().getCash(4)));
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
      } else if (c.getPlayer().getPetIndex(item.petId()) > -1) {
         MessageBroadcaster.getInstance().sendServerNotice(chr, ServerNoticeType.POP_UP, "You cannot put the pet you currently equip into the Cash Shop inventory.");
         c.enableCSActions();
         return;
      } else if (ItemConstants.isWeddingRing(item.id()) || ItemConstants.isWeddingToken(item.id())) {
         MessageBroadcaster.getInstance().sendServerNotice(chr, ServerNoticeType.POP_UP, "You cannot put relationship items into the Cash Shop inventory.");
         c.enableCSActions();
         return;
      }
      cs.addToInventory(item);
      mi.removeSlot(item.position());
      PacketCreator.announce(c, new PutIntoCashInventory(item, c.getAccID()));
   }

   private void takeCashFromInventory(MapleClient c, MapleCharacter chr, CashShop cs, int itemId) {
      Item item = cs.findByCashId(itemId);
      if (item == null) {
         c.enableCSActions();
         return;
      }
      if (chr.getInventory(item.inventoryType()).addItem(item) != -1) {
         cs.removeFromInventory(item);
         PacketCreator.announce(c, new TakeFromCashInventory(item));

         if (item instanceof Equip) {
            Equip equip = (Equip) item;
            if (equip.ringId() >= 0) {
               Ring ring = MapleRingProcessor.getInstance().loadFromDb(equip.ringId());
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
         PacketCreator.announce(c, new ShowBoughtCharacterSlots(c.getCharacterSlots()));
         cs.gainCash(cash, cItem, chr.getWorld());
         PacketCreator.announce(c, new ShowCash(chr.getCashShop().getCash(1), chr.getCashShop().getCash(2), chr.getCashShop().getCash(4)));
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

         PacketCreator.announce(c, new ShowBoughtStorageSlots(chr.getStorage().getSlots()));
         cs.gainCash(cash, -4000);
         PacketCreator.announce(c, new ShowCash(chr.getCashShop().getCash(1), chr.getCashShop().getCash(2), chr.getCashShop().getCash(4)));
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

         PacketCreator.announce(c, new ShowBoughtStorageSlots(chr.getStorage().getSlots()));
         cs.gainCash(cash, cItem, chr.getWorld());
         PacketCreator.announce(c, new ShowCash(chr.getCashShop().getCash(1), chr.getCashShop().getCash(2), chr.getCashShop().getCash(4)));
      }
   }

   private void increaseInventorySlotsSmall(MapleClient c, MapleCharacter chr, CashShop cs, int cash, byte type) {
      if (cs.getCash(cash) < 4000) {
         c.enableCSActions();
         return;
      }
      if (chr.gainSlots(type, 4, false)) {
         PacketCreator.announce(c, new ShowBoughtInventorySlots(type, chr.getSlots(type)));
         cs.gainCash(cash, -4000);
         PacketCreator.announce(c, new ShowCash(chr.getCashShop().getCash(1), chr.getCashShop().getCash(2), chr.getCashShop().getCash(4)));
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
         PacketCreator.announce(c, new ShowBoughtInventorySlots(type, chr.getSlots(type)));
         cs.gainCash(cash, cItem, chr.getWorld());
         PacketCreator.announce(c, new ShowCash(chr.getCashShop().getCash(1), chr.getCashShop().getCash(2), chr.getCashShop().getCash(4)));
      }
   }

   private void handleWorldTransfer(MapleClient c, MapleCharacter chr, CashShop cs, int itemId, int newWorldId) {
      CashItem cItem = CashItemFactory.getItem(itemId);
      if (cItem == null || !canBuy(chr, cItem, cs.getCash(4))) {
         PacketCreator.announce(c, new ShowCashShopMessage(CashShopMessage.UNKNOWN_ERROR));
         c.enableCSActions();
         return;
      }
      if (cItem.getSN() == 50600001 && YamlConfig.config.server.ALLOW_CASHSHOP_WORLD_TRANSFER) {

         int worldTransferError = chr.checkWorldTransferEligibility();
         if (worldTransferError != 0 || newWorldId >= Server.getInstance().getWorldsSize() || Server.getInstance().getWorldsSize() <= 1) {
            PacketCreator.announce(c, new ShowCashShopMessage(CashShopMessage.UNKNOWN_ERROR));
            return;
         } else if (newWorldId == c.getWorld()) {
            PacketCreator.announce(c, new ShowCashShopMessage(CashShopMessage.CANNOT_TRANSFER_TO_SAME_WORLD));
            return;
         } else if (c.getAvailableCharacterWorldSlots(newWorldId) < 1 || Server.getInstance().getAccountWorldCharacterCount(c.getAccID(), newWorldId) >= 3) {
            PacketCreator.announce(c, new ShowCashShopMessage(CashShopMessage.CANNOT_TRANSFER_NO_EMPTY_SLOTS));
            return;
         } else if (chr.registerWorldTransfer(newWorldId)) {
            Item item = cItem.toItem();
            PacketCreator.announce(c, new ShowWorldTransferSuccess(item, c.getAccID()));
            cs.addToInventory(item);
            cs.gainCash(4, cItem, chr.getWorld());
         } else {
            PacketCreator.announce(c, new ShowCashShopMessage(CashShopMessage.UNKNOWN_ERROR));
         }
      }
      c.enableCSActions();
   }

   private void handleNameChange(MapleClient c, MapleCharacter chr, CashShop cs, int itemId, String newName) {
      CashItem cItem = CashItemFactory.getItem(itemId);
      if (cItem == null || !canBuy(chr, cItem, cs.getCash(4))) {
         PacketCreator.announce(c, new ShowCashShopMessage(CashShopMessage.UNKNOWN_ERROR));
         c.enableCSActions();
         return;
      }
      if (cItem.getSN() == 50600000 && YamlConfig.config.server.ALLOW_CASHSHOP_NAME_CHANGE) {
         if (!CharacterProcessor.getInstance().canCreateChar(newName) || chr.getLevel() < 10) { //(longest ban duration isn't tracked currently)
            PacketCreator.announce(c, new ShowCashShopMessage(CashShopMessage.UNKNOWN_ERROR));
            c.enableCSActions();
            return;
         } else if (c.getTempBanCalendar() != null && c.getTempBanCalendar().getTimeInMillis() + (30 * 24 * 60 * 60 * 1000) > Calendar.getInstance().getTimeInMillis()) {
            PacketCreator.announce(c, new ShowCashShopMessage(CashShopMessage.UNKNOWN_ERROR));
            c.enableCSActions();
            return;
         }
         if (chr.registerNameChange(newName)) { //success
            Item item = cItem.toItem();
            PacketCreator.announce(c, new ShowNameChangeSuccess(item, c.getAccID()));
            cs.addToInventory(item);
            cs.gainCash(4, cItem, chr.getWorld());
         } else {
            PacketCreator.announce(c, new ShowCashShopMessage(CashShopMessage.UNKNOWN_ERROR));
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
               eqp.ringId_$eq(rings.getLeft());
               cs.addToInventory(eqp);
               PacketCreator.announce(c, new ShowBoughtCashRing(eqp, partner.getName(), c.getAccID()));
               CashShopProcessor.getInstance().gift(partner.getId(), chr.getName(), text, eqp.sn(), rings.getRight());
               cs.gainCash(payment, -itemRing.getPrice());
               chr.addFriendshipRing(MapleRingProcessor.getInstance().loadFromDb(rings.getLeft()));
               NoteProcessor.getInstance().sendNote(partner.getName(), chr.getName(), text, (byte) 1);
               partner.showNote();
            }
         }, () -> PacketCreator.announce(c, new ShowCashShopMessage(CashShopMessage.INVALID_RECEIVER_NAME)));
      } else {
         PacketCreator.announce(c, new ShowCashShopMessage(CashShopMessage.CHECK_BIRTHDAY_CODE));
      }

      PacketCreator.announce(c, new ShowCash(chr.getCashShop().getCash(1), chr.getCashShop().getCash(2), chr.getCashShop().getCash(4)));
   }

   private void crushRingOperation(MapleClient c, MapleCharacter chr, CashShop cs, int birthday, int toCharge, int sn, String recipientName, String text) {
      if (checkBirthday(c, birthday)) {
         CashItem itemRing = CashItemFactory.getItem(sn);
         c.getChannelServer().getPlayerStorage().getCharacterByName(recipientName).ifPresentOrElse(partner -> {
            if (itemRing.toItem() instanceof Equip) {
               Equip eqp = (Equip) itemRing.toItem();
               Pair<Integer, Integer> rings = MapleRingProcessor.getInstance().createRing(itemRing.getItemId(), chr, partner);
               eqp.ringId_$eq(rings.getLeft());
               cs.addToInventory(eqp);
               PacketCreator.announce(c, new ShowBoughtCashItem(eqp, c.getAccID()));
               CashShopProcessor.getInstance().gift(partner.getId(), chr.getName(), text, eqp.sn(), rings.getRight());
               cs.gainCash(toCharge, itemRing, chr.getWorld());
               chr.addCrushRing(MapleRingProcessor.getInstance().loadFromDb(rings.getLeft()));
               NoteProcessor.getInstance().sendNote(partner.getName(), chr.getName(), text, (byte) 1);
               partner.showNote();
            }
         }, () -> MessageBroadcaster.getInstance().sendServerNotice(chr, ServerNoticeType.POP_UP, "The partner you specified cannot be found.\r\nPlease make sure your partner is online and in the same channel."));
      } else {
         PacketCreator.announce(c, new ShowCashShopMessage(CashShopMessage.CHECK_BIRTHDAY_CODE));
      }
      PacketCreator.announce(c, new ShowCash(chr.getCashShop().getCash(1), chr.getCashShop().getCash(2), chr.getCashShop().getCash(4)));
   }
}
