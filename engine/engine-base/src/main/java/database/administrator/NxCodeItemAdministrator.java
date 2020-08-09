package database.administrator;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import accessor.AbstractQueryExecutor;

public class NxCodeItemAdministrator extends AbstractQueryExecutor {
   private static NxCodeItemAdministrator instance;

   public static NxCodeItemAdministrator getInstance() {
      if (instance == null) {
         instance = new NxCodeItemAdministrator();
      }
      return instance;
   }

   private NxCodeItemAdministrator() {
   }

   public void deleteItems(EntityManager entityManager, List<Integer> itemIds) {
      if (itemIds.size() == 0) {
         return;
      }

      Query query = entityManager.createQuery("DELETE FROM NxCodeItem WHERE codeId IN :codeIds");
      query.setParameter("codeIds", itemIds);
      execute(entityManager, query);
   }
}