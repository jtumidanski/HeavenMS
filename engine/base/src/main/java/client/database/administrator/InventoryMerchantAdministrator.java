package client.database.administrator;


import javax.persistence.EntityManager;
import javax.persistence.Query;

import client.database.AbstractQueryExecutor;
import entity.InventoryMerchant;

public class InventoryMerchantAdministrator extends AbstractQueryExecutor {
   private static InventoryMerchantAdministrator instance;

   public static InventoryMerchantAdministrator getInstance() {
      if (instance == null) {
         instance = new InventoryMerchantAdministrator();
      }
      return instance;
   }

   private InventoryMerchantAdministrator() {
   }

   public void deleteForCharacter(EntityManager entityManager, int characterId) {
      Query query = entityManager.createQuery("DELETE FROM InventoryMerchant WHERE characterId = :characterId");
      query.setParameter("characterId", characterId);
      execute(entityManager, query);
   }

   public void create(EntityManager entityManager, int itemId, int characterId, int bundle) {
      InventoryMerchant inventoryMerchant = new InventoryMerchant();
      inventoryMerchant.setInventoryItemId(itemId);
      inventoryMerchant.setCharacterId(characterId);
      inventoryMerchant.setBundles((short) bundle);
      insert(entityManager, inventoryMerchant);
   }
}