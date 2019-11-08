package client.database.administrator;


import javax.persistence.EntityManager;
import javax.persistence.Query;

import client.database.AbstractQueryExecutor;
import entity.IpBan;

public class IpBanAdministrator extends AbstractQueryExecutor {
   private static IpBanAdministrator instance;

   public static IpBanAdministrator getInstance() {
      if (instance == null) {
         instance = new IpBanAdministrator();
      }
      return instance;
   }

   private IpBanAdministrator() {
   }

   public void banIp(EntityManager entityManager, String ip) {
      IpBan ipBan = new IpBan();
      ipBan.setIp(ip);
      insert(entityManager, ip);
   }

   public void banIp(EntityManager entityManager, String ip, int accountId) {
      IpBan ipBan = new IpBan();
      ipBan.setIp(ip);
      ipBan.setAid(accountId);
      insert(entityManager, ipBan);
   }

   public void removeIpBan(EntityManager entityManager, int accountId) {
      Query query = entityManager.createQuery("DELETE FROM IpBan WHERE aid = :accountId");
      query.setParameter("accountId", accountId);
      execute(entityManager, query);
   }
}
