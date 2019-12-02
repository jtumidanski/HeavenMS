package database.administrator;


import javax.persistence.EntityManager;
import javax.persistence.Query;

import database.AbstractQueryExecutor;
import entity.Marriage;

public class MarriageAdministrator extends AbstractQueryExecutor {
   private static MarriageAdministrator instance;

   public static MarriageAdministrator getInstance() {
      if (instance == null) {
         instance = new MarriageAdministrator();
      }
      return instance;
   }

   private MarriageAdministrator() {
   }

   public void endMarriage(EntityManager entityManager, int playerId) {
      Query query = entityManager.createQuery("DELETE FROM Marriage WHERE marriageId = :marriageId");
      query.setParameter("marriageId", playerId);
      execute(entityManager, query);
   }

   public int createMarriage(EntityManager entityManager, int spouse1, int spouse2) {
      Marriage marriage = new Marriage();
      marriage.setHusbandId(spouse1);
      marriage.setWifeId(spouse2);
      insert(entityManager, marriage);
      return marriage.getMarriageId();
   }
}