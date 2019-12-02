package database.administrator;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import database.AbstractQueryExecutor;
import database.DeleteForCharacter;
import entity.MonsterBook;

public class MonsterBookAdministrator extends AbstractQueryExecutor implements DeleteForCharacter {
   private static MonsterBookAdministrator instance;

   public static MonsterBookAdministrator getInstance() {
      if (instance == null) {
         instance = new MonsterBookAdministrator();
      }
      return instance;
   }

   private MonsterBookAdministrator() {
   }

   @Override
   public void deleteForCharacter(EntityManager entityManager, int characterId) {
      Query query = entityManager.createQuery("DELETE FROM MonsterBook WHERE characterId = :characterId");
      query.setParameter("characterId", characterId);
      execute(entityManager, query);
   }

   public void save(EntityManager entityManager, int charId, Set<Map.Entry<Integer, Integer>> cardSet) {
      List<MonsterBook> monsterBookList = cardSet.stream().map(card -> {
         MonsterBook monsterBook = new MonsterBook();
         monsterBook.setCharacterId(charId);
         monsterBook.setCardId(card.getKey());
         monsterBook.setLevel(card.getValue());
         return monsterBook;
      }).collect(Collectors.toList());
      insertBulk(entityManager, monsterBookList);
   }
}
