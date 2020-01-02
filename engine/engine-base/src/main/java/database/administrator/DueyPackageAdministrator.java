package database.administrator;

import java.sql.Timestamp;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import database.AbstractQueryExecutor;
import entity.duey.DueyPackage;

public class DueyPackageAdministrator extends AbstractQueryExecutor {
   private static DueyPackageAdministrator instance;

   public static DueyPackageAdministrator getInstance() {
      if (instance == null) {
         instance = new DueyPackageAdministrator();
      }
      return instance;
   }

   private DueyPackageAdministrator() {
   }

   public void uncheck(EntityManager entityManager, int characterId) {
      Query query = entityManager.createQuery("UPDATE DueyPackage SET checked = 0 WHERE receiverId = :receiverId");
      query.setParameter("receiverId", characterId);
      execute(entityManager, query);
   }

   public void removePackage(EntityManager entityManager, int packageId) {
      Query query = entityManager.createQuery("DELETE FROM DueyPackage WHERE packageId = :packageId");
      query.setParameter("packageId", packageId);
      execute(entityManager, query);
   }

   public void deletePackagesAfter(EntityManager entityManager, Timestamp timestamp) {
      Query query = entityManager.createQuery("DELETE FROM DueyPackage WHERE timestamp < :timestamp");
      query.setParameter("timestamp", timestamp);
      execute(entityManager, query);
   }

   public int create(EntityManager entityManager, int recipientId, String senderName, int mesos, String message, boolean quick) {
      DueyPackage dueyPackage = new DueyPackage();
      dueyPackage.setReceiverId(recipientId);
      dueyPackage.setSenderName(senderName);
      dueyPackage.setMesos(mesos);
      dueyPackage.setTimestamp(new Timestamp(System.currentTimeMillis()));
      dueyPackage.setMessage(message);
      dueyPackage.setChecked(quick ? 1 : 0);
      insert(entityManager, dueyPackage);
      return dueyPackage.getPackageId();
   }
}