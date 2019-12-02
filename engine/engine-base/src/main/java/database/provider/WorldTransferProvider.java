package database.provider;

import java.sql.Timestamp;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import database.AbstractQueryExecutor;
import client.database.data.PendingWorldTransfers;
import client.database.utility.PendingWorldTransferTransformer;
import entity.WorldTransfer;

public class WorldTransferProvider extends AbstractQueryExecutor {
   private static WorldTransferProvider instance;

   public static WorldTransferProvider getInstance() {
      if (instance == null) {
         instance = new WorldTransferProvider();
      }
      return instance;
   }

   private WorldTransferProvider() {
   }

   public Timestamp getCompletionTimeByCharacterId(EntityManager entityManager, int characterId) {
      TypedQuery<Timestamp> query = entityManager.createQuery("SELECT w.completionTime FROM WorldTransfer w WHERE w.characterId = :characterId", Timestamp.class);
      query.setParameter("characterId", characterId);
      return getSingleWithDefault(query, null);
   }

   public List<PendingWorldTransfers> getPendingTransfers(EntityManager entityManager) {
      TypedQuery<WorldTransfer> query = entityManager.createQuery("FROM WorldTransfer w WHERE w.completionTime IS NULL", WorldTransfer.class);
      return getResultList(query, new PendingWorldTransferTransformer());
   }

   public int countOutstandingWorldTransfers(EntityManager entityManager, int characterId) {
      TypedQuery<Long> query = entityManager.createQuery("SELECT COUNT(*) FROM WorldTransfer w WHERE w.characterId = :characterId AND w.completionTime IS NULL", Long.class);
      query.setParameter("characterId", characterId);
      return getSingleWithDefault(query, 0L).intValue();
   }
}