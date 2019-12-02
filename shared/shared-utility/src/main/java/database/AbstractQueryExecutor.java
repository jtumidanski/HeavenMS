package database;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

public abstract class AbstractQueryExecutor {
   protected <T> T getSingleWithDefault(TypedQuery<T> query, T defaultValue) {
      try {
         return query.getSingleResult();
      } catch (NoResultException exception) {
         return defaultValue;
      }
   }

   protected <T, U> T getSingleWithDefault(TypedQuery<U> query, SqlTransformer<T, U> transformer, T defaultValue) {
      try {
         U result = query.getSingleResult();
         return transformer.transform(result);
      } catch (NoResultException exception) {
         return defaultValue;
      }
   }

   protected <T> Optional<T> getSingleOptional(TypedQuery<T> query) {
      try {
         return Optional.of(query.getSingleResult());
      } catch (NoResultException exception) {
         return Optional.empty();
      }
   }

   protected <T, U> Optional<T> getSingleOptional(TypedQuery<U> query, SqlTransformer<T, U> transformer) {
      try {
         U result = query.getSingleResult();
         return Optional.of(transformer.transform(result));
      } catch (NoResultException exception) {
         return Optional.empty();
      }
   }

   protected <T, U> List<T> getResultList(TypedQuery<U> query, SqlTransformer<T, U> transformer) {
      return query.getResultList().parallelStream().map(transformer::transform).collect(Collectors.toList());
   }

   protected boolean resultExists(Query query) {
      try {
         query.getSingleResult();
         return true;
      } catch (NoResultException exception) {
         return false;
      }
   }

   protected <T> void update(EntityManager entityManager, Class<T> clazz, int id, Consumer<T> consumer) {
      T thing = entityManager.find(clazz, id);
      DatabaseConnection.getInstance().thing(entityManager, em -> consumer.accept(thing));
   }

   protected void execute(EntityManager entityManager, Query query) {
      DatabaseConnection.getInstance().thing(entityManager, em -> query.executeUpdate());
   }

   protected void insert(EntityManager entityManager, Object object) {
      DatabaseConnection.getInstance().thing(entityManager, em -> em.persist(object));
   }

   protected void insertBulk(EntityManager entityManager, List<?> objects) {
      DatabaseConnection.getInstance().thing(entityManager, em -> objects.forEach(em::persist));
   }
}
