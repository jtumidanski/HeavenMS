package database.provider;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import database.AbstractQueryExecutor;
import client.database.utility.ReactorDropTransformer;
import entity.ReactorDrop;
import server.maps.ReactorDropEntry;

public class ReactorDropProvider extends AbstractQueryExecutor {
   private static ReactorDropProvider instance;

   public static ReactorDropProvider getInstance() {
      if (instance == null) {
         instance = new ReactorDropProvider();
      }
      return instance;
   }

   private ReactorDropProvider() {
   }

   public List<Integer> getDropIds(EntityManager entityManager, int minimumId, int maximumId) {
      TypedQuery<Integer> query = entityManager.createQuery("SELECT r.itemId FROM ReactorDrop r WHERE r.itemId >= :minimum AND r.itemId < :maximum", Integer.class);
      query.setParameter("minimum", minimumId);
      query.setParameter("maximum", maximumId);
      return query.getResultList();
   }

   public List<ReactorDropEntry> getDropsForReactor(EntityManager entityManager, int reactorId) {
      TypedQuery<ReactorDrop> query = entityManager.createQuery("FROM ReactorDrop r WHERE r.reactorId = :reactorId AND r.chance >= 0", ReactorDrop.class);
      query.setParameter("reactorId", reactorId);
      return getResultList(query, new ReactorDropTransformer());
   }
}