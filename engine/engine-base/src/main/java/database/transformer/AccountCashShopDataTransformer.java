package database.transformer;

import client.database.data.AccountCashShopData;
import entity.Account;
import transformer.SqlTransformer;

public class AccountCashShopDataTransformer implements SqlTransformer<AccountCashShopData, Account> {
   @Override
   public AccountCashShopData transform(Account account) {
      return new AccountCashShopData(account.getNxCredit(), account.getMaplePoint(), account.getNxPrepaid());
   }
}
