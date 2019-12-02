package database.administrator;


import javax.persistence.EntityManager;
import javax.persistence.Query;

import database.AbstractQueryExecutor;
import entity.NewYear;

public class NewYearAdministrator extends AbstractQueryExecutor {
   private static NewYearAdministrator instance;

   public static NewYearAdministrator getInstance() {
      if (instance == null) {
         instance = new NewYearAdministrator();
      }
      return instance;
   }

   private NewYearAdministrator() {
   }

   public int create(EntityManager entityManager, int senderId, String senderName, int receiverId, String receiverName,
                     String content, boolean senderDiscardCard, boolean receiverDiscardCard,
                     boolean receiverReceivedCard, long dateSent, long dateReceived) {
      NewYear newYear = new NewYear();
      newYear.setSenderId(senderId);
      newYear.setSenderName(senderName);
      newYear.setReceiverId(receiverId);
      newYear.setReceiverName(receiverName);
      newYear.setMessage(content);
      newYear.setSenderDiscard(senderDiscardCard ? 1 : 0);
      newYear.setReceiverDiscard(receiverDiscardCard ? 1 : 0);
      newYear.setReceived(receiverReceivedCard ? 1 : 0);
      newYear.setTimeSent(dateSent);
      newYear.setTimerReceived(dateReceived);
      insert(entityManager, newYear);
      return newYear.getId();
   }

   public void setReceived(EntityManager entityManager, int id, long dateReceived) {
      Query query = entityManager.createQuery("UPDATE NewYear SET received = 1, timerReceived = :time WHERE id = :id");
      query.setParameter("time", dateReceived);
      query.setParameter("id", id);
      execute(entityManager, query);
   }

   public void deleteById(EntityManager entityManager, int id) {
      Query query = entityManager.createQuery("DELETE FROM NewYear WHERE id = :id");
      query.setParameter("id", id);
      execute(entityManager, query);
   }
}