package database.provider;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import accessor.AbstractQueryExecutor;

public class PetIgnoreProvider extends AbstractQueryExecutor {
   private static PetIgnoreProvider instance;

   public static PetIgnoreProvider getInstance() {
      if (instance == null) {
         instance = new PetIgnoreProvider();
      }
      return instance;
   }

   private PetIgnoreProvider() {
   }

   public List<Integer> getIgnoresForPet(EntityManager entityManager, int petId) {
      TypedQuery<Integer> query = entityManager.createQuery("SELECT p.itemId FROM PetIgnore p WHERE p.pet.id = :petId", Integer.class);
      query.setParameter("petId", petId);
      return query.getResultList();
   }
}