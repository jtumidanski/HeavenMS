package client.processor;

import java.util.Optional;

import database.administrator.AccountAdministrator;
import database.provider.AccountProvider;
import database.provider.CharacterProvider;
import database.DatabaseConnection;

public class BanProcessor {
   private static BanProcessor ourInstance = new BanProcessor();

   public static BanProcessor getInstance() {
      return ourInstance;
   }

   protected BanProcessor() {
   }

   public boolean ban(String id, String reason, boolean accountId) {
      Optional<Integer> theAccountId;
      if (accountId) {
         theAccountId = DatabaseConnection.getInstance().withConnectionResult(connection -> AccountProvider.getInstance().getAccountIdForName(connection, id));
      } else {
         theAccountId = DatabaseConnection.getInstance().withConnectionResult(connection -> CharacterProvider.getInstance().getAccountIdForName(connection, id));
      }

      if (theAccountId.isPresent()) {
         DatabaseConnection.getInstance().withConnection(connection -> AccountAdministrator.getInstance().setPermanentBan(connection, theAccountId.get(), reason));
         return true;
      }
      return false;
   }
}
