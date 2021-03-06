package database.provider;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import accessor.AbstractQueryExecutor;
import client.database.data.AccountCashShopData;
import client.database.data.AccountData;
import client.database.data.AccountLoginData;
import database.transformer.AccountCashShopDataTransformer;
import database.transformer.AccountDataTransformer;
import database.transformer.AccountLoginDataTransformer;
import entity.Account;

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

   public Optional<AccountLoginData> getLoginData(EntityManager entityManager, int accountId) {
      TypedQuery<Account> query = entityManager.createQuery("SELECT a FROM Account a WHERE a.id = :id", Account.class);
      query.setParameter("id", accountId);
      return getSingleOptional(query, new AccountLoginDataTransformer());
   }

   public Calendar getTempBanCalendar(EntityManager entityManager, int accountId) {
      TypedQuery<Date> query = entityManager.createQuery("SELECT a.tempBan FROM Account a WHERE a.id = :id", Date.class);
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

   public Optional<AccountCashShopData> getAccountCashShopData(EntityManager entityManager, int accountId) {
      TypedQuery<Account> query = entityManager.createQuery("SELECT a FROM Account a WHERE a.id = :id", Account.class);
      query.setParameter("id", accountId);
      return getSingleOptional(query, new AccountCashShopDataTransformer());
   }

   public Optional<AccountData> getAccountDataByName(EntityManager entityManager, String name) {
      TypedQuery<Account> query = entityManager.createQuery("SELECT a FROM Account a WHERE a.name = :name", Account.class);
      query.setParameter("name", name);
      return getSingleOptional(query, new AccountDataTransformer());
   }

   public long getAccountCount(EntityManager entityManager) {
      TypedQuery<Long> query = entityManager.createQuery("SELECT COUNT(*) FROM Account", Long.class);
      return query.getSingleResult();
   }

   public Optional<AccountData> getAccountDataById(EntityManager entityManager, int accountId) {
      TypedQuery<Account> query = entityManager.createQuery("SELECT a FROM Account a WHERE a.id = :id", Account.class);
      query.setParameter("id", accountId);
      return getSingleOptional(query, new AccountDataTransformer());
   }

   public List<Integer> getAllAccountIds(EntityManager entityManager) {
      TypedQuery<Integer> query = entityManager.createQuery("SELECT a.id FROM Account a", Integer.class);
      return query.getResultList();
   }
}
