package database.provider;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import accessor.AbstractQueryExecutor;
import client.database.data.PendingNameChanges;
import database.transformer.PendingNameChangesTransformer;
import entity.NameChange;

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
      TypedQuery<NameChange> query = entityManager.createQuery("SELECT n FROM NameChange n WHERE n.completionTime IS NULL", NameChange.class);
      return getResultList(query, new PendingNameChangesTransformer());
   }

   public Optional<PendingNameChanges> getPendingNameChangeForCharacter(EntityManager entityManager, int characterId) {
      TypedQuery<NameChange> query = entityManager.createQuery("SELECT n FROM NameChange n WHERE n.characterId = :characterId AND n.completionTime IS NULL", NameChange.class);
      query.setParameter("characterId", characterId);
      return getSingleOptional(query, new PendingNameChangesTransformer());
   }
}