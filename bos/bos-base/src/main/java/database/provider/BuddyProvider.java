package database.provider;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

import accessor.AbstractQueryExecutor;
import client.BuddyListEntry;
import client.BuddyListStats;

public class BuddyProvider extends AbstractQueryExecutor {
   private static BuddyProvider instance;

   public static BuddyProvider getInstance() {
      if (instance == null) {
         instance = new BuddyProvider();
      }
      return instance;
   }

   private BuddyProvider() {
   }

   public boolean buddyIsPending(EntityManager entityManager, int characterId, int buddyId, String group) {
      TypedQuery<Integer> query = entityManager.createQuery("SELECT 1 FROM Buddy b WHERE b.characterId = :characterId AND b.buddyId = :buddyId AND b.buddyGroup = :group", Integer.class);
      query.setParameter("characterId", characterId);
      query.setParameter("buddyId", buddyId);
      query.setParameter("group", group);
      try {
         return query.getSingleResult() == 1;
      } catch (NoResultException exception) {
         return false;
      }
   }

   public boolean buddyIsInOtherGroup(EntityManager entityManager, int characterId, int buddyId, String group) {
      TypedQuery<Integer> query = entityManager.createQuery("SELECT 1 FROM Buddy b WHERE b.characterId = :characterId AND b.buddyId = :buddyId AND b.buddyGroup <> :group", Integer.class);
      query.setParameter("characterId", characterId);
      query.setParameter("buddyId", buddyId);
      query.setParameter("group", group);
      try {
         return query.getSingleResult() == 1;
      } catch (NoResultException exception) {
         return false;
      }
   }

   public boolean atCapacity(EntityManager entityManager, int characterId) {
      TypedQuery<BuddyListStats> query = entityManager.createQuery("SELECT NEW client.BuddyListStats(COUNT(b), c.buddyCapacity) FROM Buddy b JOIN Character c ON b.characterId = c.id WHERE c.id = :characterId GROUP BY c.id", BuddyListStats.class);
      query.setParameter("characterId", characterId);
      BuddyListStats buddyListStats;
      try {
         buddyListStats = query.getSingleResult();
      } catch (NoResultException exception) {
         return false;
      }
      return buddyListStats.buddies() >= buddyListStats.capacity();
   }

   public boolean buddyExists(EntityManager entityManager, int characterId) {
      TypedQuery<Integer> query = entityManager.createQuery("SELECT 1 FROM Character c WHERE c.id = :characterId", Integer.class);
      query.setParameter("characterId", characterId);
      try {
         return query.getSingleResult() == 1;
      } catch (NoResultException exception) {
         return false;
      }
   }

   public List<BuddyListEntry> getInfoForBuddies(EntityManager entityManager, int characterId) {
      TypedQuery<BuddyListEntry> query = entityManager.createQuery(
            "SELECT NEW client.BuddyListEntry(b.buddyGroup, b.buddyId, -1, true) " +
                  "FROM Buddy b " +
                  "WHERE b.characterId = :characterId AND b.pending <> 1", BuddyListEntry.class);
      query.setParameter("characterId", characterId);
      return query.getResultList();
   }

   public List<Integer> getInfoForPendingBuddies(EntityManager entityManager, int characterId) {
      TypedQuery<Integer> query = entityManager.createQuery(
            "SELECT b.buddyId " +
                  "FROM Buddy b JOIN Character c ON b.buddyId = c.id " +
                  "WHERE b.characterId = :characterId AND b.pending = 1 AND b.responseRequired = true", Integer.class);
      query.setParameter("characterId", characterId);
      return query.getResultList();
   }
}
