package database.provider;

import java.util.Optional;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import accessor.AbstractQueryExecutor;
import client.database.data.MakerReagentData;

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

   public Optional<MakerReagentData> getForItem(EntityManager entityManager, int itemId) {
      TypedQuery<MakerReagentData> query = entityManager.createQuery("SELECT NEW client.database.data.MakerReagentData(m.stat, m.value) FROM MakerReagentData m WHERE m.itemId = :itemId", MakerReagentData.class);
      query.setParameter("itemId", itemId);
      return getSingleOptional(query);
   }
}