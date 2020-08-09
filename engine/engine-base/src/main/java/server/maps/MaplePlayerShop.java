package server.maps;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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
import server.MapleTradeUtil;
import tools.I18nMessage;
import tools.MasterBroadcaster;
import tools.MessageBroadcaster;
import tools.PacketCreator;
import tools.Pair;
import tools.ServerNoticeType;
import tools.data.output.MaplePacketLittleEndianWriter;
import tools.packet.character.box.RemovePlayerShop;
import tools.packet.character.box.UpdatePlayerShopBox;
import tools.packet.character.interaction.GetPlayerShop;
import tools.packet.character.interaction.PlayerShopChat;
import tools.packet.character.interaction.PlayerShopErrorMessage;
import tools.packet.character.interaction.PlayerShopItemUpdate;
import tools.packet.character.interaction.PlayerShopNewVisitor;
import tools.packet.character.interaction.PlayerShopOwnerUpdate;
import tools.packet.character.interaction.PlayerShopRemoveVisitor;
import tools.packet.stat.EnableActions;

public class MaplePlayerShop extends AbstractMapleMapObject {
   private AtomicBoolean open = new AtomicBoolean(false);
   private MapleCharacter owner;
   private int itemId;

   private MapleCharacter[] visitors = new MapleCharacter[3];
   private List<MaplePlayerShopItem> items = new ArrayList<>();
   private List<MaplePlayerShopSoldItem> sold = new LinkedList<>();
   private String description;
   private int boughtNumber = 0;
   private List<String> bannedList = new ArrayList<>();
   private List<Pair<MapleCharacter, String>> chatLog = new LinkedList<>();
   private Map<Integer, Byte> chatSlot = new LinkedHashMap<>();
   private Lock visitorLock = MonitoredReentrantLockFactory.createLock(MonitoredLockType.VISITOR_PLAYER_SHOP, true);

   public MaplePlayerShop(MapleCharacter owner, String description, int itemId) {
      this.setPosition(owner.position());
      this.owner = owner;
      this.description = description;
      this.itemId = itemId;
   }

   private static boolean canBuy(MapleClient c, Item newItem) {
      boolean hasSpace = MapleInventoryManipulator.checkSpace(c, newItem.id(), newItem.quantity(), newItem.owner());
      Optional<Item> item = MapleInventoryManipulator.addFromDrop(c, newItem, false);
      return hasSpace && item.isPresent();
   }

   public static byte[] shopErrorMessage(int error, int type) {
      MaplePacketLittleEndianWriter writer = new MaplePacketLittleEndianWriter();
      writer.writeShort(SendOpcode.PLAYER_INTERACTION.getValue());
      writer.write(0x0A);
      writer.write(type);
      writer.write(error);
      return writer.getPacket();
   }

   public int getChannel() {
      return owner.getClient().getChannel();
   }

   public int getMapId() {
      return owner.getMapId();
   }

