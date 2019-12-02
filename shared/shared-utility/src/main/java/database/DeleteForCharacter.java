package database;

import javax.persistence.EntityManager;

public interface DeleteForCharacter {
   void deleteForCharacter(EntityManager entityManager, int characterId);
}
