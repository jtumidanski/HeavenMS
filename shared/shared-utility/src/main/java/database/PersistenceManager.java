package database;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.PersistenceException;

public class PersistenceManager {
   private static EntityManagerFactory instance;

   private PersistenceManager() {
      super();
   }

   public static void construct(String persistenceName) {
      instance = Persistence.createEntityManagerFactory(persistenceName);
   }

   public static synchronized EntityManager getInstance() {
      if (instance == null) {
         throw new PersistenceException("No primary persistence provider created. Call PersistenceManager.construct prior to getInstance.");
      }
      return instance.createEntityManager();
   }
}