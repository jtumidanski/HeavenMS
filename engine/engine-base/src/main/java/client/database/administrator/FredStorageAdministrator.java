package client.database.administrator;

import java.sql.Timestamp;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import client.database.AbstractQueryExecutor;
import entity.FredStorage;
import tools.Pair;

public class FredStorageAdministrator extends AbstractQueryExecutor {
   private static FredStorageAdministrator instance;

   public static FredStorageAdministrator getInstance() {
      if (instance == null) {
         instance = new FredStorageAdministrator();
      }
      return instance;
   }

   private FredStorageAdministrator() {
   }

   public void deleteForCharacter(EntityManager entityManager, int characterId) {
      Query query = entityManager.createQuery("DELETE FROM FredStorage WHERE characterId = :characterId");
      query.setParameter("characterId", characterId);
      execute(entityManager, query);
   }

   public void deleteForCharacterBatch(EntityManager entityManager, List<Integer> characterIds) {
      Query query = entityManager.createQuery("DELETE FROM FredStorage WHERE characterId IN :characterIds");
      query.setParameter("characterIds", characterIds);
      execute(entityManager, query);
   }

   public void create(EntityManager entityManager, int characterId) {
      FredStorage fredStorage = new FredStorage();
      fredStorage.setCharacterId(characterId);
      fredStorage.setDayNotes(0);
      fredStorage.setTimestamp(new Timestamp(System.currentTimeMillis()));
      insert(entityManager, fredStorage);
   }

   public void updateNotesBatch(EntityManager entityManager, List<Pair<Integer, Integer>> data) {
      entityManager.getTransaction().begin();
      data.forEach(dataPoint -> {
         Query query = entityManager.createQuery("UPDATE FredStorage SET dayNotes = :dayNotes WHERE characterId = :characterId");
         query.setParameter("dayNotes", dataPoint.getLeft());
         query.setParameter("characterId", dataPoint.getRight());
         query.executeUpdate();
      });
      entityManager.getTransaction().commit();
   }
}