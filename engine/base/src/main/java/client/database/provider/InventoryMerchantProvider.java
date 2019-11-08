package client.database.provider;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import client.database.AbstractQueryExecutor;

public class InventoryMerchantProvider extends AbstractQueryExecutor {
   private static InventoryMerchantProvider instance;

   public static InventoryMerchantProvider getInstance() {
      if (instance == null) {
         instance = new InventoryMerchantProvider();
      }
      return instance;
   }

   private InventoryMerchantProvider() {
   }

   public Short getBundleForItem(EntityManager entityManager, int inventoryItemId) {
      TypedQuery<Short> query = entityManager.createQuery("SELECT i.bundles FROM InventoryMerchant i WHERE i.inventoryItemId = :inventoryItemId", Short.class);
      query.setParameter("inventoryItemId", inventoryItemId);
      return getSingleWithDefault(query, (short) 0);
   }
}