package database.provider;

import java.util.Optional;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import accessor.AbstractQueryExecutor;
import database.transformer.MakerReagentDataTransformer;
import entity.maker.MakerReagentData;

public class MakerReagentProvider extends AbstractQueryExecutor {
   private static MakerReagentProvider instance;

   public static MakerReagentProvider getInstance() {
      if (instance == null) {
         instance = new MakerReagentProvider();
      }
      return instance;
   }

   private MakerReagentProvider() {
   }

   public Optional<client.database.data.MakerReagentData> getForItem(EntityManager entityManager, int itemId) {
      TypedQuery<MakerReagentData> query = entityManager.createQuery("SELECT m FROM MakerReagentData m WHERE m.itemId = :itemId", MakerReagentData.class);
      query.setParameter("itemId", itemId);
      return getSingleOptional(query, new MakerReagentDataTransformer());
   }
}