package database;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class PersistenceManager {

   private static ThreadLocal<EntityManager> instance = ThreadLocal.withInitial(() -> {
      EntityManagerFactory factory = Persistence.createEntityManagerFactory("ms-buddy");
      return factory.createEntityManager();
   });

   private PersistenceManager() {
      super();
      try {
         Class.forName("com.mysql.cj.jdbc.Driver"); // touch the mysql driver
      } catch (ClassNotFoundException e) {
         System.out.println("[SEVERE] SQL Driver Not Found. Consider death by clams.");
         e.printStackTrace();
      }
   }

   public static synchronized EntityManager getInstance() {
      return instance.get();
   }
}