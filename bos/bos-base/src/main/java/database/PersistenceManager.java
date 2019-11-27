package database;

import javax.persistence.EntityManager;
import javax.persistence.Persistence;

public class PersistenceManager {
   private static EntityManager instance = Persistence.createEntityManagerFactory("ms-buddy").createEntityManager();

   private PersistenceManager() {
      super();
   }

   public static synchronized EntityManager getInstance() {
      return instance;
   }
}