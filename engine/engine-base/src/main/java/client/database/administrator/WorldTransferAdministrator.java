package client.database.administrator;

import java.sql.Timestamp;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import client.database.AbstractQueryExecutor;
import entity.WorldTransfer;

public class WorldTransferAdministrator extends AbstractQueryExecutor {
   private static WorldTransferAdministrator instance;

   public static WorldTransferAdministrator getInstance() {
      if (instance == null) {
         instance = new WorldTransferAdministrator();
      }
      return instance;
   }

   private WorldTransferAdministrator() {
   }

   public void create(EntityManager entityManager, int characterId, int oldWorld, int newWorld) {
      WorldTransfer worldTransfer = new WorldTransfer();
      worldTransfer.setCharacterId(characterId);
      worldTransfer.setFromWorld(oldWorld);
      worldTransfer.setToWorld(newWorld);
      insert(entityManager, worldTransfer);
   }

   public void cancelPendingForCharacter(EntityManager entityManager, int characterId) {
      Query query = entityManager.createQuery("DELETE FROM WorldTransfer WHERE characterId = :characterId AND completionTime IS NULL");
      query.setParameter("characterId", characterId);
      execute(entityManager, query);
   }

   public void markComplete(EntityManager entityManager, int transferId) {
      Query query = entityManager.createQuery("UPDATE WorldTransfer SET completionTime = :time WHERE id = :id");
      query.setParameter("time", new Timestamp(System.currentTimeMillis()));
      query.setParameter("id", transferId);
      execute(entityManager, query);
   }

   public void cancelById(EntityManager entityManager, int transferId) {
      Query query = entityManager.createQuery("DELETE FROM WorldTransfer WHERE id = :id");
      query.setParameter("id", transferId);
      execute(entityManager, query);
   }
}