package client.database.provider;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import client.database.AbstractQueryExecutor;

public class MtsCartProvider extends AbstractQueryExecutor {
   private static MtsCartProvider instance;

   public static MtsCartProvider getInstance() {
      if (instance == null) {
         instance = new MtsCartProvider();
      }
      return instance;
   }

   private MtsCartProvider() {
   }

   public boolean isItemInCart(EntityManager entityManager, int characterId, int itemId) {
      Query query = entityManager.createQuery("SELECT m.id FROM MtsCart m WHERE m.characterId = :characterId AND m.itemId = :itemId");
      query.setParameter("characterId", characterId);
      query.setParameter("itemId", itemId);
      try {
         query.getSingleResult();
         return true;
      } catch (NoResultException exception) {
         return false;
      }
   }

   public long countCartSize(EntityManager entityManager, int characterId) {
      TypedQuery<Long> query = entityManager.createQuery("SELECT COUNT(*) FROM MtsCart m WHERE m.characterId = :characterId", Long.class);
      query.setParameter("characterId", characterId);
      return getSingleWithDefault(query, 0L);
   }

   public List<Integer> getCartItems(EntityManager entityManager, int characterId) {
      TypedQuery<Integer> query = entityManager.createQuery("SELECT m.itemId FROM MtsCart m WHERE m.characterId = :characterId ORDER BY m.id DESC", Integer.class);
      query.setParameter("characterId", characterId);
      return query.getResultList();
   }
}