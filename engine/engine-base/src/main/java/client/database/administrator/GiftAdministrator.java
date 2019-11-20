package client.database.administrator;


import javax.persistence.EntityManager;
import javax.persistence.Query;

import client.database.AbstractQueryExecutor;
import entity.Gift;

public class GiftAdministrator extends AbstractQueryExecutor {
   private static GiftAdministrator instance;

   public static GiftAdministrator getInstance() {
      if (instance == null) {
         instance = new GiftAdministrator();
      }
      return instance;
   }

   private GiftAdministrator() {
   }

   public void createGift(EntityManager entityManager, int recipient, String from, String message, int sn, int ringId) {
      Gift gift = new Gift();
      gift.setGiftedTo(recipient);
      gift.setGiftedFrom(from);
      gift.setMessage(message);
      gift.setSn(sn);
      gift.setRingId(ringId);
      insert(entityManager, gift);
   }

   public void deleteAllGiftsForCharacter(EntityManager entityManager, int characterId) {
      Query query = entityManager.createQuery("DELETE FROM Gift WHERE giftedTo = :to");
      query.setParameter("to", characterId);
      execute(entityManager, query);
   }
}