package database.provider;

import java.util.Optional;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import accessor.AbstractQueryExecutor;
import client.database.data.MarriageData;

public class MarriageProvider extends AbstractQueryExecutor {
   private static MarriageProvider instance;

   public static MarriageProvider getInstance() {
      if (instance == null) {
         instance = new MarriageProvider();
      }
      return instance;
   }

   private MarriageProvider() {
   }

   public Optional<MarriageData> getById(EntityManager entityManager, int marriageId) {
      TypedQuery<MarriageData> query = entityManager.createQuery("SELECT NEW client.database.data.MarriageData(m.marriageId, m.husbandId, m.wifeId) FROM Marriage m WHERE m.marriageId = :marriageId", MarriageData.class);
      query.setParameter("marriageId", marriageId);
      return getSingleOptional(query);
   }

   public Optional<MarriageData> getBySpouses(EntityManager entityManager, int spouse1, int spouse2) {
      TypedQuery<MarriageData> query = entityManager.createQuery("SELECT NEW client.database.data.MarriageData(m.marriageId, m.husbandId, m.wifeId) FROM Marriage m WHERE m.husbandId = :spouse1 OR m.wifeId = :spouse2", MarriageData.class);
      query.setParameter("spouse1", spouse1);
      query.setParameter("spouse2", spouse2);
      return getSingleOptional(query);
   }
}