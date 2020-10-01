package database.provider;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import accessor.AbstractQueryExecutor;
import client.database.data.AreaInfoData;
import database.transformer.AreaInfoDataTransformer;
import entity.AreaInfo;

public class AreaInfoProvider extends AbstractQueryExecutor {
   private static AreaInfoProvider instance;

   public static AreaInfoProvider getInstance() {
      if (instance == null) {
         instance = new AreaInfoProvider();
      }
      return instance;
   }

   private AreaInfoProvider() {
   }

   public List<AreaInfoData> getAreaInfo(EntityManager entityManager, int characterId) {
      TypedQuery<AreaInfo> query = entityManager.createQuery("SELECT ai FROM AreaInfo ai WHERE ai.characterId = :characterId", AreaInfo.class);
      query.setParameter("characterId", characterId);
      return getResultList(query, new AreaInfoDataTransformer());
   }
}