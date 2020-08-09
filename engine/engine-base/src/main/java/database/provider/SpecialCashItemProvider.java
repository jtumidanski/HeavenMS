package database.provider;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import accessor.AbstractQueryExecutor;
import client.database.utility.SpecialCashItemTransformer;
import entity.SpecialCashItem;
import server.CashShop;

public class SpecialCashItemProvider extends AbstractQueryExecutor {
   private static SpecialCashItemProvider instance;

   public static SpecialCashItemProvider getInstance() {
      if (instance == null) {
         instance = new SpecialCashItemProvider();
      }
      return instance;
   }

   private SpecialCashItemProvider() {
   }

   public List<CashShop.SpecialCashItem> getSpecialCashItems(EntityManager entityManager) {
      TypedQuery<SpecialCashItem> query = entityManager.createQuery("FROM SpecialCashItem", SpecialCashItem.class);
      return getResultList(query, new SpecialCashItemTransformer());
   }
}