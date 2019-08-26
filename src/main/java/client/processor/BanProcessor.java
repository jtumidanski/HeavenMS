package client.processor;

import java.util.Optional;

import client.database.administrator.AccountAdministrator;
import client.database.provider.AccountProvider;
import client.database.provider.CharacterProvider;
import tools.DatabaseConnection;

public class BanProcessor {
   private static BanProcessor ourInstance = new BanProcessor();

   public static BanProcessor getInstance() {
      return ourInstance;
   }

   private AccountProvider accountProvider;

   private CharacterProvider characterProvider;

   private AccountAdministrator accountAdministrator;

   private DatabaseConnection databaseConnection;

   protected BanProcessor() {
      this.databaseConnection = DatabaseConnection.getInstance();
      this.accountProvider = AccountProvider.getInstance();
      this.characterProvider = CharacterProvider.getInstance();
      this.accountAdministrator = AccountAdministrator.getInstance();
   }

   protected BanProcessor(DatabaseConnection databaseConnection,
                          AccountProvider accountProvider,
                          CharacterProvider characterProvider,
                          AccountAdministrator accountAdministrator) {
      this.databaseConnection = databaseConnection;
      this.accountProvider = accountProvider;
      this.characterProvider = characterProvider;
      this.accountAdministrator = accountAdministrator;
   }

   public boolean ban(String id, String reason, boolean accountId) {
      Optional<Integer> theAccountId;
      if (accountId) {
         theAccountId = databaseConnection.withConnectionResult(connection -> accountProvider.getAccountIdForName(connection, id));
      } else {
         theAccountId = databaseConnection.withConnectionResult(connection -> characterProvider.getAccountIdForName(connection, id));
      }

      if (theAccountId.isPresent()) {
         databaseConnection.withConnection(connection -> accountAdministrator.setPermaBan(connection, theAccountId.get(), reason));
         return true;
      }
      return false;
   }
}
