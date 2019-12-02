package database.provider;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import database.AbstractQueryExecutor;

public class IpBanProvider extends AbstractQueryExecutor {
   private static IpBanProvider instance;

   public static IpBanProvider getInstance() {
      if (instance == null) {
         instance = new IpBanProvider();
      }
      return instance;
   }

   private IpBanProvider() {
   }

   public long getIpBanCount(EntityManager entityManager, String ipAddress) {
      TypedQuery<Long> query = entityManager.createQuery("SELECT COUNT(*) FROM IpBan i WHERE :ipAddress LIKE CONCAT(i.ip, '%')", Long.class);
      query.setParameter("ipAddress", ipAddress);
      return getSingleWithDefault(query, 0L);
   }
}
