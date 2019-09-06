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
package server.maps;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import client.MapleCharacter;
import client.MapleClient;
import client.database.administrator.CharacterAdministrator;
import client.database.provider.CharacterProvider;
import client.inventory.Item;
import client.inventory.ItemFactory;
import client.inventory.MapleInventory;
import client.inventory.MapleInventoryType;
import client.inventory.manipulator.MapleInventoryManipulator;
import client.inventory.manipulator.MapleKarmaManipulator;
import client.processor.FredrickProcessor;
import constants.ServerConstants;
import net.server.Server;
import net.server.audit.locks.MonitoredLockType;
import net.server.audit.locks.factory.MonitoredReentrantLockFactory;
import server.MapleItemInformationProvider;
import server.MapleTrade;
import server.processor.maps.MapleHiredMerchantProcessor;
import tools.DatabaseConnection;
import tools.MaplePacketCreator;
import tools.MessageBroadcaster;
import tools.Pair;
import tools.ServerNoticeType;

/**
 * @author XoticStory
 * @author Ronan - concurrency protection
 */
public class MapleHiredMerchant extends AbstractMapleMapObject {
   private final List<MaplePlayerShopItem> items = new LinkedList<>();
   private int ownerId, itemId, mesos = 0;
   private int channel, world;
   private long start;
   private String ownerName;
   private String description;
   private MapleCharacter[] visitors = new MapleCharacter[3];
   private List<Pair<String, Byte>> messages = new LinkedList<>();
   private List<MapleSoldItem> sold = new LinkedList<>();
   private AtomicBoolean open = new AtomicBoolean();
   private boolean published = false;
   private MapleMap map;
   private Lock visitorLock = MonitoredReentrantLockFactory.createLock(MonitoredLockType.VISITOR_MERCH, true);

   public MapleHiredMerchant(final MapleCharacter owner, String desc, int itemId) {
      this.setPosition(owner.getPosition());
      this.start = System.currentTimeMillis();
      this.ownerId = owner.getId();
      this.channel = owner.getClient().getChannel();
      this.world = owner.getWorld();
      this.itemId = itemId;
      this.ownerName = owner.getName();
      this.description = desc;
      this.map = owner.getMap();
   }

   public void broadcastToVisitorsThreadsafe(final byte[] packet) {
      visitorLock.lock();
      try {
         broadcastToVisitors(packet);
      } finally {
         visitorLock.unlock();
      }
   }

   private void broadcastToVisitors(final byte[] packet) {
      Arrays.stream(visitors).filter(Objects::nonNull).forEach(visitor -> visitor.getClient().announce(packet));
   }

   public byte[] getShopRoomInfo() {
      visitorLock.lock();
      try {
         byte count;
         if (this.isOpen()) {
            count = (byte) Arrays.stream(visitors).filter(Objects::nonNull).count();
         } else {
            count = (byte) (visitors.length + 1);
         }

         return new byte[]{count, (byte) (visitors.length + 1)};
      } finally {
         visitorLock.unlock();
      }
   }

   public boolean addVisitor(MapleCharacter visitor) {
      visitorLock.lock();
      try {
         int i = this.getFreeSlot();
         if (i > -1) {
            visitors[i] = visitor;
            broadcastToVisitors(MaplePacketCreator.hiredMerchantVisitorAdd(visitor, i + 1));
            this.getMap().broadcastMessage(MaplePacketCreator.updateHiredMerchantBox(this));

            return true;
         }

         return false;
      } finally {
         visitorLock.unlock();
      }
   }

   public void removeVisitor(MapleCharacter visitor) {
      visitorLock.lock();
      try {
         int slot = getVisitorSlot(visitor);
         if (slot < 0) { //Not found
            return;
         }
         if (visitors[slot] != null && visitors[slot].getId() == visitor.getId()) {
            visitors[slot] = null;
            broadcastToVisitors(MaplePacketCreator.hiredMerchantVisitorLeave(slot + 1));
            this.getMap().broadcastMessage(MaplePacketCreator.updateHiredMerchantBox(this));
         }
      } finally {
         visitorLock.unlock();
      }
   }

