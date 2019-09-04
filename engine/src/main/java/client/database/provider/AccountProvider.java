package client.database.provider;

import java.sql.Connection;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

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

   public byte getGReason(Connection connection, int accountId) {
      String sql = "SELECT `greason` FROM `accounts` WHERE id = ?";
      Optional<Byte> result = getSingle(connection, sql, ps -> ps.setInt(1, accountId), "greason");
      return result.orElse(Byte.MIN_VALUE);
   }

   public int getVotePoints(Connection connection, int accountId) {
      String sql = "SELECT `votepoints` FROM accounts WHERE id = ?";
      Optional<Integer> result = getSingle(connection, sql, ps -> ps.setInt(1, accountId), "votepoints");
      return result.orElse(0);
   }

   public boolean getTosStatus(Connection connection, int accountId) {
      String sql = "SELECT `tos` FROM accounts WHERE id = ?";
      Optional<Boolean> result = getSingle(connection, sql, ps -> ps.setInt(1, accountId), "tos");
      return result.orElse(false);
   }

   public AccountLoginData getLoginData(Connection connection, int accountId) {
      String sql = "SELECT loggedin, lastlogin, birthday FROM accounts WHERE id = ?";
      Optional<AccountLoginData> result = get(connection, sql, ps -> ps.setInt(1, accountId), rs -> {
         if (rs != null && rs.next()) {
            return Optional.of(new AccountLoginData(rs.getInt("loggedin"), rs.getTimestamp("lastlogin"), rs.getDate("birthday")));
         }
         return Optional.empty();
      });
      return result.orElse(null);
   }

   public Calendar getTempBanCalendar(Connection connection, int accountId) {
      String sql = "SELECT `tempban` FROM accounts WHERE id = ?";
      Optional<Calendar> result = get(connection, sql, ps -> ps.setInt(1, accountId), rs -> {
         if (rs != null && rs.next()) {
            Calendar tempBan = Calendar.getInstance();
            tempBan.setTimeInMillis(rs.getTimestamp("tempban").getTime());
            return Optional.of(tempBan);
         }
         return Optional.empty();
      });
      return result.orElse(null);
   }

   public Set<String> getMacs(Connection connection, int accountId) {
      String sql = "SELECT macs FROM accounts WHERE id = ?";
      Optional<Set<String>> result = get(connection, sql, ps -> ps.setInt(1, accountId), rs -> {
         if (rs != null && rs.next()) {
            return Optional.of(Arrays.stream(rs.getString("macs").split(", ")).filter(mac -> !mac.equals("")).collect(Collectors.toSet()));
         }
         return Optional.empty();
      });
      return result.orElse(null);
   }

   public String getHwid(Connection connection, int accountId) {
      String sql = "SELECT hwid FROM accounts WHERE id = ?";
      Optional<String> result = getSingle(connection, sql, ps -> ps.setInt(1, accountId), "hwid");
      return result.orElse("");
   }

   public int getRewardPoints(Connection connection, int accountId) {
      String sql = "SELECT rewardpoints FROM accounts WHERE id=?";
      Optional<Integer> result = getSingle(connection, sql, ps -> ps.setInt(1, accountId), 1);
      return result.orElse(-1);
   }

   public Integer getAccountIdForName(Connection connection, String name) {
      String sql = "SELECT id FROM accounts WHERE name = ?";
      Optional<Integer> result = getSingle(connection, sql, ps -> ps.setString(1, name), 1);
      return result.orElse(null);
   }

   public AccountCashShopData getAccountCashShopData(Connection connection, int accountId) {
      String sql = "SELECT `nxCredit`, `maplePoint`, `nxPrepaid` FROM `accounts` WHERE `id` = ?";
      Optional<AccountCashShopData> result = get(connection, sql, ps -> ps.setInt(1, accountId), rs -> {
         if (rs != null && rs.next()) {
            return Optional.of(new AccountCashShopData(rs.getInt("nxCredit"), rs.getInt("maplePoint"), rs.getInt("nxPrepaid")));
         }
         return Optional.empty();
      });
      return result.orElse(null);
   }

   public Optional<AccountData> getAccountDataByName(Connection connection, String name) {
      String sql = "SELECT id, password, gender, banned, pin, pic, characterslots, tos, language FROM accounts WHERE name = ?";
      return get(connection, sql, ps -> ps.setString(1, name), rs -> {
         if (rs != null && rs.next()) {
            return Optional.of(new AccountData(rs.getInt("id"), name, rs.getString("password"), rs.getByte("gender"),
                  rs.getByte("banned") == 1, rs.getString("pin"), rs.getString("pic"), rs.getByte("characterslots"),
                  rs.getByte("tos"), rs.getInt("language")));
         }
         return Optional.empty();
      });
   }

   public long getAccountCount(Connection connection) {
      String sql = "SELECT count(*) FROM accounts";
      Optional<Long> result = getSingle(connection, sql, 1);
      return result.orElse(0L);
   }

   public Optional<AccountData> getAccountDataById(Connection connection, int accountId) {
      String sql = "SELECT name, password, gender, banned, pin, pic, characterslots, tos, language FROM accounts WHERE id = ?";
      return get(connection, sql, ps -> ps.setInt(1, accountId), rs -> {
         if (rs != null && rs.next()) {
            return Optional.of(new AccountData(accountId, rs.getString("name"), rs.getString("password"), rs.getByte("gender"),
                  rs.getByte("banned") == 1, rs.getString("pin"), rs.getString("pic"), rs.getByte("characterslots"),
                  rs.getByte("tos"), rs.getInt("language")));
         }
         return Optional.empty();
      });
   }

   public List<Integer> getAllAccountIds(Connection connection) {
      String sql = "SELECT id FROM accounts";
      return getListNew(connection, sql, ps -> {
      }, rs -> rs.getInt("id"));
   }
}
