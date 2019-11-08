package client.database.provider;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import client.database.AbstractQueryExecutor;
import client.database.data.GiftData;

public class GiftProvider extends AbstractQueryExecutor {
   private static GiftProvider instance;

   public static GiftProvider getInstance() {
      if (instance == null) {
         instance = new GiftProvider();
      }
      return instance;
   }

   private GiftProvider() {
   }

   public List<GiftData> getGiftsForCharacter(EntityManager entityManager, int characterId) {
      TypedQuery<GiftData> query = entityManager.createQuery(
            "SELECT NEW client.database.data.GiftData(g.sn, g.ringId, g.message, g.giftedFrom) " +
                  "FROM Gift g WHERE g.giftedTo = :characterId", GiftData.class);
      query.setParameter("characterId", characterId);
      return query.getResultList();
   }
}