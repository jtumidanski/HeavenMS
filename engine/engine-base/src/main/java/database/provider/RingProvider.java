package database.provider;

import java.util.List;
import java.util.Optional;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import accessor.AbstractQueryExecutor;
import client.Ring;
import database.transformer.RingTransformer;

public class RingProvider extends AbstractQueryExecutor {
   private static RingProvider instance;

   public static RingProvider getInstance() {
      if (instance == null) {
         instance = new RingProvider();
      }
      return instance;
   }

   private RingProvider() {
   }

   public Optional<Ring> getRingById(EntityManager entityManager, int ringId) {
      TypedQuery<entity.Ring> query = entityManager.createQuery("FROM Ring r WHERE r.id = :id", entity.Ring.class);
      query.setParameter("id", ringId);
      return getSingleOptional(query, new RingTransformer());
   }

   public List<Integer> getAll(EntityManager entityManager) {
      TypedQuery<Integer> query = entityManager.createQuery("SELECT r.id FROM Ring r", Integer.class);
      return query.getResultList();
   }
}