   public int getVisitorSlotThreadsafe(MapleCharacter visitor) {
      visitorLock.lock();
      try {
         return getVisitorSlot(visitor);
      } finally {
         visitorLock.unlock();
      }
   }

   private int getVisitorSlot(MapleCharacter visitor) {
      //Actually 0 because of the +1's.
      return IntStream.range(0, visitors.length)
            .filter(i -> visitors[i] != null && visitors[i].getId() == visitor.getId())
            .findFirst()
            .orElse(-1);
   }

   private void removeAllVisitors() {
      visitorLock.lock();
      try {
         for (int i = 0; i < 3; i++) {
            MapleCharacter visitor = visitors[i];

            if (visitor != null) {
               visitor.setHiredMerchant(null);

               visitor.getClient().announce(MaplePacketCreator.leaveHiredMerchant(i + 1, 0x11));
               visitor.getClient().announce(MaplePacketCreator.hiredMerchantMaintenanceMessage());

               visitors[i] = null;
            }
         }

         this.getMap().broadcastMessage(MaplePacketCreator.updateHiredMerchantBox(this));
      } finally {
         visitorLock.unlock();
      }
   }

   private void removeOwner(MapleCharacter owner) {
      if (owner.getHiredMerchant() == this) {
         owner.announce(MaplePacketCreator.hiredMerchantOwnerLeave());
         owner.announce(MaplePacketCreator.leaveHiredMerchant(0x00, 0x03));
         owner.setHiredMerchant(null);
      }
   }

   public void withdrawMesos(MapleCharacter chr) {
      if (isOwner(chr)) {
         synchronized (items) {
            chr.withdrawMerchantMesos();
         }
      }
   }

   public void takeItemBack(int slot, MapleCharacter chr) {
      synchronized (items) {
         MaplePlayerShopItem shopItem = items.get(slot);
         if (shopItem.isExist()) {
            if (shopItem.getBundles() > 0) {
               Item iitem = shopItem.getItem().copy();
               iitem.setQuantity((short) (shopItem.getItem().getQuantity() * shopItem.getBundles()));

               if (!MapleInventory.checkSpot(chr, iitem)) {
                  MessageBroadcaster.getInstance().sendServerNotice(chr, ServerNoticeType.POP_UP, "Have a slot available on your inventory to claim back the item.");
                  chr.announce(MaplePacketCreator.enableActions());
                  return;
               }

               MapleInventoryManipulator.addFromDrop(chr.getClient(), iitem, true);
            }

            removeFromSlot(slot);
            chr.announce(MaplePacketCreator.updateHiredMerchant(this, chr));
         }

         if (ServerConstants.USE_ENFORCE_MERCHANT_SAVE) {
            chr.saveCharToDB(false);
         }
      }
   }

   private int getQuantityLeft(int itemid) {
      synchronized (items) {
         return items.stream()
               .filter(shopItem -> shopItem.getItem().getItemId() == itemid)
               .mapToInt(shopItem -> (shopItem.getBundles() * shopItem.getItem().getQuantity()))
               .sum();
      }
   }

