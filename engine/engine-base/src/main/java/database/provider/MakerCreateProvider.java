package database.provider;

import java.util.Optional;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import database.AbstractQueryExecutor;
import client.database.data.MakerCreateData;

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

   public Optional<MakerCreateData> getMakerCreateDataForItem(EntityManager entityManager, int itemId) {
      TypedQuery<MakerCreateData> query = entityManager.createQuery(
            "SELECT NEW client.database.data.MakerCreateData(m.requiredLevel, m.requiredMakerLevel, m.requiredMeso, m.quantity) " +
                  "FROM MakerCreateData m " +
                  "WHERE m.itemId = :itemId", MakerCreateData.class);
      query.setParameter("itemId", itemId);
      return getSingleOptional(query);
   }
}