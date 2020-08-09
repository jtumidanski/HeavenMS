package database.provider;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import accessor.AbstractQueryExecutor;
import client.database.data.MakerRecipeData;

public class MakerRecipeProvider extends AbstractQueryExecutor {
   private static MakerRecipeProvider instance;

   public static MakerRecipeProvider getInstance() {
      if (instance == null) {
         instance = new MakerRecipeProvider();
      }
      return instance;
   }

   private MakerRecipeProvider() {
   }

   public List<MakerRecipeData> getRecipeForItem(EntityManager entityManager, int itemId) {
      TypedQuery<MakerRecipeData> query = entityManager.createQuery("SELECT NEW client.database.data.MakerRecipeData(m.requiredItem, m.count) FROM MakerRecipeData m WHERE m.itemId = :itemId", MakerRecipeData.class);
      query.setParameter("itemId", itemId);
      return query.getResultList();
   }

   public List<MakerRecipeData> getMakerDisassembledItems(EntityManager entityManager, int itemId) {
      TypedQuery<MakerRecipeData> query = entityManager.createQuery("SELECT NEW client.database.data.MakerRecipeData(m.requiredItem, m.count / 2) FROM MakerRecipeData m WHERE m.itemId = :itemId AND m.requiredItem >= 4260000 AND m.requiredItem < 4270000 ", MakerRecipeData.class);
      query.setParameter("itemId", itemId);
      return query.getResultList();
   }
}