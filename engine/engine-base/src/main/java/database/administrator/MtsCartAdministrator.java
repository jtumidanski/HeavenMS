package database.administrator;


import javax.persistence.EntityManager;
import javax.persistence.Query;

import accessor.AbstractQueryExecutor;
import database.DeleteForCharacter;
import entity.mts.MtsCart;

public class MtsCartAdministrator extends AbstractQueryExecutor implements DeleteForCharacter {
   private static MtsCartAdministrator instance;

   public static MtsCartAdministrator getInstance() {
      if (instance == null) {
         instance = new MtsCartAdministrator();
      }
      return instance;
   }

   private MtsCartAdministrator() {
   }

   @Override
   public void deleteForCharacter(EntityManager entityManager, int characterId) {
      Query query = entityManager.createQuery("DELETE FROM MtsCart WHERE characterId = :characterId");
      query.setParameter("characterId", characterId);
      execute(entityManager, query);
   }

   public void removeItemFromCarts(EntityManager entityManager, int itemId) {
      Query query = entityManager.createQuery("DELETE FROM MtsCart WHERE itemId = :itemId");
      query.setParameter("itemId", itemId);
      execute(entityManager, query);
   }

   public void removeItemFromCart(EntityManager entityManager, int itemId, int characterId) {
      Query query = entityManager.createQuery("DELETE FROM MtsCart WHERE itemId = :itemId AND characterId = :characterId");
      query.setParameter("itemId", itemId);
      query.setParameter("characterId", characterId);
      execute(entityManager, query);
   }

   public void addToCart(EntityManager entityManager, int characterId, int itemId) {
      MtsCart mtsCart = new MtsCart();
      mtsCart.setCharacterId(characterId);
      mtsCart.setItemId(itemId);
      insert(entityManager, mtsCart);
   }
}
