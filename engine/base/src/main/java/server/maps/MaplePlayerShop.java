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
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;

import client.MapleCharacter;
import client.MapleClient;
import client.inventory.Item;
import client.inventory.MapleInventory;
import client.inventory.MapleInventoryType;
import client.inventory.manipulator.MapleInventoryManipulator;
import client.inventory.manipulator.MapleKarmaManipulator;
import net.opcodes.SendOpcode;
import net.server.audit.locks.MonitoredLockType;
import net.server.audit.locks.factory.MonitoredReentrantLockFactory;
import server.MapleTrade;
import tools.MaplePacketCreator;
import tools.MessageBroadcaster;
import tools.Pair;
import tools.ServerNoticeType;
import tools.data.output.MaplePacketLittleEndianWriter;

/**
 * @author Matze
 * @author Ronan - concurrency protection
 */
public class MaplePlayerShop extends AbstractMapleMapObject {
   private AtomicBoolean open = new AtomicBoolean(false);
   private MapleCharacter owner;
   private int itemid;

   private MapleCharacter[] visitors = new MapleCharacter[3];
   private List<MaplePlayerShopItem> items = new ArrayList<>();
   private List<SoldItem> sold = new LinkedList<>();
   private String description;
   private int boughtnumber = 0;
   private List<String> bannedList = new ArrayList<>();
   private List<Pair<MapleCharacter, String>> chatLog = new LinkedList<>();
   private Map<Integer, Byte> chatSlot = new LinkedHashMap<>();
   private Lock visitorLock = MonitoredReentrantLockFactory.createLock(MonitoredLockType.VISITOR_PSHOP, true);

   public MaplePlayerShop(MapleCharacter owner, String description, int itemid) {
      this.setPosition(owner.getPosition());
      this.owner = owner;
      this.description = description;
      this.itemid = itemid;
   }

   private static boolean canBuy(MapleClient c, Item newItem) {
      return MapleInventoryManipulator.checkSpace(c, newItem.id(), newItem.quantity(), newItem.owner()) && MapleInventoryManipulator.addFromDrop(c, newItem, false);
   }

