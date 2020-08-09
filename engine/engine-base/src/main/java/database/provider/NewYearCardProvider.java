package database.provider;

import java.util.List;
import java.util.Optional;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import accessor.AbstractQueryExecutor;
import client.database.utility.NewYearCardRecordFromResultSetTransformer;
import client.newyear.NewYearCardRecord;
import entity.NewYear;

public class NewYearCardProvider extends AbstractQueryExecutor {
   private static NewYearCardProvider instance;

   public static NewYearCardProvider getInstance() {
      if (instance == null) {
         instance = new NewYearCardProvider();
      }
      return instance;
   }

   private NewYearCardProvider() {
   }

   public List<NewYearCardRecord> getNotReceived(EntityManager entityManager) {
      TypedQuery<NewYear> query = entityManager.createQuery("FROM NewYear n WHERE n.timerReceived = 0 AND n.senderDiscard = 0", NewYear.class);
      return getResultList(query, new NewYearCardRecordFromResultSetTransformer());
   }

   public Optional<NewYearCardRecord> getById(EntityManager entityManager, int cardId) {
      TypedQuery<NewYear> query = entityManager.createQuery("FROM NewYear n WHERE n.id = :id", NewYear.class);
      query.setParameter("id", cardId);
      return getSingleOptional(query, new NewYearCardRecordFromResultSetTransformer());
   }

   public List<NewYearCardRecord> getBySenderOrReceiver(EntityManager entityManager, int senderId, int receiverId) {
      TypedQuery<NewYear> query = entityManager.createQuery("FROM NewYear n WHERE n.senderId = :senderId OR n.receiverId = :receiverId", NewYear.class);
      query.setParameter("senderId", senderId);
      query.setParameter("receiverId", receiverId);
      return getResultList(query, new NewYearCardRecordFromResultSetTransformer());
   }
}