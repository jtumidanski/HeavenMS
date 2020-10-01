package database.provider;

import java.util.Optional;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import accessor.AbstractQueryExecutor;
import database.transformer.MakerCreateDataTransformer;
import entity.maker.MakerCreateData;

public class MakerCreateProvider extends AbstractQueryExecutor {
   private static MakerCreateProvider instance;

   public static MakerCreateProvider getInstance() {
      if (instance == null) {
         instance = new MakerCreateProvider();
      }
      return instance;
   }

   private MakerCreateProvider() {
   }

   public Optional<client.database.data.MakerCreateData> getMakerCreateDataForItem(EntityManager entityManager, int itemId) {
      TypedQuery<MakerCreateData> query = entityManager.createQuery("SELECT m FROM MakerCreateData m WHERE m.itemId = :itemId", MakerCreateData.class);
      query.setParameter("itemId", itemId);
      return getSingleOptional(query, new MakerCreateDataTransformer());
   }
}