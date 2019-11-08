package client.database.administrator;

import java.sql.Timestamp;
import java.util.Calendar;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import client.database.AbstractQueryExecutor;
import entity.HwidAccount;

public class HwidAccountAdministrator extends AbstractQueryExecutor {
   private static HwidAccountAdministrator instance;

   public static HwidAccountAdministrator getInstance() {
      if (instance == null) {
         instance = new HwidAccountAdministrator();
      }
      return instance;
   }

   private HwidAccountAdministrator() {
   }

   public void updateByAccountId(EntityManager entityManager, int accountId, String hwid, int relevance, Timestamp timestamp) {
      Query query = entityManager.createQuery("UPDATE HwidAccount SET relevance = :relevance, expiresAt = :timestamp WHERE accountId = :accountId AND hwid LIKE :hwid");
      query.setParameter("relevance", relevance);
      query.setParameter("timestamp", timestamp);
      query.setParameter("accountId", accountId);
      query.setParameter("hwid", hwid);
      execute(entityManager, query);
   }

   public void create(EntityManager entityManager, int accountId, String hwid, Timestamp timestamp) {
      HwidAccount hwidAccount = new HwidAccount();
      hwidAccount.setAccountId(accountId);
      hwidAccount.setHwid(hwid);
      hwidAccount.setExpiresAt(timestamp);
      insert(entityManager, hwidAccount);
   }

   public void deleteExpired(EntityManager entityManager) {
      Query query = entityManager.createQuery("DELETE FROM HwidAccount WHERE expiresAt < :timestamp");
      query.setParameter("timestamp", new Timestamp(Calendar.getInstance().getTimeInMillis()));
      execute(entityManager, query);
   }
}