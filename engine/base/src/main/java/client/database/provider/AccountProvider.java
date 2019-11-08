package client.database.provider;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import client.database.AbstractQueryExecutor;
import client.database.data.AccountCashShopData;
import client.database.data.AccountData;
import client.database.data.AccountLoginData;

public class AccountProvider extends AbstractQueryExecutor {
   private static AccountProvider instance;

   public static AccountProvider getInstance() {
      if (instance == null) {
         instance = new AccountProvider();
      }
      return instance;
   }

   private AccountProvider() {
   }

   public byte getGReason(EntityManager entityManager, int accountId) {
      TypedQuery<Integer> query = entityManager.createQuery("SELECT a.gReason FROM Account a WHERE a.id = :id", Integer.class);
      query.setParameter("id", accountId);
      return query.getSingleResult().byteValue();
   }

   public int getVotePoints(EntityManager entityManager, int accountId) {
      TypedQuery<Integer> query = entityManager.createQuery("SELECT a.votePoints FROM Account a WHERE a.id = :id", Integer.class);
      query.setParameter("id", accountId);
      return query.getSingleResult();
   }

   public boolean getTosStatus(EntityManager entityManager, int accountId) {
      TypedQuery<Boolean> query = entityManager.createQuery("SELECT a.tos FROM Account a WHERE a.id = :id", Boolean.class);
      query.setParameter("id", accountId);
      return query.getSingleResult();
   }

   public AccountLoginData getLoginData(EntityManager entityManager, int accountId) {
      TypedQuery<AccountLoginData> query = entityManager.createQuery("SELECT NEW client.database.data.AccountLoginData(a.loggedIn, a.lastLogin, a.birthday) FROM Account a WHERE a.id = :id", AccountLoginData.class);
      query.setParameter("id", accountId);
      return query.getSingleResult();
   }

   public Calendar getTempBanCalendar(EntityManager entityManager, int accountId) {
      TypedQuery<Timestamp> query = entityManager.createQuery("SELECT a.tempBan FROM Account a WHERE a.id = :id", Timestamp.class);
      query.setParameter("id", accountId);
      Calendar tempBan = Calendar.getInstance();
      tempBan.setTimeInMillis(query.getSingleResult().getTime());
      return tempBan;
   }

   public Set<String> getMacs(EntityManager entityManager, int accountId) {
      TypedQuery<String> query = entityManager.createQuery("SELECT a.macs FROM Account a WHERE a.id = :id", String.class);
      query.setParameter("id", accountId);
      String macs = query.getSingleResult();
      return Arrays.stream(macs.split(", ")).filter(mac -> !mac.equals("")).collect(Collectors.toSet());
   }

   public String getHwid(EntityManager entityManager, int accountId) {
      TypedQuery<String> query = entityManager.createQuery("SELECT a.hwid FROM Account a WHERE a.id = :id", String.class);
      query.setParameter("id", accountId);
      return query.getSingleResult();
   }

   public int getRewardPoints(EntityManager entityManager, int accountId) {
      TypedQuery<Integer> query = entityManager.createQuery("SELECT a.rewardPoints FROM Account a WHERE a.id = :id", Integer.class);
      query.setParameter("id", accountId);
      return query.getSingleResult();
   }

   public Integer getAccountIdForName(EntityManager entityManager, String name) {
      TypedQuery<Integer> query = entityManager.createQuery("SELECT a.id FROM Account a WHERE a.name = :name", Integer.class);
      query.setParameter("name", name);
      return query.getSingleResult();
   }

   public AccountCashShopData getAccountCashShopData(EntityManager entityManager, int accountId) {
      TypedQuery<AccountCashShopData> query = entityManager.createQuery("SELECT NEW client.database.data.AccountCashShopData(a.nxCredit, a.maplePoint, a.nxPrepaid) FROM Account a WHERE a.id = :id", AccountCashShopData.class);
      query.setParameter("id", accountId);
      return query.getSingleResult();
   }

   public Optional<AccountData> getAccountDataByName(EntityManager entityManager, String name) {
      TypedQuery<AccountData> query = entityManager.createQuery("SELECT NEW client.database.data.AccountData(a.id, a.name, a.password, a.gender, a.banned, a.pin, a.pic, a.characterSlots, a.tos, a.language) FROM Account a WHERE a.name = :name", AccountData.class);
      query.setParameter("name", name);
      return getSingleOptional(query);
   }

   public long getAccountCount(EntityManager entityManager) {
      TypedQuery<Long> query = entityManager.createQuery("SELECT COUNT(*) FROM Account", Long.class);
      return query.getSingleResult();
   }

   public Optional<AccountData> getAccountDataById(EntityManager entityManager, int accountId) {
      TypedQuery<AccountData> query = entityManager.createQuery("SELECT NEW client.database.data.AccountData(a.id, a.name, a.password, a.gender, a.banned, a.pin, a.pic, a.characterSlots, a.tos, a.language) FROM Account a WHERE a.id = :id", AccountData.class);
      query.setParameter("id", accountId);
      return getSingleOptional(query);
   }

   public List<Integer> getAllAccountIds(EntityManager entityManager) {
      TypedQuery<Integer> query = entityManager.createQuery("SELECT a.id FROM Account a", Integer.class);
      return query.getResultList();
   }
}
