package client.database.provider;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

import client.BuddyListEntry;
import client.CharacterNameAndId;
import client.database.AbstractQueryExecutor;

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

   public List<Integer> getBuddies(EntityManager entityManager, int characterId) {
      TypedQuery<Integer> query = entityManager.createQuery("SELECT b.buddyId FROM Buddy b WHERE b.characterId = :characterId", Integer.class);
      query.setParameter("characterId", characterId);
      return query.getResultList();
   }

   public List<BuddyListEntry> getInfoForBuddies(EntityManager entityManager, int characterId) {
      TypedQuery<BuddyListEntry> query = entityManager.createQuery(
            "SELECT NEW client.BuddyListEntry(c.name, b.buddyGroup, b.buddyId, -1, true) " +
                  "FROM Buddy b JOIN Character c ON b.buddyId = c.id " +
                  "WHERE b.characterId = :characterId AND b.pending != 1", BuddyListEntry.class);
      query.setParameter("characterId", characterId);
      return query.getResultList();
   }

   public List<CharacterNameAndId> getInfoForPendingBuddies(EntityManager entityManager, int characterId) {
      TypedQuery<CharacterNameAndId> query = entityManager.createQuery(
            "SELECT NEW client.CharacterNameAndId(b.buddyId, c.name) " +
                  "FROM Buddy b JOIN Character c ON b.buddyId = c.id " +
                  "WHERE b.characterId = :characterId AND b.pending = 1", CharacterNameAndId.class);
      query.setParameter("characterId", characterId);
      return query.getResultList();
   }

   public long getBuddyCount(EntityManager entityManager, int characterId) {
      TypedQuery<Long> query = entityManager.createQuery("SELECT COUNT(*) FROM Buddy b WHERE b.characterId = :characterId AND b.pending = 0", Long.class);
      query.setParameter("characterId", characterId);
      return getSingleWithDefault(query, 0L);
   }

   public boolean buddyIsPending(EntityManager entityManager, int characterId, int buddyId) {
      TypedQuery<Integer> query = entityManager.createQuery("SELECT 1 FROM Buddy b WHERE b.characterId = :characterId AND b.buddyId = :buddyId", Integer.class);
      query.setParameter("characterId", characterId);
      query.setParameter("buddyId", buddyId);
      try {
         query.getFirstResult();
         return true;
      } catch (NoResultException exception) {
         return false;
      }
   }
}
