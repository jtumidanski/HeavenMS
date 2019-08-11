package tools;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import client.database.SQLConsumer;
import client.database.provider.AccountProvider;
import constants.ServerConstants;

/**
 * @author Frz - Big Daddy
 * @author The Real Spookster - some modifications to this beautiful code
 * @author Ronan - some connection pool to this beautiful code
 */
public class DatabaseConnection {
   private static HikariDataSource ds;

   public DatabaseConnection() {
      try {
         Class.forName("com.mysql.cj.jdbc.Driver"); // touch the mysql driver
      } catch (ClassNotFoundException e) {
         System.out.println("[SEVERE] SQL Driver Not Found. Consider death by clams.");
         e.printStackTrace();
      }

      ds = null;

      if (ServerConstants.DB_CONNECTION_POOL) {
         // Connection Pool on database ftw!

         HikariConfig config = new HikariConfig();
         config.setJdbcUrl(ServerConstants.DB_URL);

         config.setUsername(ServerConstants.DB_USER);
         config.setPassword(ServerConstants.DB_PASS);

         // Make sure pool size is comfortable for the worst case scenario.
         // Under 100 accounts? Make it 10. Over 10000 accounts? Make it 30.
         int poolSize = (int) Math.ceil(0.00202020202 * 100 + 9.797979798);
         if (poolSize < 10) {
            poolSize = 10;
         } else if (poolSize > 30) {
            poolSize = 30;
         }

         config.setConnectionTimeout(30 * 1000);
         config.setMaximumPoolSize(poolSize);

         config.addDataSourceProperty("cachePrepStmts", true);
         config.addDataSourceProperty("prepStmtCacheSize", 25);
         config.addDataSourceProperty("prepStmtCacheSqlLimit", 2048);

         ds = new HikariDataSource(config);
      }
   }

   public static Connection getConnection() throws SQLException {
      if (ds == null) {
         new DatabaseConnection();
      }

      Connection connection = null;
      if (ds != null) {
         try {
            connection = ds.getConnection();
         } catch (SQLException sqle) {
            sqle.printStackTrace();
         }
      } else {
         int denies = 0;
         while (true) {   // There is no way it can pass with a null out of here?
            try {
               connection = DriverManager.getConnection(ServerConstants.DB_URL, ServerConstants.DB_USER, ServerConstants.DB_PASS);
               break;
            } catch (SQLException sqle) {
               denies++;

               if (denies == 3) {
                  // Give up, throw exception. Nothing good will come from this.
                  FilePrinter.printError(FilePrinter.SQL_EXCEPTION, "SQL Driver refused to give a connection after " + denies + " tries. Problem: " + sqle.getMessage());
                  throw sqle;
               }
            }
         }
      }

      return connection;
   }

   public static void withConnection(Consumer<Connection> consumer) {
      Connection connection = null;
      try {
         connection = DatabaseConnection.getConnection();
         consumer.accept(connection);
      } catch (SQLException e) {
         e.printStackTrace();
      } finally {
         try {
            if (connection != null) {
               connection.close();
            }
         } catch (SQLException exception) {
            exception.printStackTrace();
         }
      }
   }

   public static void withExplicitCommitConnection(SQLConsumer<Connection> consumer) {
      Connection connection = null;
      try {
         connection = DatabaseConnection.getConnection();
         connection.setAutoCommit(false);

         consumer.accept(connection);

         connection.setAutoCommit(true);
      } catch (SQLException e) {
         e.printStackTrace();
         try {
            if (connection != null) {
               connection.rollback();
               connection.setAutoCommit(true);
            }
         } catch (SQLException exception) {
            exception.printStackTrace();
         }
      } finally {
         try {
            if (connection != null) {
               connection.close();
            }
         } catch (SQLException exception) {
            exception.printStackTrace();
         }
      }
   }

   public static <T> Optional<T> withConnectionResult(Function<Connection, T> function) {
      Connection connection = null;
      Optional<T> result = Optional.empty();
      try {
         connection = DatabaseConnection.getConnection();
         result = Optional.of(function.apply(connection));
      } catch (SQLException e) {
         e.printStackTrace();
      } finally {
         try {
            if (connection != null) {
               connection.close();
            }
         } catch (SQLException exception) {
            exception.printStackTrace();
         }
      }
      return result;
   }

   public static <T> Optional<T> withConnectionResultOpt(Function<Connection, Optional<T>> function) {
      Connection connection = null;
      Optional<T> result = Optional.empty();
      try {
         connection = DatabaseConnection.getConnection();
         result = function.apply(connection);
      } catch (SQLException e) {
         e.printStackTrace();
      } finally {
         try {
            if (connection != null) {
               connection.close();
            }
         } catch (SQLException exception) {
            exception.printStackTrace();
         }
      }
      return result;
   }

   private static long getNumberOfAccounts() {
      return withConnectionResult(connection -> AccountProvider.getInstance().getAccountCount(connection)).orElse(0L);
   }
}
