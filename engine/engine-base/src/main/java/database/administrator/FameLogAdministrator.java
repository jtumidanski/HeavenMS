package database.administrator;


import javax.persistence.EntityManager;
import javax.persistence.Query;

import database.AbstractQueryExecutor;
import database.DeleteForCharacter;
import entity.FameLog;

public class FameLogAdministrator extends AbstractQueryExecutor implements DeleteForCharacter {
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

   @Override
   public void deleteForCharacter(EntityManager entityManager, int characterId) {
      Query query = entityManager.createQuery("DELETE FROM FameLog WHERE characterIdTo = :characterId");
      query.setParameter("characterId", characterId);
      execute(entityManager, query);
   }
}