   public void buy(MapleClient c, int item, short quantity) {
      synchronized (items) {
         MaplePlayerShopItem pItem = items.get(item);
         Item newItem = pItem.getItem().copy();

         newItem.setQuantity((short) ((pItem.getItem().getQuantity() * quantity)));
         if (quantity < 1 || !pItem.isExist() || pItem.getBundles() < quantity) {
            c.announce(MaplePacketCreator.enableActions());
            return;
         } else if (newItem.getInventoryType().equals(MapleInventoryType.EQUIP) && newItem.getQuantity() > 1) {
            c.announce(MaplePacketCreator.enableActions());
            return;
         }

         MapleKarmaManipulator.toggleKarmaFlagToUntradeable(newItem);

         int price = (int) Math.min((float) pItem.getPrice() * quantity, Integer.MAX_VALUE);
         if (c.getPlayer().getMeso() >= price) {
            if (MapleHiredMerchantProcessor.getInstance().canBuy(c, newItem)) {
               c.getPlayer().gainMeso(-price, false);
               price -= MapleTrade.getFee(price);  // thanks BHB for pointing out trade fees not applying here

               synchronized (sold) {
                  sold.add(new MapleSoldItem(c.getPlayer().getName(), pItem.getItem().getItemId(), newItem.getQuantity(), price));
               }

               pItem.setBundles((short) (pItem.getBundles() - quantity));
               if (pItem.getBundles() < 1) {
                  pItem.setDoesExist(false);
               }

               if (ServerConstants.USE_ANNOUNCE_SHOPITEMSOLD) {   // idea thanks to Vcoc
                  announceItemSold(newItem, price, getQuantityLeft(pItem.getItem().getItemId()));
               }

               Optional<MapleCharacter> owner = Server.getInstance().getWorld(world).getPlayerStorage().getCharacterByName(ownerName);
               if (owner.isPresent()) {
                  owner.get().addMerchantMesos(price);
               } else {
                  final int priceIncrease = price;
                  DatabaseConnection.getInstance().withConnection(connection -> {
                     long merchantMesos = CharacterProvider.getInstance().getMerchantMesos(connection, ownerId);
                     merchantMesos += priceIncrease;
                     CharacterAdministrator.getInstance().setMerchantMesos(connection, (int) Math.min(merchantMesos, Integer.MAX_VALUE), ownerId);
                  });
               }
            } else {
               MessageBroadcaster.getInstance().sendServerNotice(c.getPlayer(), ServerNoticeType.POP_UP, "Your inventory is full. Please clear a slot before buying this item.");
               c.announce(MaplePacketCreator.enableActions());
               return;
            }
         } else {
            MessageBroadcaster.getInstance().sendServerNotice(c.getPlayer(), ServerNoticeType.POP_UP, "You don't have enough mesos to purchase this item.");
            c.announce(MaplePacketCreator.enableActions());
            return;
         }
         try {
            this.saveItems(false);
         } catch (Exception e) {
            e.printStackTrace();
         }
      }
   }

   private void announceItemSold(Item item, int mesos, int inStore) {
      String qtyStr = (item.getQuantity() > 1) ? " x " + item.getQuantity() : "";
      String itemName = MapleItemInformationProvider.getInstance().getName(item.getItemId());
      Server.getInstance().getWorld(world).getPlayerStorage().getCharacterById(ownerId)
            .filter(MapleCharacter::isLoggedinWorld)
            .ifPresent(character ->  MessageBroadcaster.getInstance().sendServerNotice(character, ServerNoticeType.LIGHT_BLUE, "[Hired Merchant] Item '" + itemName + "'" + qtyStr + " has been sold for " + mesos + " mesos. (" + inStore + " left)"));
   }

   public void forceClose() {
      //Server.getInstance().getChannel(world, channel).removeHiredMerchant(ownerId);
      map.broadcastMessage(MaplePacketCreator.removeHiredMerchantBox(getOwnerId()));
      map.removeMapObject(this);


      visitorLock.lock();
      try {
         setOpen(false);
         removeAllVisitors();

         Server.getInstance().getWorld(world).getPlayerStorage().getCharacterById(ownerId)
               .filter(owner -> owner.isLoggedinWorld() && MapleHiredMerchant.this == owner.getHiredMerchant())
               .ifPresent(this::closeOwnerMerchant);
      } finally {
         visitorLock.unlock();
      }

      Server.getInstance().getWorld(world).unregisterHiredMerchant(this);

      saveItems(true);
      synchronized (items) {
         items.clear();
      }

      Server.getInstance().getWorld(world).getPlayerStorage().getCharacterById(ownerId).ifPresentOrElse(
            character -> character.setHasMerchant(false),
            () -> DatabaseConnection.getInstance().withConnection(connection -> CharacterAdministrator.getInstance().setMerchant(connection, ownerId, false)));

      map = null;
   }

