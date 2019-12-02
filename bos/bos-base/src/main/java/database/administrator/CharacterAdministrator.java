package database.administrator;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import database.AbstractQueryExecutor;
import entity.Character;

public class CharacterAdministrator extends AbstractQueryExecutor {
   private static CharacterAdministrator instance;

   public static CharacterAdministrator getInstance() {
      if (instance == null) {
         instance = new CharacterAdministrator();
      }
      return instance;
   }

   private CharacterAdministrator() {
   }

   public void addCharacter(EntityManager entityManager, int accountId, int characterId) {
      Character character = new Character();
      character.setAccountId(accountId);
      character.setId(characterId);
      insert(entityManager, character);
   }

   public void updateCharacter(EntityManager entityManager, int characterId, int capacity) {
      update(entityManager, Character.class, characterId, character -> character.setBuddyCapacity(capacity));
   }

   public void deleteCharacter(EntityManager entityManager, int characterId) {
      Query query = entityManager.createQuery("DELETE FROM Character WHERE id = :characterId");
      query.setParameter("characterId", characterId);
      execute(entityManager, query);
   }
}