package database.administrator;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import accessor.AbstractQueryExecutor;
import entity.Ring;

public class RingAdministrator extends AbstractQueryExecutor {
   private static RingAdministrator instance;

   public static RingAdministrator getInstance() {
      if (instance == null) {
         instance = new RingAdministrator();
      }
      return instance;
   }

   private RingAdministrator() {
   }

   public void deleteRing(EntityManager entityManager, int ringId) {
      Query query = entityManager.createQuery("DELETE FROM Ring WHERE id = :id");
      query.setParameter("id", ringId);
      execute(entityManager, query);
   }

   public void deleteRing(EntityManager entityManager, int ringId, int partnerRingId) {
      Query query = entityManager.createQuery("DELETE FROM Ring WHERE id = :id OR id = :partnerRingId");
      query.setParameter("id", ringId);
      query.setParameter("partnerRingId", partnerRingId);
      execute(entityManager, query);
   }

   public void addRing(EntityManager entityManager, int ringId, int itemId, int partnerRingId, int partnerCharacterId, String partnerName) {
      Ring ring = new Ring();
      ring.setId(ringId);
      ring.setItemId(itemId);
      ring.setPartnerRingId(partnerRingId);
      ring.setPartnerCharacterId(partnerCharacterId);
      ring.setPartnerName(partnerName);
      insert(entityManager, ring);
   }

   public void updatePartnerName(EntityManager entityManager, String newName, String oldName) {
      Query query = entityManager.createQuery("UPDATE Ring SET partnerName = :newPartnerName WHERE partnerName = :oldPartnerName");
      query.setParameter("newPartnerName", newName);
      query.setParameter("oldPartnerName", oldName);
      execute(entityManager, query);
   }
}