package database.provider;

import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import accessor.AbstractQueryExecutor;
import client.database.utility.MapleStorageTransformer;
import entity.Storage;
import server.MapleStorage;

public class StorageProvider extends AbstractQueryExecutor {
   private static StorageProvider instance;

   public static StorageProvider getInstance() {
      if (instance == null) {
         instance = new StorageProvider();
      }
      return instance;
   }

   private StorageProvider() {
   }

   public Optional<MapleStorage> getByAccountAndWorld(EntityManager entityManager, int accountId, int worldId) {
      TypedQuery<Storage> query = entityManager.createQuery("FROM Storage s WHERE s.accountId = :accountId AND s.world = :world", Storage.class);
      query.setParameter("accountId", accountId);
      query.setParameter("world", worldId);
      return getSingleOptional(query, new MapleStorageTransformer());
   }
}