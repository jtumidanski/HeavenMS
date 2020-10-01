package database.provider;

import java.util.Optional;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

import accessor.AbstractQueryExecutor;
import client.database.data.AllianceData;
import database.transformer.AllianceTransformer;
import entity.Alliance;

public class AllianceProvider extends AbstractQueryExecutor {
   private static AllianceProvider instance;

   public static AllianceProvider getInstance() {
      if (instance == null) {
         instance = new AllianceProvider();
      }
      return instance;
   }

   private AllianceProvider() {
   }

   public boolean allianceExists(EntityManager entityManager, String allianceName) {
      TypedQuery<Integer> query = entityManager.createQuery("SELECT 1 FROM Alliance a WHERE a.name = :name", Integer.class);
      query.setParameter("name", allianceName);
      try {
         query.getSingleResult();
         return true;
      } catch (NoResultException exception) {
         return false;
      }
   }

   public Optional<AllianceData> getAllianceData(EntityManager entityManager, int allianceId) {
      TypedQuery<Alliance> query = entityManager.createQuery("FROM Alliance a WHERE a.id = :id", Alliance.class);
      query.setParameter("id", allianceId);
      return getSingleOptional(query, new AllianceTransformer());
   }
}