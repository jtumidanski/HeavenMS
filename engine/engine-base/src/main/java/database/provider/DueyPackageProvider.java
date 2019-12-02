package database.provider;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import database.AbstractQueryExecutor;
import client.database.utility.DueyPackageFromResultSetTransformer;
import server.DueyPackage;
import tools.Pair;

public class DueyPackageProvider extends AbstractQueryExecutor {
   private static DueyPackageProvider instance;

   public static DueyPackageProvider getInstance() {
      if (instance == null) {
         instance = new DueyPackageProvider();
      }
      return instance;
   }

   private DueyPackageProvider() {
   }

   public Optional<Integer> getPackageTypeForCharacter(EntityManager entityManager, int characterId) {
      TypedQuery<Integer> query = entityManager.createQuery("SELECT d.type FROM DueyPackage d WHERE d.receiverId = :characterId AND d.checked = 1 ORDER BY d.type DESC", Integer.class);
      query.setParameter("characterId", characterId);
      return getSingleOptional(query);
   }

   public Optional<Pair<String, Integer>> get(EntityManager entityManager, int characterId) {
      Query query = entityManager.createQuery("SELECT d.senderName, d.type FROM DueyPackage d WHERE d.receiverId = :characterId AND d.checked = 1 ORDER BY d.type DESC");
      query.setParameter("characterId", characterId);
      try {
         Object[] result = (Object[]) query.getSingleResult();
         return Optional.of(new Pair<>((String) result[0], (int) result[1]));
      } catch (NoResultException exception) {
         return Optional.empty();
      }
   }

   public List<DueyPackage> getPackagesForReceiver(EntityManager entityManager, int characterId) {
      TypedQuery<entity.duey.DueyPackage> query = entityManager.createQuery("FROM DueyPackage d WHERE d.receiverId = :receiverId", entity.duey.DueyPackage.class);
      query.setParameter("receiverId", characterId);
      return getResultList(query, new DueyPackageFromResultSetTransformer());
   }

   public Optional<DueyPackage> getById(EntityManager entityManager, int packageId) {
      TypedQuery<entity.duey.DueyPackage> query = entityManager.createQuery("FROM DueyPackage d WHERE d.packageId = :packageId", entity.duey.DueyPackage.class);
      query.setParameter("packageId", packageId);
      return getSingleOptional(query, new DueyPackageFromResultSetTransformer());
   }

   public List<Integer> getPackagesAfter(EntityManager entityManager, Timestamp timestamp) {
      TypedQuery<Integer> query = entityManager.createQuery("SELECT d.packageId FROM DueyPackage d WHERE d.timestamp < :timestamp", Integer.class);
      query.setParameter("timestamp", timestamp);
      return query.getResultList();
   }
}