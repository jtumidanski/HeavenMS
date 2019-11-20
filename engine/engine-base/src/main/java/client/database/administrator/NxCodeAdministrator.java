package client.database.administrator;


import javax.persistence.EntityManager;
import javax.persistence.Query;

import client.database.AbstractQueryExecutor;

public class NxCodeAdministrator extends AbstractQueryExecutor {
   private static NxCodeAdministrator instance;

   public static NxCodeAdministrator getInstance() {
      if (instance == null) {
         instance = new NxCodeAdministrator();
      }
      return instance;
   }

   private NxCodeAdministrator() {
   }

   public void setRetriever(EntityManager entityManager, int codeId, String name) {
      Query query = entityManager.createQuery("UPDATE NxCode SET retriever = :retriever WHERE code = :code");
      query.setParameter("retriever", name);
      query.setParameter("code", codeId);
      execute(entityManager, query);
   }

   public void deleteExpired(EntityManager entityManager, long time) {
      Query query = entityManager.createQuery("DELETE FROM NxCode WHERE expiration <= :time");
      query.setParameter("time", time);
      execute(entityManager, query);
   }
}