package client.inventory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;

import constants.MapleInventoryType;
import database.DatabaseConnection;
import database.administrator.InventoryEquipmentAdministrator;
import database.administrator.InventoryItemAdministrator;
import database.administrator.InventoryMerchantAdministrator;
import database.provider.InventoryItemProvider;
import database.provider.InventoryMerchantProvider;
import net.server.audit.locks.MonitoredLockType;
import net.server.audit.locks.factory.MonitoredReentrantLockFactory;
import tools.Pair;

public enum ItemFactory {

   INVENTORY(1, false),
   STORAGE(2, true),
   CASH_EXPLORER(3, true),
   CASH_CYGNUS(4, true),
   CASH_ARAN(5, true),
   MERCHANT(6, false),
   CASH_OVERALL(7, true),
   MARRIAGE_GIFTS(8, false),
   DUEY(9, false);
   private static final int lockCount = 400;
   private static final Lock[] locks = new Lock[lockCount];

   static {
      for (int i = 0; i < lockCount; i++) {
         locks[i] = MonitoredReentrantLockFactory.createLock(MonitoredLockType.ITEM, true);
      }
   }

   private final int value;
   private final boolean account;

   ItemFactory(int value, boolean account) {
      this.value = value;
      this.account = account;
   }

   public static List<Pair<Item, Integer>> loadEquippedItems(int id, boolean isAccount, boolean login) {
      if (isAccount) {
         return DatabaseConnection.getInstance().withConnectionResult(connection -> InventoryItemProvider.getInstance().getEquipsByAccount(connection, id, login)).orElse(Collections.emptyList());
      } else {
         return DatabaseConnection.getInstance().withConnectionResult(connection -> InventoryItemProvider.getInstance().getEquipsByCharacter(connection, id, login)).orElse(Collections.emptyList());
      }
   }

   public int getValue() {
      return value;
   }

   public List<Pair<Item, MapleInventoryType>> loadItems(int id, boolean login) {
      if (value != 6) {
         return loadItemsCommon(id, login);
      } else {
         return loadItemsMerchant(id, login);
      }
   }

   public void saveItems(List<Pair<Item, MapleInventoryType>> items, int id, EntityManager entityManager) {
      saveItems(items, null, id, entityManager);
   }

   public void saveItems(List<Pair<Item, MapleInventoryType>> items, List<Short> bundlesList, int id, EntityManager entityManager) {
      if (value != 6) {
         saveItemsCommon(items, id, entityManager);
      } else {
         saveItemsMerchant(items, bundlesList, id, entityManager);
      }
   }

   private List<Pair<Item, MapleInventoryType>> loadItemsCommon(int id, boolean login) {
      if (account) {
         return DatabaseConnection.getInstance().withConnectionResult(connection -> InventoryItemProvider.getInstance().getItemsByAccountAndType(connection, id, value, login)).orElse(new ArrayList<>());
      } else {
         return DatabaseConnection.getInstance().withConnectionResult(connection -> InventoryItemProvider.getInstance().getItemsByCharacterAndType(connection, id, value, login)).orElse(new ArrayList<>());
      }
   }

   private void saveItemsCommon(List<Pair<Item, MapleInventoryType>> items, int id, EntityManager entityManager) {
      Lock lock = locks[id % lockCount];
      lock.lock();
      try {
         if (account) {
            InventoryItemAdministrator.getInstance().deleteForAccountByType(entityManager, id, value);
         } else {
            InventoryItemAdministrator.getInstance().deleteForCharacterByType(entityManager, id, value);
         }


         if (!items.isEmpty()) {
            for (Pair<Item, MapleInventoryType> pair : items) {
               Item item = pair.getLeft();
               MapleInventoryType mit = pair.getRight();
               int genKey = InventoryItemAdministrator.getInstance().create(entityManager, value, account ? -1 : id, account ? id : -1,
                     item.id(), mit.getType(), item.position(), item.quantity(), item.owner(),
                     item.petId(), item.flag(), item.expiration(), item.giftFrom());

               if (mit.equals(MapleInventoryType.EQUIP) || mit.equals(MapleInventoryType.EQUIPPED)) {
                  saveEquipItem(entityManager, (Equip) item, genKey);
               }
            }
         }
      } finally {
         lock.unlock();
      }
   }

   private List<Pair<Item, MapleInventoryType>> loadItemsMerchant(int id, boolean login) {
      return DatabaseConnection.getInstance().withConnectionResult(connection -> {
         List<Pair<Item, MapleInventoryType>> results;
         if (account) {
            results = InventoryItemProvider.getInstance().getItemsByAccountAndType(connection, id, value, login);
         } else {
            results = InventoryItemProvider.getInstance().getItemsByCharacterAndType(connection, id, value, login);
         }
         return results.stream().map(original -> {
            short bundles = InventoryMerchantProvider.getInstance().getBundleForItem(connection, original.getLeft().id());
            MapleInventoryType inventoryType = original.getRight();
            if (inventoryType.equals(MapleInventoryType.EQUIP) || inventoryType.equals(MapleInventoryType.EQUIPPED)) {
               return original;
            } else {
               if (bundles > 0) {
                  Item updated = Item.newBuilder(original.getLeft()).setQuantity((short) (bundles * original.getLeft().quantity())).build();
                  return new Pair<>(updated, original.getRight());
               }
            }
            return null;
         }).collect(Collectors.toList());
      }).orElse(new ArrayList<>());
   }

   private void saveItemsMerchant(List<Pair<Item, MapleInventoryType>> items, List<Short> bundlesList, int id, EntityManager entityManager) {
      Lock lock = locks[id % lockCount];
      lock.lock();
      try {
         InventoryMerchantAdministrator.getInstance().deleteForCharacter(entityManager, id);
         if (account) {
            InventoryItemAdministrator.getInstance().deleteForAccountByType(entityManager, id, value);

         } else {
            InventoryItemAdministrator.getInstance().deleteForCharacterByType(entityManager, id, value);
         }


         if (!items.isEmpty()) {
            int i = 0;
            for (Pair<Item, MapleInventoryType> pair : items) {
               Item item = pair.getLeft();
               Short bundles = bundlesList.get(i);
               MapleInventoryType mit = pair.getRight();
               i++;

               int genKey = InventoryItemAdministrator.getInstance().create(entityManager, value, account ? -1 : id,
                     account ? id : -1, item.id(), mit.getType(), item.position(), item.quantity(),
                     item.owner(), item.petId(), item.flag(), item.expiration(), item.giftFrom());
               InventoryMerchantAdministrator.getInstance().create(entityManager, genKey, id, bundles);

               if (mit.equals(MapleInventoryType.EQUIP) || mit.equals(MapleInventoryType.EQUIPPED)) {
                  saveEquipItem(entityManager, (Equip) item, genKey);
               }
            }
         }
      } finally {
         lock.unlock();
      }
   }

   private void saveEquipItem(EntityManager entityManager, Equip item, int genKey) {
      InventoryEquipmentAdministrator.getInstance().create(entityManager, genKey, item.slots(),
            item.level(), item.str(), item.dex(), item.intelligence(), item.luk(),
            item.hp(), item.mp(), item.watk(), item.matk(), item.wdef(),
            item.mdef(), item.acc(), item.avoid(), item.hands(), item.speed(),
            item.jump(), 0, item.vicious(), item.itemLevel(), item.itemExp(),
            item.ringId());
   }
}