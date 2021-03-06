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
import constants.MapleInventoryType;
import database.DatabaseConnection;
import database.administrator.CharacterAdministrator;
import database.provider.CharacterProvider;
import client.inventory.Item;
import client.inventory.ItemFactory;
import client.inventory.MapleInventory;
import client.inventory.manipulator.MapleInventoryManipulator;
import client.inventory.manipulator.MapleKarmaManipulator;
import client.processor.npc.FredrickProcessor;
import config.YamlConfig;
import net.server.Server;
import net.server.audit.locks.MonitoredLockType;
import net.server.audit.locks.factory.MonitoredReentrantLockFactory;
import server.MapleItemInformationProvider;
import server.MapleTradeUtil;
import server.processor.maps.MapleHiredMerchantProcessor;
import tools.MasterBroadcaster;
import tools.MessageBroadcaster;
import tools.PacketCreator;
import tools.Pair;
import tools.ServerNoticeType;
import tools.I18nMessage;
import tools.packet.MiniRoomError;
import tools.packet.character.interaction.GetHiredMerchant;
import tools.packet.character.interaction.GetMiniRoomError;
import tools.packet.character.interaction.LeaveHiredMerchant;
import tools.packet.character.interaction.MerchantChat;
import tools.packet.character.interaction.MerchantMaintenanceMessage;
import tools.packet.character.interaction.MerchantOwnerLeave;
import tools.packet.character.interaction.MerchantVisitorAdd;
import tools.packet.character.interaction.MerchantVisitorLeave;
import tools.packet.character.interaction.UpdateHiredMerchant;
import tools.packet.shop.DestroyHiredMerchantBox;
import tools.packet.shop.UpdateHiredMerchantBox;
import tools.packet.stat.EnableActions;

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
   private Lock visitorLock = MonitoredReentrantLockFactory.createLock(MonitoredLockType.VISITOR_MERCHANT, true);

   public MapleHiredMerchant(final MapleCharacter owner, String desc, int itemId) {
      this.setPosition(owner.position());
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
            broadcastToVisitors(PacketCreator.create(new MerchantVisitorAdd(visitor, i + 1)));
            MasterBroadcaster.getInstance().sendToAllInMap(getMap(), new UpdateHiredMerchantBox(getOwnerId(), this.objectId(), getDescription(), getItemId(), getShopRoomInfo()));

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
            broadcastToVisitors(PacketCreator.create(new MerchantVisitorLeave(slot + 1)));
            MasterBroadcaster.getInstance().sendToAllInMap(getMap(), new UpdateHiredMerchantBox(getOwnerId(), this.objectId(), getDescription(), getItemId(), getShopRoomInfo()));
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

               PacketCreator.announce(visitor, new LeaveHiredMerchant(i + 1, 0x11));
               PacketCreator.announce(visitor, new MerchantMaintenanceMessage());

               visitors[i] = null;
            }
         }

         MasterBroadcaster.getInstance().sendToAllInMap(getMap(), new UpdateHiredMerchantBox(getOwnerId(), this.objectId(), getDescription(), getItemId(), getShopRoomInfo()));
      } finally {
         visitorLock.unlock();
      }
   }

   private void removeOwner(MapleCharacter owner) {
      if (owner.getHiredMerchant() == this) {
         PacketCreator.announce(owner, new MerchantOwnerLeave());
         PacketCreator.announce(owner, new LeaveHiredMerchant(0x00, 0x03));
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
            PacketCreator.announce(chr, new UpdateHiredMerchant(chr, this));
         }

         if (YamlConfig.config.server.USE_ENFORCE_MERCHANT_SAVE) {
            chr.saveCharToDB(false);
         }
      }
   }

   private int getQuantityLeft(int itemId) {
      synchronized (items) {
         return items.stream()
               .filter(shopItem -> shopItem.item().id() == itemId)
               .mapToInt(shopItem -> (shopItem.bundles() * shopItem.item().quantity()))
               .sum();
      }
   }

   public void buy(MapleClient c, int item, short quantity) {
      synchronized (items) {
         MaplePlayerShopItem pItem = items.get(item);
         Item newItem = pItem.item().copy();

         newItem = newItem.setQuantity((short) ((pItem.item().quantity() * quantity)));
         if (quantity < 1 || !pItem.doesExist() || pItem.bundles() < quantity) {
            PacketCreator.announce(c, new EnableActions());
            return;
         } else if (newItem.inventoryType().equals(MapleInventoryType.EQUIP) && newItem.quantity() > 1) {
            PacketCreator.announce(c, new EnableActions());
            return;
         }

         newItem = MapleKarmaManipulator.toggleKarmaFlagToUntradeable(newItem);

         int price = (int) Math.min((float) pItem.price() * quantity, Integer.MAX_VALUE);
         if (c.getPlayer().getMeso() >= price) {
            if (MapleHiredMerchantProcessor.getInstance().canBuy(c, newItem)) {
               c.getPlayer().gainMeso(-price, false);
               price -= MapleTradeUtil.getFee(price);

               synchronized (sold) {
                  sold.add(new MapleSoldItem(c.getPlayer().getName(), pItem.item().id(), newItem.quantity(), price));
               }

               pItem.setBundles((short) (pItem.bundles() - quantity));
               if (pItem.bundles() < 1) {
                  pItem.setDoesExist(false);
               }

               if (YamlConfig.config.server.USE_ANNOUNCE_SHOPITEMSOLD) {
                  announceItemSold(newItem, price, getQuantityLeft(pItem.item().id()));
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
               MessageBroadcaster.getInstance().sendServerNotice(c.getPlayer(), ServerNoticeType.POP_UP, I18nMessage.from("HIRED_MERCHANT_PURCHASE_ITEM"));
               PacketCreator.announce(c, new EnableActions());
               return;
            }
         } else {
            MessageBroadcaster.getInstance().sendServerNotice(c.getPlayer(), ServerNoticeType.POP_UP, I18nMessage.from("HIRED_MERCHANT_PURCHASE_ITEM_ERROR_NO_MESO"));
            PacketCreator.announce(c, new EnableActions());
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
      String qtyStr = (item.quantity() > 1) ? " x " + item.quantity() : "";
      String itemName = MapleItemInformationProvider.getInstance().getName(item.id());
      Server.getInstance().getWorld(world).getPlayerStorage().getCharacterById(ownerId)
            .filter(MapleCharacter::isLoggedInWorld)
            .ifPresent(character -> MessageBroadcaster.getInstance().sendServerNotice(character, ServerNoticeType.LIGHT_BLUE, I18nMessage.from("HIRED_MERCHANT_ITEM_SOLD").with(itemName, qtyStr, mesos, inStore)));
   }

   public void forceClose() {
      //Server.getInstance().getChannel(world, channel).removeHiredMerchant(ownerId);
      MasterBroadcaster.getInstance().sendToAllInMap(map, new DestroyHiredMerchantBox(getOwnerId()));
      map.removeMapObject(this);


      visitorLock.lock();
      try {
         setOpen(false);
         removeAllVisitors();

         Server.getInstance().getWorld(world).getPlayerStorage().getCharacterById(ownerId)
               .filter(owner -> owner.isLoggedInWorld() && MapleHiredMerchant.this == owner.getHiredMerchant())
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
      MasterBroadcaster.getInstance().sendToAllInMap(map, new DestroyHiredMerchantBox(ownerId));
      c.getChannelServer().removeHiredMerchant(ownerId);

      this.removeAllVisitors();
      this.removeOwner(c.getPlayer());

      try {
         c.getWorldServer().getPlayerStorage().getCharacterById(ownerId).ifPresentOrElse(
               character -> character.setHasMerchant(false),
               () -> DatabaseConnection.getInstance().withConnection(connection -> CharacterAdministrator.getInstance().setMerchant(connection, ownerId, false)));

         List<MaplePlayerShopItem> copyItems = getItems();
         if (MapleHiredMerchantProcessor.getInstance().check(c.getPlayer(), copyItems) && !timeout) {
            copyItems.stream().filter(MaplePlayerShopItem::doesExist).forEach(shopItem -> {
               if (shopItem.item().inventoryType().equals(MapleInventoryType.EQUIP)) {
                  MapleInventoryManipulator.addFromDrop(c, shopItem.item(), false);
               } else {
                  Item item = shopItem.item();
                  short quantity = (short) (shopItem.bundles() * item.quantity());
                  MapleInventoryManipulator.addById(c, item.id(), quantity, item.owner(), -1, item.flag(), item.expiration());
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

         if (YamlConfig.config.server.USE_ENFORCE_MERCHANT_SAVE) {
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

            PacketCreator.announce(chr, new GetHiredMerchant(chr, this, false));
         } else if (!this.isOpen()) {
            PacketCreator.announce(chr, new GetMiniRoomError(MiniRoomError.UNDERGOING_MAINTENANCE));
            return;
         } else if (!this.addVisitor(chr)) {
            PacketCreator.announce(chr, new GetMiniRoomError(MiniRoomError.FULL_CAPACITY));
            return;
         } else {
            PacketCreator.announce(chr, new GetHiredMerchant(chr, this, false));
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

   public boolean hasItem(int itemId) {
      for (MaplePlayerShopItem playerShopItem : getItems()) {
         if (playerShopItem.item().id() == itemId && playerShopItem.doesExist() && playerShopItem.bundles() > 0) {
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

   public void clearNonExistentItems() {
      synchronized (items) {
         for (int i = items.size() - 1; i >= 0; i--) {
            if (!items.get(i).doesExist()) {
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
      broadcastToVisitorsThreadsafe(PacketCreator.create(new MerchantChat(message, slot)));
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
            .filter(shopItem -> shopItem.item().id() == itemId && shopItem.bundles() > 0 && shopItem.doesExist())
            .collect(Collectors.toList());
   }

   public void saveItems(boolean shutdown) {
      List<Pair<Item, MapleInventoryType>> itemsWithType = new ArrayList<>();
      List<Short> bundles = new ArrayList<>();

      getItems().forEach(shopItem -> {
         Item newItem = shopItem.item();
         short newBundle = shopItem.bundles();

         newItem = newItem.setQuantity(shopItem.item().quantity());
         if (newBundle > 0) {
            itemsWithType.add(new Pair<>(newItem, newItem.inventoryType()));
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
   public MapleMapObjectType type() {
      return MapleMapObjectType.HIRED_MERCHANT;
   }
}
