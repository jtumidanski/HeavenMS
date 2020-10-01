package database.transformer;

import client.database.data.AccountLoginData;
import entity.Account;
import transformer.SqlTransformer;

public class AccountLoginDataTransformer implements SqlTransformer<AccountLoginData, Account> {
   @Override
   public AccountLoginData transform(Account account) {
      return new AccountLoginData(account.getLoggedIn(), account.getLastLogin(), account.getBirthday());
   }
}
