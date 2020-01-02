package database.administrator;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.function.Consumer;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import database.AbstractQueryExecutor;
import entity.Account;
import net.server.Server;

public class AccountAdministrator extends AbstractQueryExecutor {
   private static AccountAdministrator instance;

   public static AccountAdministrator getInstance() {
      if (instance == null) {
         instance = new AccountAdministrator();
      }
      return instance;
   }

   private AccountAdministrator() {
   }

   protected void update(EntityManager entityManager, int id, Consumer<Account> consumer) {
      super.update(entityManager, Account.class, id, consumer);
   }

   public void updateGender(EntityManager entityManager, int accountId, byte gender) {
      update(entityManager, accountId, account -> account.setGender((int) gender));
   }

   public void updateSlotCount(EntityManager entityManager, int accountId, int count) {
      update(entityManager, accountId, account -> account.setCharacterSlots(count));
   }

   public void updateVotePoints(EntityManager entityManager, int accountId, int points) {
      update(entityManager, accountId, account -> account.setVotePoints(points));
   }

   public void acceptTos(EntityManager entityManager, int accountId) {
      update(entityManager, accountId, account -> account.setTos(true));
   }

   public void setLoggedInStatus(EntityManager entityManager, int accountId, int status) {
      update(entityManager, accountId, account -> {
         account.setLoggedIn(status);
         account.setLastLogin(new Timestamp(Server.getInstance().getCurrentTime()));
      });
   }

   public void setMacs(EntityManager entityManager, int accountId, String macData) {
      update(entityManager, accountId, account -> account.setMacs(macData));
   }

   public void setHwid(EntityManager entityManager, int accountId, String hwid) {
      update(entityManager, accountId, account -> account.setHwid(hwid));
   }

   public void setPic(EntityManager entityManager, int accountId, String pic) {
      update(entityManager, accountId, account -> account.setPic(pic));
   }

   public void setPin(EntityManager entityManager, int accountId, String pin) {
      update(entityManager, accountId, account -> account.setPin(pin));
   }

   public void setPermanentBan(EntityManager entityManager, int accountId, String reason) {
      update(entityManager, accountId, account -> {
         account.setBanned(true);
         account.setBanReason(reason);
      });
   }

   public void removePermanentBan(EntityManager entityManager, int accountId) {
      update(entityManager, accountId, account -> account.setBanned(false));
   }

   public void setBan(EntityManager entityManager, int accountId, int reason, int days, String desc) {
      Calendar cal = Calendar.getInstance();
      cal.add(Calendar.DATE, days);
      Timestamp timestamp = new Timestamp(cal.getTimeInMillis());
      update(entityManager, accountId, account -> {
         account.setBanReason(desc);
         account.setTempBan(timestamp);
         account.setgReason(reason);
      });
   }

   public void setRewardPoints(EntityManager entityManager, int accountId, int value) {
      update(entityManager, accountId, account -> account.setRewardPoints(value));
   }

   public void setLanguage(EntityManager entityManager, int accountId, int language) {
      update(entityManager, accountId, account -> account.setLanguage(language));
   }

   public void logoutAllAccounts(EntityManager entityManager) {
      Query query = entityManager.createQuery("UPDATE Account SET loggedIn = 0");
      execute(entityManager, query);
   }

   public void saveNxInformation(EntityManager entityManager, int accountId, int nxCredit, int maplePoints, int nxPrepaid) {
      update(entityManager, accountId, account -> {
         account.setNxCredit(nxCredit);
         account.setMaplePoint(maplePoints);
         account.setNxPrepaid(nxPrepaid);
      });
   }

   public void awardNxPrepaid(EntityManager entityManager, int accountId, int amount) {
      Query query = entityManager.createQuery("UPDATE Account SET nxPrepaid = nxPrepaid + :value WHERE id = :id");
      query.setParameter("value", amount);
      query.setParameter("id", accountId);
      execute(entityManager, query);
   }

   public int create(EntityManager entityManager, String name, String password) {
      Account account = new Account();
      account.setName(name);
      account.setPassword(password);
      account.setBirthday(new Date(0));
      account.setTempBan(new Timestamp(0));
      insert(entityManager, account);
      return account.getId();
   }

   public void updatePasswordByName(EntityManager entityManager, String name, String password) {
      Query query = entityManager.createQuery("UPDATE Account SET password = :password WHERE name = :name");
      query.setParameter("password", password);
      query.setParameter("name", name);
      execute(entityManager, query);
   }
}
