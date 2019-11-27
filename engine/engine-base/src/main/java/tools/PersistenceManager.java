package tools;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class PersistenceManager {
   private static ThreadLocal<EntityManager> instance = ThreadLocal.withInitial(() -> {
      EntityManagerFactory factory = Persistence.createEntityManagerFactory("jpa-example");
      return factory.createEntityManager();
   });

   private PersistenceManager() {
      super();
   }

   public static synchronized EntityManager getInstance() {
      return instance.get();
   }
}