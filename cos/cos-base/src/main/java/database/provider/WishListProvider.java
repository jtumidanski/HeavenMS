package database.provider;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import database.AbstractQueryExecutor;

public class WishListProvider extends AbstractQueryExecutor {
   private static WishListProvider instance;

   public static WishListProvider getInstance() {
      if (instance == null) {
         instance = new WishListProvider();
      }
      return instance;
   }

   private WishListProvider() {
   }

   public List<Integer> getWishListSn(EntityManager entityManager, int characterId) {
      TypedQuery<Integer> query = entityManager.createQuery("SELECT w.sn FROM Wishlist w WHERE w.characterId = :characterId", Integer.class);
      query.setParameter("characterId", characterId);
      return query.getResultList();
   }
}