   public void closeOwnerMerchant(MapleCharacter chr) {
      if (this.isOwner(chr)) {
         this.closeShop(chr.getClient(), false);
         chr.setHasMerchant(false);
      }
   }

   private void closeShop(MapleClient c, boolean timeout) {
      map.removeMapObject(this);
      map.broadcastMessage(MaplePacketCreator.removeHiredMerchantBox(ownerId));
      c.getChannelServer().removeHiredMerchant(ownerId);

      this.removeAllVisitors();
      this.removeOwner(c.getPlayer());

      try {
         c.getWorldServer().getPlayerStorage().getCharacterById(ownerId).ifPresentOrElse(
               character -> character.setHasMerchant(false),
               () -> DatabaseConnection.getInstance().withConnection(connection -> CharacterAdministrator.getInstance().setMerchant(connection, ownerId, false)));

         List<MaplePlayerShopItem> copyItems = getItems();
         if (MapleHiredMerchantProcessor.getInstance().check(c.getPlayer(), copyItems) && !timeout) {
            copyItems.stream().filter(MaplePlayerShopItem::isExist).forEach(shopItem -> {
               if (shopItem.getItem().getInventoryType().equals(MapleInventoryType.EQUIP)) {
                  MapleInventoryManipulator.addFromDrop(c, shopItem.getItem(), false);
               } else {
                  Item item = shopItem.getItem();
                  short quantity = (short) (shopItem.getBundles() * item.getQuantity());
                  MapleInventoryManipulator.addById(c, item.getItemId(), quantity, item.getOwner(), -1, item.getFlag(), item.getExpiration());
               }
            });

            synchronized (items) {
               items.clear();
            }
         }

         try {
            this.saveItems(timeout);
         } catch (Exception e) {
            e.printStackTrace();
         }

         if (ServerConstants.USE_ENFORCE_MERCHANT_SAVE) {
            c.getPlayer().saveCharToDB(false);
         }

         synchronized (items) {
            items.clear();
         }
      } catch (Exception e) {
         e.printStackTrace();
      }

      Server.getInstance().getWorld(world).unregisterHiredMerchant(this);
   }

   public synchronized void visitShop(MapleCharacter chr) {
      visitorLock.lock();
      try {
         if (this.isOwner(chr)) {
            this.setOpen(false);
            this.removeAllVisitors();

            chr.announce(MaplePacketCreator.getHiredMerchant(chr, this, false));
         } else if (!this.isOpen()) {
            chr.announce(MaplePacketCreator.getMiniRoomError(18));
            return;
         } else if (!this.addVisitor(chr)) {
            chr.announce(MaplePacketCreator.getMiniRoomError(2));
            return;
         } else {
            chr.announce(MaplePacketCreator.getHiredMerchant(chr, this, false));
         }
         chr.setHiredMerchant(this);
      } finally {
         visitorLock.unlock();
      }
   }

   public String getOwner() {
      return ownerName;
   }

   public void clearItems() {
      synchronized (items) {
         items.clear();
      }
   }

   public int getOwnerId() {
      return ownerId;
   }

   public String getDescription() {
      return description;
   }

   public void setDescription(String description) {
      this.description = description;
   }

   public MapleCharacter[] getVisitors() {
      visitorLock.lock();
      try {
         MapleCharacter[] copy = new MapleCharacter[3];
         System.arraycopy(visitors, 0, copy, 0, visitors.length);
         return copy;
      } finally {
         visitorLock.unlock();
      }
   }

   public List<MaplePlayerShopItem> getItems() {
      synchronized (items) {
         return Collections.unmodifiableList(items);
      }
   }

   public boolean hasItem(int itemid) {
      for (MaplePlayerShopItem mpsi : getItems()) {
         if (mpsi.getItem().getItemId() == itemid && mpsi.isExist() && mpsi.getBundles() > 0) {
            return true;
         }
      }

      return false;
   }