   public int getItemId() {
      return itemId;
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

            byte[] newVisitorPacket = PacketCreator.create(new PlayerShopNewVisitor(visitor, i + 1));
            MasterBroadcaster.getInstance().sendToShop(this, character -> newVisitorPacket);
            MasterBroadcaster.getInstance().sendToAllInMap(owner.getMap(), new UpdatePlayerShopBox(this));
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

               byte[] removeVisitorPacket = PacketCreator.create(new PlayerShopRemoveVisitor(i + 1));
               MasterBroadcaster.getInstance().sendToShop(this, character -> removeVisitorPacket);
               MasterBroadcaster.getInstance().sendToAllInMap(owner.getMap(), new UpdatePlayerShopBox(this));
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
                        PacketCreator.announce(owner, new PlayerShopRemoveVisitor(j + 1));
                     }
                     visitors[j] = visitors[j + 1];
                     if (visitors[j] != null) {
                        visitors[j].setSlot(j);
                     }
                  }
                  visitors[2] = null;
                  for (int j = i; j < 2; j++) {
                     if (visitors[j] != null) {
                        PacketCreator.announce(owner, new PlayerShopNewVisitor(visitors[j], j + 1));
                     }
                  }

                  broadcastRestoreToVisitors();
                  MasterBroadcaster.getInstance().sendToAllInMap(owner.getMap(), new UpdatePlayerShopBox(this));
                  return;
               }
            }
         } finally {
            visitorLock.unlock();
         }

         MasterBroadcaster.getInstance().sendToAllInMap(owner.getMap(), new UpdatePlayerShopBox(this));
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
               Item item = shopItem.item().copy();
               item = item.setQuantity((short) (shopItem.item().quantity() * shopItem.bundles()));

               if (!MapleInventory.checkSpot(chr, item)) {
                  MessageBroadcaster.getInstance().sendServerNotice(chr, ServerNoticeType.POP_UP, I18nMessage.from("HIRED_MERCHANT_TAKE_ITEM_BACK"));
                  PacketCreator.announce(chr, new EnableActions());
                  return;
               }

               MapleInventoryManipulator.addFromDrop(chr.getClient(), item, true);
            }

            removeFromSlot(slot);
            PacketCreator.announce(chr, new PlayerShopItemUpdate(this));
         }
      }
   }

   public boolean buy(MapleClient c, int item, short quantity) {
      synchronized (items) {
         if (isVisitor(c.getPlayer())) {
            MaplePlayerShopItem pItem = items.get(item);
            Item newItem = pItem.item().copy();

            newItem = newItem.setQuantity((short) ((pItem.item().quantity() * quantity)));
            if (quantity < 1 || !pItem.doesExist() || pItem.bundles() < quantity) {
               PacketCreator.announce(c, new EnableActions());
               return false;
            } else if (newItem.inventoryType().equals(MapleInventoryType.EQUIP) && newItem.quantity() > 1) {
               PacketCreator.announce(c, new EnableActions());
               return false;
            }

            newItem = MapleKarmaManipulator.toggleKarmaFlagToUntradeable(newItem);

            visitorLock.lock();
            try {
               int price = (int) Math.min((float) pItem.price() * quantity, Integer.MAX_VALUE);

               if (c.getPlayer().getMeso() >= price) {
                  if (!owner.canHoldMeso(price)) {
                     MessageBroadcaster.getInstance().sendServerNotice(owner, ServerNoticeType.POP_UP, I18nMessage.from("HIRED_MERCHANT_PURCHASE_ERROR_MAX_MESO"));
                     PacketCreator.announce(c, new EnableActions());
                     return false;
                  }

                  if (canBuy(c, newItem)) {
                     c.getPlayer().gainMeso(-price, false);
                     price -= MapleTradeUtil.getFee(price);
                     owner.gainMeso(price, true);

                     MaplePlayerShopSoldItem soldItem = new MaplePlayerShopSoldItem(c.getPlayer().getName(), pItem.item().id(), quantity, price);
                     PacketCreator.announce(owner, new PlayerShopOwnerUpdate(soldItem, item));

                     synchronized (sold) {
                        sold.add(soldItem);
                     }

                     pItem.setBundles((short) (pItem.bundles() - quantity));
                     if (pItem.bundles() < 1) {
                        pItem.setDoesExist(false);
                        if (++boughtNumber == items.size()) {
                           owner.setPlayerShop(null);
                           this.setOpen(false);
                           this.closeShop();
                           MessageBroadcaster.getInstance().sendServerNotice(owner, ServerNoticeType.POP_UP, I18nMessage.from("HIRED_MERCHANT_SHOP_SOLD_OUT"));
                        }
                     }
                  } else {
                     MessageBroadcaster.getInstance().sendServerNotice(c.getPlayer(), ServerNoticeType.POP_UP, I18nMessage.from("HIRED_MERCHANT_PURCHASE_ITEM"));
                     PacketCreator.announce(c, new EnableActions());
                     return false;
                  }
               } else {
                  MessageBroadcaster.getInstance().sendServerNotice(c.getPlayer(), ServerNoticeType.POP_UP, I18nMessage.from("HIRED_MERCHANT_PURCHASE_ITEM_ERROR_NO_MESO"));
                  PacketCreator.announce(c, new EnableActions());
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

   public void broadcastRestoreToVisitors() {
      visitorLock.lock();
      try {
         MasterBroadcaster.getInstance().sendToShoppers(this, (character, index) -> PacketCreator.create(new PlayerShopRemoveVisitor(index + 1)));
         MasterBroadcaster.getInstance().sendToShoppers(this, new GetPlayerShop(this, false));
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
                  PacketCreator.announce(visitors[i], new PlayerShopErrorMessage(10, 1));
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
      MasterBroadcaster.getInstance().sendToShop(this, new PlayerShopChat(c.getPlayer().getName(), chat, s));
   }

   private void recoverChatLog() {
      synchronized (chatLog) {
         for (Pair<MapleCharacter, String> it : chatLog) {
            MapleCharacter chr = it.getLeft();
            Byte pos = chatSlot.get(chr.getId());
            MasterBroadcaster.getInstance().sendToShoppers(this, new PlayerShopChat(chr.getName(), it.getRight(), pos));
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
      MasterBroadcaster.getInstance().sendToAllInMap(owner.getMap(), new RemovePlayerShop(this.getOwner().getId()));
   }

   public void sendShop(MapleClient c) {
      visitorLock.lock();
      try {
         PacketCreator.announce(c, new GetPlayerShop(this, isOwner(c.getPlayer())));
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

   public boolean hasItem(int itemId) {
      for (MaplePlayerShopItem playerShopItem : getItems()) {
         if (playerShopItem.item().id() == itemId && playerShopItem.doesExist() && playerShopItem.bundles() > 0) {
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
         PacketCreator.announce(target, new PlayerShopErrorMessage(5, 1));
         removeVisitor(target);
      }
   }

   public boolean isBanned(String name) {
      return bannedList.contains(name);
   }

   public synchronized boolean visitShop(MapleCharacter chr) {
      if (this.isBanned(chr.getName())) {
         MessageBroadcaster.getInstance().sendServerNotice(chr, ServerNoticeType.POP_UP, I18nMessage.from("HIRED_MERCHANT_BANNED"));
         return false;
      }

      visitorLock.lock();
      try {
         if (!open.get()) {
            MessageBroadcaster.getInstance().sendServerNotice(chr, ServerNoticeType.POP_UP, I18nMessage.from("HIRED_MERCHANT_NOT_OPEN_YET"));
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

   public List<MaplePlayerShopItem> sendAvailableBundles(int itemId) {
      List<MaplePlayerShopItem> list = new LinkedList<>();
      List<MaplePlayerShopItem> all;

      synchronized (items) {
         all = new ArrayList<>(items);
      }

      for (MaplePlayerShopItem playerShopItem : all) {
         if (playerShopItem.item().id() == itemId && playerShopItem.bundles() > 0 && playerShopItem.doesExist()) {
            list.add(playerShopItem);
         }
      }
      return list;
   }

   public List<MaplePlayerShopSoldItem> getSold() {
      synchronized (sold) {
         return Collections.unmodifiableList(sold);
      }
   }

   @Override
   public MapleMapObjectType type() {
      return MapleMapObjectType.SHOP;
   }
}