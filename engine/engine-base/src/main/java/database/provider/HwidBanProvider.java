package database.provider;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import accessor.AbstractQueryExecutor;

public class HwidBanProvider extends AbstractQueryExecutor {
   private static HwidBanProvider instance;

   public static HwidBanProvider getInstance() {
      if (instance == null) {
         instance = new HwidBanProvider();
      }
      return instance;
   }

   private HwidBanProvider() {
   }

   public long getHwidBanCount(EntityManager entityManager, String hwid) {
      TypedQuery<Long> query = entityManager.createQuery("SELECT COUNT(*) FROM HwidBan h WHERE h.hwid LIKE :hwid", Long.class);
      query.setParameter("hwid", hwid);
      return getSingleWithDefault(query, 0L);
   }
}
