package database.administrator;


import javax.persistence.EntityManager;
import javax.persistence.Query;

import accessor.AbstractQueryExecutor;

public class ServerQueueAdministrator extends AbstractQueryExecutor {
   private static ServerQueueAdministrator instance;

   public static ServerQueueAdministrator getInstance() {
      if (instance == null) {
         instance = new ServerQueueAdministrator();
      }
      return instance;
   }

   private ServerQueueAdministrator() {
   }

   public void deleteForCharacter(EntityManager entityManager, int characterId) {
      Query query = entityManager.createQuery("DELETE FROM ServerQueue WHERE characterId = :characterId");
      query.setParameter("characterId", characterId);
      execute(entityManager, query);
   }
}