   public static byte[] shopErrorMessage(int error, int type) {
      MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.PLAYER_INTERACTION.getValue());
      mplew.write(0x0A);
      mplew.write(type);
      mplew.write(error);
      return mplew.getPacket();
   }

   public int getChannel() {
      return owner.getClient().getChannel();
   }

   public int getMapId() {
      return owner.getMapId();
   }

   public int getItemId() {
      return itemid;
   }

   public boolean isOpen() {
      return open.get();
   }

   public void setOpen(boolean openShop) {
      open.set(openShop);
   }

   public boolean hasFreeSlot() {
      visitorLock.lock();
      try {
         return visitors[0] == null || visitors[1] == null || visitors[2] == null;
      } finally {
         visitorLock.unlock();
      }
   }

   public byte[] getShopRoomInfo() {
      visitorLock.lock();
      try {
         byte count = 0;
         //if (this.isOpen()) {
         for (MapleCharacter visitor : visitors) {
            if (visitor != null) {
               count++;
            }
         }
         //} else {  shouldn't happen since there isn't a "closed" state for player shops.
         //    count = (byte) (visitors.length + 1);
         //}

         return new byte[]{count, (byte) visitors.length};
      } finally {
         visitorLock.unlock();
      }
   }

   public boolean isOwner(MapleCharacter c) {
      return owner.equals(c);
   }

   private void addVisitor(MapleCharacter visitor) {
      for (int i = 0; i < 3; i++) {
         if (visitors[i] == null) {
            visitors[i] = visitor;
            visitor.setSlot(i);

            this.broadcast(MaplePacketCreator.getPlayerShopNewVisitor(visitor, i + 1));
            owner.getMap().broadcastMessage(MaplePacketCreator.updatePlayerShopBox(this));
            break;
         }
      }
   }

   public void forceRemoveVisitor(MapleCharacter visitor) {
      if (visitor == owner) {
         owner.getMap().removeMapObject(this);
         owner.setPlayerShop(null);
      }

      visitorLock.lock();
      try {
         for (int i = 0; i < 3; i++) {
            if (visitors[i] != null && visitors[i].getId() == visitor.getId()) {
               visitors[i].setPlayerShop(null);
               visitors[i] = null;
               visitor.setSlot(-1);

               this.broadcast(MaplePacketCreator.getPlayerShopRemoveVisitor(i + 1));
               owner.getMap().broadcastMessage(MaplePacketCreator.updatePlayerShopBox(this));
               return;
            }
         }
      } finally {
         visitorLock.unlock();
      }
   }

   public void removeVisitor(MapleCharacter visitor) {
      if (visitor == owner) {
         owner.getMap().removeMapObject(this);
         owner.setPlayerShop(null);
      } else {
         visitorLock.lock();
         try {
            for (int i = 0; i < 3; i++) {
               if (visitors[i] != null && visitors[i].getId() == visitor.getId()) {
                  visitor.setSlot(-1);    //absolutely cant remove player slot for late players without dc'ing them... heh

                  for (int j = i; j < 2; j++) {
                     if (visitors[j] != null) {
                        owner.announce(MaplePacketCreator.getPlayerShopRemoveVisitor(j + 1));
                     }
                     visitors[j] = visitors[j + 1];
                     if (visitors[j] != null) {
                        visitors[j].setSlot(j);
                     }
                  }
                  visitors[2] = null;
                  for (int j = i; j < 2; j++) {
                     if (visitors[j] != null) {
                        owner.announce(MaplePacketCreator.getPlayerShopNewVisitor(visitors[j], j + 1));
                     }
                  }

                  this.broadcastRestoreToVisitors();
                  owner.getMap().broadcastMessage(MaplePacketCreator.updatePlayerShopBox(this));
                  return;
               }
            }
         } finally {
            visitorLock.unlock();
         }

         owner.getMap().broadcastMessage(MaplePacketCreator.updatePlayerShopBox(this));
      }
   }

   public boolean isVisitor(MapleCharacter visitor) {
      visitorLock.lock();
      try {
         return visitors[0] == visitor || visitors[1] == visitor || visitors[2] == visitor;
      } finally {
         visitorLock.unlock();
      }
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

   private void removeFromSlot(int slot) {
      items.remove(slot);
   }

   public void takeItemBack(int slot, MapleCharacter chr) {
      synchronized (items) {
         MaplePlayerShopItem shopItem = items.get(slot);
         if (shopItem.doesExist()) {
            if (shopItem.bundles() > 0) {
               Item iitem = shopItem.item().copy();
               iitem.quantity_$eq((short) (shopItem.item().quantity() * shopItem.bundles()));

               if (!MapleInventory.checkSpot(chr, iitem)) {
                  MessageBroadcaster.getInstance().sendServerNotice(chr, ServerNoticeType.POP_UP, "Have a slot available on your inventory to claim back the item.");
                  chr.announce(MaplePacketCreator.enableActions());
                  return;
               }

               MapleInventoryManipulator.addFromDrop(chr.getClient(), iitem, true);
            }

            removeFromSlot(slot);
            chr.announce(MaplePacketCreator.getPlayerShopItemUpdate(this));
         }
      }
   }

   /**
    * no warnings for now o.o
    *
    * @param c
    * @param item
    * @param quantity
    */
   public boolean buy(MapleClient c, int item, short quantity) {
      synchronized (items) {
         if (isVisitor(c.getPlayer())) {
            MaplePlayerShopItem pItem = items.get(item);
            Item newItem = pItem.item().copy();

            newItem.quantity_$eq((short) ((pItem.item().quantity() * quantity)));
            if (quantity < 1 || !pItem.doesExist() || pItem.bundles() < quantity) {
               c.announce(MaplePacketCreator.enableActions());
               return false;
            } else if (newItem.inventoryType().equals(MapleInventoryType.EQUIP) && newItem.quantity() > 1) {
               c.announce(MaplePacketCreator.enableActions());
               return false;
            }

            MapleKarmaManipulator.toggleKarmaFlagToUntradeable(newItem);

            visitorLock.lock();
            try {
               int price = (int) Math.min((float) pItem.price() * quantity, Integer.MAX_VALUE);

               if (c.getPlayer().getMeso() >= price) {
                  if (canBuy(c, newItem)) {
                     if (!owner.canHoldMeso(price)) {
                        MessageBroadcaster.getInstance().sendServerNotice(owner, ServerNoticeType.POP_UP, "Transaction failed since the shop owner can't hold any more mesos.");
                        c.announce(MaplePacketCreator.enableActions());
                        return false;
                     }

                     c.getPlayer().gainMeso(-price, false);
                     price -= MapleTrade.getFee(price);  // thanks BHB for pointing out trade fees not applying here
                     owner.gainMeso(price, true);

                     SoldItem soldItem = new SoldItem(c.getPlayer().getName(), pItem.item().id(), quantity, price);
                     owner.announce(MaplePacketCreator.getPlayerShopOwnerUpdate(soldItem, item));

                     synchronized (sold) {
                        sold.add(soldItem);
                     }

                     pItem.bundles_$eq((short) (pItem.bundles() - quantity));
                     if (pItem.bundles() < 1) {
                        pItem.doesExist_$eq(false);
                        if (++boughtnumber == items.size()) {
                           owner.setPlayerShop(null);
                           this.setOpen(false);
                           this.closeShop();
                           MessageBroadcaster.getInstance().sendServerNotice(owner, ServerNoticeType.POP_UP, "Your items are sold out, and therefore your shop is closed.");
                        }
                     }
                  } else {
                     MessageBroadcaster.getInstance().sendServerNotice(c.getPlayer(), ServerNoticeType.POP_UP, "Your inventory is full. Please clear a slot before buying this item.");
                     c.announce(MaplePacketCreator.enableActions());
                     return false;
                  }
               } else {
                  MessageBroadcaster.getInstance().sendServerNotice(c.getPlayer(), ServerNoticeType.POP_UP, "You don't have enough mesos to purchase this item.");
                  c.announce(MaplePacketCreator.enableActions());
                  return false;
               }

               return true;
            } finally {
               visitorLock.unlock();
            }
         } else {
            return false;
         }
      }
   }

   public void broadcastToVisitors(final byte[] packet) {
      visitorLock.lock();
      try {
         for (int i = 0; i < 3; i++) {
            if (visitors[i] != null) {
               visitors[i].getClient().announce(packet);
            }
         }
      } finally {
         visitorLock.unlock();
      }
   }

   public void broadcastRestoreToVisitors() {
      visitorLock.lock();
      try {
         for (int i = 0; i < 3; i++) {
            if (visitors[i] != null) {
               visitors[i].getClient().announce(MaplePacketCreator.getPlayerShopRemoveVisitor(i + 1));
            }
         }

         for (int i = 0; i < 3; i++) {
            if (visitors[i] != null) {
               visitors[i].getClient().announce(MaplePacketCreator.getPlayerShop(this, false));
            }
         }

         recoverChatLog();
      } finally {
         visitorLock.unlock();
      }
   }

   public void removeVisitors() {
      List<MapleCharacter> visitorList = new ArrayList<>(3);

      visitorLock.lock();
      try {
         try {
            for (int i = 0; i < 3; i++) {
               if (visitors[i] != null) {
                  visitors[i].getClient().announce(MaplePacketCreator.shopErrorMessage(10, 1));
                  visitorList.add(visitors[i]);
               }
            }
         } catch (Exception e) {
            e.printStackTrace();
         }
      } finally {
         visitorLock.unlock();
      }

      for (MapleCharacter mc : visitorList) forceRemoveVisitor(mc);
      if (owner != null) {
         forceRemoveVisitor(owner);
      }
   }

   public void broadcast(final byte[] packet) {
      if (owner.getClient() != null && owner.getClient().getSession() != null) {
         owner.getClient().announce(packet);
      }
      broadcastToVisitors(packet);
   }

   private byte getVisitorSlot(MapleCharacter chr) {
      byte s = 0;
      for (MapleCharacter mc : getVisitors()) {
         s++;
         if (mc != null) {
            if (mc.getName().equalsIgnoreCase(chr.getName())) {
               break;
            }
         } else if (s == 3) {
            s = 0;
         }
      }

      return s;
   }

   public void chat(MapleClient c, String chat) {
      byte s = getVisitorSlot(c.getPlayer());

      synchronized (chatLog) {
         chatLog.add(new Pair<>(c.getPlayer(), chat));
         if (chatLog.size() > 25) {
            chatLog.remove(0);
         }
         chatSlot.put(c.getPlayer().getId(), s);
      }

      broadcast(MaplePacketCreator.getPlayerShopChat(c.getPlayer(), chat, s));
   }

   private void recoverChatLog() {
      synchronized (chatLog) {
         for (Pair<MapleCharacter, String> it : chatLog) {
            MapleCharacter chr = it.getLeft();
            Byte pos = chatSlot.get(chr.getId());

            broadcastToVisitors(MaplePacketCreator.getPlayerShopChat(chr, it.getRight(), pos));
         }
      }
   }

   private void clearChatLog() {
      synchronized (chatLog) {
         chatLog.clear();
      }
   }

   public void closeShop() {
      clearChatLog();
      removeVisitors();
      owner.getMap().broadcastMessage(MaplePacketCreator.removePlayerShopBox(this));
   }

   public void sendShop(MapleClient c) {
      visitorLock.lock();
      try {
         c.announce(MaplePacketCreator.getPlayerShop(this, isOwner(c.getPlayer())));
      } finally {
         visitorLock.unlock();
      }
   }

   public MapleCharacter getOwner() {
      return owner;
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
         if (mpsi.item().id() == itemid && mpsi.doesExist() && mpsi.bundles() > 0) {
            return true;
         }
      }

      return false;
   }

   public String getDescription() {
      return description;
   }

   public void setDescription(String description) {
      this.description = description;
   }

   public void banPlayer(String name) {
      if (!bannedList.contains(name)) {
         bannedList.add(name);
      }

      MapleCharacter target = null;
      visitorLock.lock();
      try {
         for (int i = 0; i < 3; i++) {
            if (visitors[i] != null && visitors[i].getName().equals(name)) {
               target = visitors[i];
               break;
            }
         }
      } finally {
         visitorLock.unlock();
      }

      if (target != null) {
         target.getClient().announce(MaplePacketCreator.shopErrorMessage(5, 1));
         removeVisitor(target);
      }
   }

   public boolean isBanned(String name) {
      return bannedList.contains(name);
   }

   public synchronized boolean visitShop(MapleCharacter chr) {
      if (this.isBanned(chr.getName())) {
         MessageBroadcaster.getInstance().sendServerNotice(chr, ServerNoticeType.POP_UP, "You have been banned from this store.");
         return false;
      }

      visitorLock.lock();
      try {
         if (!open.get()) {
            MessageBroadcaster.getInstance().sendServerNotice(chr, ServerNoticeType.POP_UP, "This store is not yet open.");
            return false;
         }

         if (this.hasFreeSlot() && !this.isVisitor(chr)) {
            this.addVisitor(chr);
            chr.setPlayerShop(this);
            this.sendShop(chr.getClient());

            return true;
         }

         return false;
      } finally {
         visitorLock.unlock();
      }
   }

   public List<MaplePlayerShopItem> sendAvailableBundles(int itemid) {
      List<MaplePlayerShopItem> list = new LinkedList<>();
      List<MaplePlayerShopItem> all;

      synchronized (items) {
         all = new ArrayList<>(items);
      }

      for (MaplePlayerShopItem mpsi : all) {
         if (mpsi.item().id() == itemid && mpsi.bundles() > 0 && mpsi.doesExist()) {
            list.add(mpsi);
         }
      }
      return list;
   }

   public List<SoldItem> getSold() {
      synchronized (sold) {
         return Collections.unmodifiableList(sold);
      }
   }

   @Override
   public void sendDestroyData(MapleClient client) {
      client.announce(MaplePacketCreator.removePlayerShopBox(this));
   }

   @Override
   public void sendSpawnData(MapleClient client) {
      client.announce(MaplePacketCreator.updatePlayerShopBox(this));
   }

   @Override
   public MapleMapObjectType getType() {
      return MapleMapObjectType.SHOP;
   }

   public class SoldItem {

      int itemid, mesos;
      short quantity;
      String buyer;

      public SoldItem(String buyer, int itemid, short quantity, int mesos) {
         this.buyer = buyer;
         this.itemid = itemid;
         this.quantity = quantity;
         this.mesos = mesos;
      }

      public String getBuyer() {
         return buyer;
      }

      public int getItemId() {
         return itemid;
      }

      public short getQuantity() {
         return quantity;
      }

      public int getMesos() {
         return mesos;
      }
   }
}