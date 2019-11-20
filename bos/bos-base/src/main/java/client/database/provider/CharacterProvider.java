package client.database.provider;

import java.util.List;
import java.util.Optional;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

import client.database.AbstractQueryExecutor;
import rest.buddy.Character;

public class CharacterProvider extends AbstractQueryExecutor {
   private static CharacterProvider instance;

   public static CharacterProvider getInstance() {
      if (instance == null) {
         instance = new CharacterProvider();
      }
      return instance;
   }

   private CharacterProvider() {
   }

   public List<Integer> getAllChars(EntityManager entityManager) {
      TypedQuery<Integer> query = entityManager.createQuery("SELECT c.id FROM Character c", Integer.class);
      return query.getResultList();
   }

   public Optional<Character> getCharacter(EntityManager entityManager, int characterId) {
      TypedQuery<Character> query = entityManager.createQuery("SELECT NEW rest.buddy.Character(c.id, c.accountId, c.buddyCapacity) FROM Character c WHERE c.id = :characterId", Character.class);
      query.setParameter("characterId", characterId);
      try {
         return Optional.of(query.getSingleResult());
      } catch (NoResultException exception) {
         return Optional.empty();
      }
   }
}