package database.administrator;

import java.sql.Timestamp;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import database.AbstractQueryExecutor;
import entity.NameChange;

public class NameChangeAdministrator extends AbstractQueryExecutor {
   private static NameChangeAdministrator instance;

   public static NameChangeAdministrator getInstance() {
      if (instance == null) {
         instance = new NameChangeAdministrator();
      }
      return instance;
   }

   private NameChangeAdministrator() {
   }

   public void markCompleted(EntityManager entityManager, int id) {
      Query query = entityManager.createQuery("UPDATE NameChange SET completionTime = :time WHERE id = :id");
      query.setParameter("time", new Timestamp(System.currentTimeMillis()));
      query.setParameter("id", id);
      execute(entityManager, query);
   }

   public void cancelPendingNameChange(EntityManager entityManager, int characterId) {
      Query query = entityManager.createQuery("DELETE FROM NameChange WHERE characterId = :characterId AND completionTime IS NULL");
      query.setParameter("characterId", characterId);
      execute(entityManager, query);
   }

   public void create(EntityManager entityManager, int characterId, String oldName, String newName) {
      NameChange nameChange = new NameChange();
      nameChange.setCharacterId(characterId);
      nameChange.setOld(oldName);
      nameChange.setNewName(newName);
      insert(entityManager, nameChange);
   }
}