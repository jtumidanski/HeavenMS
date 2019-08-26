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

   private BanProcessor() {
   }

   public boolean ban(String id, String reason, boolean accountId) {
      Optional<Integer> theAccountId;
      if (accountId) {
         theAccountId = DatabaseConnection.withConnectionResult(connection -> AccountProvider.getInstance().getAccountIdForName(connection, id));
      } else {
         theAccountId = DatabaseConnection.withConnectionResult(connection -> CharacterProvider.getInstance().getAccountIdForName(connection, id));
      }

      if (theAccountId.isPresent()) {
         DatabaseConnection.withConnection(connection -> AccountAdministrator.getInstance().setPermaBan(connection, theAccountId.get(), reason));
         return true;
      }
      return false;
   }
}
