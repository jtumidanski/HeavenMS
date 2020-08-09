package database.provider;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import accessor.AbstractQueryExecutor;

public class MacFilterProvider extends AbstractQueryExecutor {
   private static MacFilterProvider instance;

   public static MacFilterProvider getInstance() {
      if (instance == null) {
         instance = new MacFilterProvider();
      }
      return instance;
   }

   private MacFilterProvider() {
   }

   public List<String> getMacFilters(EntityManager entityManager) {
      TypedQuery<String> query = entityManager.createQuery("SELECT m.filter FROM MacFilter m", String.class);
      return query.getResultList();
   }
}
