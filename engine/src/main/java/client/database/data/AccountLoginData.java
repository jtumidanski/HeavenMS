package client.database.data;

import java.sql.Date;
import java.sql.Timestamp;

public class AccountLoginData {
   private int loggedIn;

   private Timestamp lastLogin;

   private Date birthday;

   public AccountLoginData(int loggedIn, Timestamp lastLogin, Date birthday) {
      this.loggedIn = loggedIn;
      this.lastLogin = lastLogin;
      this.birthday = birthday;
   }

   public int getLoggedIn() {
      return loggedIn;
   }

   public Timestamp getLastLogin() {
      return lastLogin;
   }

   public Date getBirthday() {
      return birthday;
   }
}