   public boolean addItem(MaplePlayerShopItem item) {
      synchronized (items) {
         if (items.size() >= 16) {
            return false;
         }

         items.add(item);
         return true;
      }
   }

   public void clearInexistentItems() {
      synchronized (items) {
         for (int i = items.size() - 1; i >= 0; i--) {
            if (!items.get(i).isExist()) {
               items.remove(i);
            }
         }
         this.saveItems(false);
      }
   }

   private void removeFromSlot(int slot) {
      items.remove(slot);
      this.saveItems(false);
   }

   private int getFreeSlot() {
      for (int i = 0; i < 3; i++) {
         if (visitors[i] == null) {
            return i;
         }
      }
      return -1;
   }

   public boolean isPublished() {
      return published;
   }

   public boolean isOpen() {
      return open.get();
   }

   public void setOpen(boolean set) {
      open.getAndSet(set);
      published = true;
   }

   public int getItemId() {
      return itemId;
   }

   public boolean isOwner(MapleCharacter chr) {
      return chr.getId() == ownerId;
   }

   public void sendMessage(MapleCharacter chr, String msg) {
      String message = chr.getName() + " : " + msg;
      byte slot = (byte) (getVisitorSlot(chr) + 1);

      synchronized (messages) {
         messages.add(new Pair<>(message, slot));
      }
      broadcastToVisitorsThreadsafe(MaplePacketCreator.hiredMerchantChat(message, slot));
   }

   public List<MaplePlayerShopItem> sendAvailableBundles(int itemId) {
      List<MaplePlayerShopItem> all;

      if (!open.get()) {
         return new LinkedList<>();
      }

      synchronized (items) {
         all = new ArrayList<>(items);
      }

      return all.stream()
            .filter(shopItem -> shopItem.getItem().getItemId() == itemId && shopItem.getBundles() > 0 && shopItem.isExist())
            .collect(Collectors.toList());
   }

   public void saveItems(boolean shutdown) {
      List<Pair<Item, MapleInventoryType>> itemsWithType = new ArrayList<>();
      List<Short> bundles = new ArrayList<>();

      getItems().forEach(shopItem -> {
         Item newItem = shopItem.getItem();
         short newBundle = shopItem.getBundles();

         if (shutdown) { //is "shutdown" really necessary?
            newItem.setQuantity(shopItem.getItem().getQuantity());
         } else {
            newItem.setQuantity(shopItem.getItem().getQuantity());
         }
         if (newBundle > 0) {
            itemsWithType.add(new Pair<>(newItem, newItem.getInventoryType()));
            bundles.add(newBundle);
         }
      });

      DatabaseConnection.getInstance().withConnection(connection -> ItemFactory.MERCHANT.saveItems(itemsWithType, bundles, this.ownerId, connection));
      FredrickProcessor.insertFredrickLog(this.ownerId);
   }

   public int getChannel() {
      return channel;
   }

   public int getTimeOpen() {
      double openTime = (System.currentTimeMillis() - start) / 60000;
      openTime /= 1440;   // heuristics since engineered method to count time here is unknown
      openTime *= 1318;

      return (int) Math.ceil(openTime);
   }

   public void clearMessages() {
      synchronized (messages) {
         messages.clear();
      }
   }

   public List<Pair<String, Byte>> getMessages() {
      synchronized (messages) {
         return new LinkedList<>(messages);
      }
   }

   public int getMapId() {
      return map.getId();
   }

   public MapleMap getMap() {
      return map;
   }

   public List<MapleSoldItem> getSold() {
      synchronized (sold) {
         return Collections.unmodifiableList(sold);
      }
   }

   public int getMesos() {
      return mesos;
   }

   @Override
   public MapleMapObjectType getType() {
      return MapleMapObjectType.HIRED_MERCHANT;
   }

   @Override
   public void sendDestroyData(MapleClient client) {
   }

   @Override
   public void sendSpawnData(MapleClient client) {
      client.announce(MaplePacketCreator.spawnHiredMerchantBox(this));
   }

}