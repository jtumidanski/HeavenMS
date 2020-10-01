package database.transformer;

import client.database.data.AccountData;
import entity.Account;
import transformer.SqlTransformer;

public class AccountDataTransformer implements SqlTransformer<AccountData, Account> {
   @Override
   public AccountData transform(Account account) {
      return new AccountData(account.getId(),
            account.getName(),
            account.getPassword(),
            account.getGender(),
            account.getBanned(),
            account.getPin(),
            account.getPic(),
            account.getCharacterSlots(),
            account.getTos(),
            account.getLanguage(),
            account.getCountry());
   }
}
