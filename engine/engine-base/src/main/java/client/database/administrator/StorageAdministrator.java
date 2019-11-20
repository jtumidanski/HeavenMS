package client.database.administrator;


import javax.persistence.EntityManager;
import javax.persistence.Query;

import client.database.AbstractQueryExecutor;
import entity.Storage;

public class StorageAdministrator extends AbstractQueryExecutor {
   private static StorageAdministrator instance;

   public static StorageAdministrator getInstance() {
      if (instance == null) {
         instance = new StorageAdministrator();
      }
      return instance;
   }

   private StorageAdministrator() {
   }

   public void create(EntityManager entityManager, int accountId, int worldId) {
      Storage storage = new Storage();
      storage.setAccountId(accountId);
      storage.setWorld(worldId);
      storage.setSlots(4);
      storage.setMeso(0);
      insert(entityManager, storage);
   }

   public void update(EntityManager entityManager, int storageId, int slots, int mesos) {
      Query query = entityManager.createQuery("UPDATE Storage SET slots = :slots, meso = :meso WHERE storageId = :storageId");
      query.setParameter("slots", slots);
      query.setParameter("meso", mesos);
      query.setParameter("storageId", storageId);
      execute(entityManager, query);
   }
}