package database.provider;

import java.util.List;
import java.util.Optional;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import accessor.AbstractQueryExecutor;
import client.database.data.NxCodeData;
import client.database.utility.NxCodeTransformer;
import entity.nx.NxCode;

public class NxCodeProvider extends AbstractQueryExecutor {
   private static NxCodeProvider instance;

   public static NxCodeProvider getInstance() {
      if (instance == null) {
         instance = new NxCodeProvider();
      }
      return instance;
   }

   private NxCodeProvider() {
   }

   public Optional<NxCodeData> get(EntityManager entityManager, String code) {
      TypedQuery<NxCode> query = entityManager.createQuery("FROM NxCode n WHERE n.code = :code", NxCode.class);
      query.setParameter("code", code);
      return getSingleOptional(query, new NxCodeTransformer());
   }

   public List<Integer> getExpiredCodes(EntityManager entityManager, long time) {
      TypedQuery<Integer> query = entityManager.createQuery("SELECT n.id FROM NxCode n WHERE n.expiration <= :time", Integer.class);
      query.setParameter("time", time);
      return query.getResultList();
   }
}