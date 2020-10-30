package database.administrator;


import accessor.AbstractQueryExecutor;
import entity.FameLog;

import javax.persistence.EntityManager;
import javax.persistence.Query;

public class FameLogAdministrator extends AbstractQueryExecutor {
   private static FameLogAdministrator instance;

   public static FameLogAdministrator getInstance() {
      if (instance == null) {
         instance = new FameLogAdministrator();
      }
      return instance;
   }

   private FameLogAdministrator() {
   }

   public void addForCharacter(EntityManager entityManager, int fromId, int toId) {
      FameLog fameLog = new FameLog();
      fameLog.setCharacterId(fromId);
      fameLog.setCharacterIdTo(toId);
      insert(entityManager, fameLog);
   }

   public void deleteForCharacter(EntityManager entityManager, int characterId) {
      Query query = entityManager.createQuery("DELETE FROM FameLog WHERE characterIdTo = :characterId");
      query.setParameter("characterId", characterId);
      execute(entityManager, query);
   }
}
