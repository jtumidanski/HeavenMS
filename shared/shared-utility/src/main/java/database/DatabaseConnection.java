package database;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import javax.persistence.EntityManager;

public class DatabaseConnection {

   private static DatabaseConnection ourInstance = new DatabaseConnection();

   public static DatabaseConnection getInstance() {
      return ourInstance;
   }

   private DatabaseConnection() {
      try {
         Class.forName("com.mysql.cj.jdbc.Driver"); // touch the mysql driver
      } catch (ClassNotFoundException e) {
         System.out.println("[SEVERE] SQL Driver Not Found. Consider death by clams.");
         e.printStackTrace();
      }
   }

   public void withConnection(Consumer<EntityManager> consumer) {
      EntityManager entityManager = PersistenceManager.getInstance();
      consumer.accept(entityManager);
      entityManager.close();
   }

   public <T> Optional<T> withConnectionResult(Function<EntityManager, T> function) {
      Optional<T> result;
      EntityManager entityManager = PersistenceManager.getInstance();
      result = Optional.ofNullable(function.apply(entityManager));
      entityManager.close();
      return result;
   }

   public <T> Optional<T> withConnectionResultOpt(Function<EntityManager, Optional<T>> function) {
      Optional<T> result;
      EntityManager entityManager = PersistenceManager.getInstance();
      result = function.apply(entityManager);
      entityManager.close();
      return result;
   }

   public void thing(EntityManager entityManager, Consumer<EntityManager> consumer) {
      boolean newTransaction = false;
      if (!entityManager.getTransaction().isActive()) {
         newTransaction = true;
         entityManager.getTransaction().begin();
      }

      consumer.accept(entityManager);

      if (newTransaction) {
         entityManager.getTransaction().commit();
      }
   }
}
