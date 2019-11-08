package client.database.administrator;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import client.database.AbstractQueryExecutor;
import client.database.DeleteForCharacter;
import entity.InventoryItem;
import tools.Pair;

public class InventoryItemAdministrator extends AbstractQueryExecutor implements DeleteForCharacter {
   private static InventoryItemAdministrator instance;

   public static InventoryItemAdministrator getInstance() {
      if (instance == null) {
         instance = new InventoryItemAdministrator();
      }
      return instance;
   }

   private InventoryItemAdministrator() {
   }

   @Override
   public void deleteForCharacter(EntityManager entityManager, int characterId) {
      Query query = entityManager.createQuery("DELETE FROM InventoryItem WHERE characterId = :characterId");
      query.setParameter("characterId", characterId);
      execute(entityManager, query);
   }

   public void deleteByCharacterAndTypeBatch(EntityManager entityManager, List<Pair<Integer, Integer>> data) {
      entityManager.getTransaction().begin();
      data.forEach(dataPoint -> {
         Query query = entityManager.createQuery("DELETE FROM InventoryItem WHERE type = :type AND characterId = :characterId");
         query.setParameter("type", dataPoint.getLeft());
         query.setParameter("characterId", dataPoint.getRight());
         query.executeUpdate();
      });
      entityManager.getTransaction().commit();
   }

   public void deleteForCharacterByType(EntityManager entityManager, int characterId, int type) {
      entityManager.getTransaction().begin();

      TypedQuery<Integer> query = entityManager.createQuery("SELECT i.inventoryItemId FROM InventoryItem i WHERE i.characterId = :characterId AND i.type = :type", Integer.class);
      query.setParameter("characterId", characterId);
      query.setParameter("type", type);
      List<Integer> inventoryItemIds = query.getResultList();

      Query inventoryEquipQuery = entityManager.createQuery("DELETE FROM InventoryEquipment WHERE inventoryItemId IN :ids");
      inventoryEquipQuery.setParameter("ids", inventoryItemIds);
      inventoryEquipQuery.executeUpdate();

      Query inventoryItemQuery = entityManager.createQuery("DELETE FROM InventoryItem WHERE inventoryItemId IN :ids");
      inventoryItemQuery.setParameter("ids", inventoryItemIds);
      inventoryItemQuery.executeUpdate();

      entityManager.getTransaction().commit();
   }

   public void deleteForAccountByType(EntityManager entityManager, int accountId, int type) {
      entityManager.getTransaction().begin();

      TypedQuery<Integer> query = entityManager.createQuery("SELECT i.inventoryItemId FROM InventoryItem i WHERE i.accountId = :accountId AND i.type = :type", Integer.class);
      query.setParameter("accountId", accountId);
      query.setParameter("type", type);
      List<Integer> inventoryItemIds = query.getResultList();

      Query inventoryEquipQuery = entityManager.createQuery("DELETE FROM InventoryEquipment WHERE inventoryItemId IN :ids");
      inventoryEquipQuery.setParameter("ids", inventoryItemIds);
      inventoryEquipQuery.executeUpdate();

      Query inventoryItemQuery = entityManager.createQuery("DELETE FROM InventoryItem WHERE inventoryItemId IN :ids");
      inventoryItemQuery.setParameter("ids", inventoryItemIds);
      inventoryItemQuery.executeUpdate();

      entityManager.getTransaction().commit();
   }

   public int create(EntityManager entityManager, int type, int characterId, int accountId, int itemId, int inventoryType,
                     int position, int quantity, String owner, int petId, int flag, long expiration, String giftFrom) {
      InventoryItem inventoryItem = new InventoryItem();
      inventoryItem.setType(type);
      inventoryItem.setCharacterId(characterId);
      inventoryItem.setAccountId(accountId);
      inventoryItem.setItemId(itemId);
      inventoryItem.setInventoryType(inventoryType);
      inventoryItem.setPosition(position);
      inventoryItem.setQuantity(quantity);
      inventoryItem.setOwner(owner);
      inventoryItem.setPetId(petId);
      inventoryItem.setFlag(flag);
      inventoryItem.setExpiration(expiration);
      inventoryItem.setGiftFrom(giftFrom);
      insert(entityManager, inventoryItem);
      return inventoryItem.getInventoryItemId();
   }

   public void expireItem(EntityManager entityManager, int itemId, int characterId) {
      Query query = entityManager.createQuery("UPDATE InventoryItem SET expiration = 0 WHERE itemId = :itemId AND characterId = :characterId");
      query.setParameter("itemId", itemId);
      query.setParameter("characterId", characterId);
      execute(entityManager, query);
   }
}