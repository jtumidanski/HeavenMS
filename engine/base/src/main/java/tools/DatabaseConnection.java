package tools;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import javax.persistence.EntityManager;

import client.database.provider.AccountProvider;

/**
 * @author Frz - Big Daddy
 * @author The Real Spookster - some modifications to this beautiful code
 * @author Ronan - some connection pool to this beautiful code
 */
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
   }

   public <T> Optional<T> withConnectionResult(Function<EntityManager, T> function) {
      Optional<T> result;
      EntityManager entityManager = PersistenceManager.getInstance();
      result = Optional.ofNullable(function.apply(entityManager));
      return result;
   }

   public <T> Optional<T> withConnectionResultOpt(Function<EntityManager, Optional<T>> function) {
      Optional<T> result;
      EntityManager entityManager = PersistenceManager.getInstance();
      result = function.apply(entityManager);
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

   private long getNumberOfAccounts() {
      return withConnectionResult(connection -> AccountProvider.getInstance().getAccountCount(connection)).orElse(0L);
   }
}
