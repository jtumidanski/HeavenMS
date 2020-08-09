package database.provider;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import accessor.AbstractQueryExecutor;
import client.database.data.PendingNameChanges;

public class NameChangeProvider extends AbstractQueryExecutor {
   private static NameChangeProvider instance;

   public static NameChangeProvider getInstance() {
      if (instance == null) {
         instance = new NameChangeProvider();
      }
      return instance;
   }

   private NameChangeProvider() {
   }

   public Optional<Timestamp> getCompletionTimeByCharacterId(EntityManager entityManager, int characterId) {
      TypedQuery<Timestamp> query = entityManager.createQuery("SELECT n.completionTime FROM NameChange n WHERE n.characterId = :characterId", Timestamp.class);
      query.setParameter("characterId", characterId);
      return getSingleOptional(query);
   }

   public List<PendingNameChanges> getPendingNameChanges(EntityManager entityManager) {
      TypedQuery<PendingNameChanges> query = entityManager.createQuery(
            "SELECT NEW client.database.data.PendingNameChanges(n.id, n.characterId, n.old, n.newName) " +
                  "FROM NameChange n " +
                  "WHERE n.completionTime IS NULL", PendingNameChanges.class);
      return query.getResultList();
   }

   public Optional<PendingNameChanges> getPendingNameChangeForCharacter(EntityManager entityManager, int characterId) {
      TypedQuery<PendingNameChanges> query = entityManager.createQuery(
            "SELECT NEW client.database.data.PendingNameChanges(n.id, n.characterId, n.old, n.newName) " +
                  "FROM NameChange n " +
                  "WHERE n.characterId = :characterId AND n.completionTime IS NULL", PendingNameChanges.class);
      query.setParameter("characterId", characterId);
      return getSingleOptional(query);
   }
}