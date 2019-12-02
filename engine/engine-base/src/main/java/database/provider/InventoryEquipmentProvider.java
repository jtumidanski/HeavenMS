package database.provider;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import database.AbstractQueryExecutor;

public class InventoryEquipmentProvider extends AbstractQueryExecutor {
   private static InventoryEquipmentProvider instance;

   public static InventoryEquipmentProvider getInstance() {
      if (instance == null) {
         instance = new InventoryEquipmentProvider();
      }
      return instance;
   }

   private InventoryEquipmentProvider() {
   }

   public List<Integer> getRings(EntityManager entityManager, int inventoryItemId) {
      TypedQuery<Integer> query = entityManager.createQuery("SELECT i.ringId FROM InventoryEquipment i WHERE i.inventoryItemId = :inventoryItemId", Integer.class);
      query.setParameter("inventoryItemId", inventoryItemId);
      return query.getResultList();
   }
}