package client.database.administrator;

import java.sql.Connection;
import java.sql.Timestamp;
import java.util.Calendar;

import client.database.AbstractQueryExecutor;
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

   public void updateGender(Connection connection, int accountId, byte gender) {
      String sql = "UPDATE accounts SET gender = ? WHERE id = ?";
      execute(connection, sql, ps -> {
         ps.setByte(1, gender);
         ps.setInt(2, accountId);
      });
   }

   public void updateSlotCount(Connection connection, int accountId, int count) {
      String sql = "UPDATE accounts SET characterslots = ? WHERE id = ?";
      execute(connection, sql, ps -> {
         ps.setInt(1, count);
         ps.setInt(2, accountId);
      });
   }

   public void updateVotePoints(Connection connection, int accountId, int points) {
      String sql = "UPDATE accounts SET votepoints = ? WHERE id = ?";
      execute(connection, sql, ps -> {
         ps.setInt(1, points);
         ps.setInt(2, accountId);
      });
   }

   public void acceptTos(Connection connection, int accountId) {
      String sql = "UPDATE accounts SET tos = 1 WHERE id = ?";
      execute(connection, sql, ps -> ps.setInt(1, accountId));
   }

   public void setLoggedInStatus(Connection connection, int accountId, int status) {
      String sql = "UPDATE accounts SET loggedin = ?, lastlogin = ? WHERE id = ?";
      execute(connection, sql, ps -> {
         ps.setInt(1, status);
         ps.setTimestamp(2, new Timestamp(Server.getInstance().getCurrentTime()));
         ps.setInt(3, accountId);
      });
   }

   public void setMacs(Connection connection, int accountId, String macData) {
      String sql = "UPDATE accounts SET macs = ? WHERE id = ?";
      execute(connection, sql, ps -> {
         ps.setString(1, macData);
         ps.setInt(2, accountId);
      });
   }

   public void setHwid(Connection connection, int accountId, String hwid) {
      String sql = "UPDATE accounts SET hwid = ? WHERE id = ?";
      execute(connection, sql, ps -> {
         ps.setString(1, hwid);
         ps.setInt(2, accountId);
      });
   }

   public void setPic(Connection connection, int accountId, String pic) {
      String sql = "UPDATE accounts SET pic = ? WHERE id = ?";
      execute(connection, sql, ps -> {
         ps.setString(1, pic);
         ps.setInt(2, accountId);
      });
   }

   public void setPin(Connection connection, int accountId, String pin) {
      String sql = "UPDATE accounts SET pin = ? WHERE id = ?";
      execute(connection, sql, ps -> {
         ps.setString(1, pin);
         ps.setInt(2, accountId);
      });
   }

   public void setPermaBan(Connection connection, int accountId, String reason) {
      String sql = "UPDATE accounts SET banned = 1, banreason = ? WHERE id = ?";
      execute(connection, sql, ps -> {
         ps.setString(1, reason);
         ps.setInt(2, accountId);
      });
   }

   public void removePermaBan(Connection connection, int accountId) {
      String sql = "UPDATE accounts SET banned = -1 WHERE id = ?";
      execute(connection, sql, ps -> ps.setInt(1, accountId));
   }

   public void setBan(Connection connection, int accountId, int reason, int days, String desc) {
      String sql = "UPDATE accounts SET banreason = ?, tempban = ?, greason = ? WHERE id = ?";
      execute(connection, sql, ps -> {
         ps.setString(1, desc);
         Calendar cal = Calendar.getInstance();
         cal.add(Calendar.DATE, days);
         Timestamp timestamp = new Timestamp(cal.getTimeInMillis());
         ps.setTimestamp(2, timestamp);
         ps.setInt(3, reason);
         ps.setInt(4, accountId);
      });
   }

   public void setRewardPoints(Connection connection, int accountId, int value) {
      String sql = "UPDATE accounts SET rewardpoints=? WHERE id=?;";
      execute(connection, sql, ps -> {
         ps.setInt(1, value);
         ps.setInt(2, accountId);
      });
   }

   public void setLanguage(Connection connection, int accountId, int language) {
      String sql = "UPDATE accounts SET language = ? WHERE id = ?";
      execute(connection, sql, ps -> {
         ps.setInt(1, language);
         ps.setInt(2, accountId);
      });
   }

   public void logoutAllAccounts(Connection connection) {
      String sql = "UPDATE accounts SET loggedin = 0";
      executeNoParam(connection, sql);
   }

   public void saveNxInformation(Connection connection, int accountId, int nxCredit, int maplePoints, int nxPrepaid) {
      String sql = "UPDATE `accounts` SET `nxCredit` = ?, `maplePoint` = ?, `nxPrepaid` = ? WHERE `id` = ?";
      execute(connection, sql, ps -> {
         ps.setInt(1, nxCredit);
         ps.setInt(2, maplePoints);
         ps.setInt(3, nxPrepaid);
         ps.setInt(4, accountId);
      });
   }

   public void awardNxPrepaid(Connection connection, int accountId, int amount) {
      String sql = "UPDATE accounts SET nxPrepaid = nxPrepaid + ? WHERE id = ?";
      execute(connection, sql, ps -> {
         ps.setInt(1, amount);
         ps.setInt(2, accountId);
      });
   }

   public int create(Connection connection, String name, String password) {
      String sql = "INSERT INTO accounts (name, password, birthday, tempban) VALUES (?, ?, ?, ?)";
      return insertAndReturnKey(connection, sql, ps -> {
         ps.setString(1, name);
         ps.setString(2, password);
         ps.setString(3, "2018-06-20");
         ps.setString(4, "2018-06-20");
      });
   }

   public void updatePasswordByName(Connection connection, String name, String password) {
      String sql = "UPDATE accounts SET password = ? WHERE name = ?;";
      execute(connection, sql, ps -> {
         ps.setString(1, password);
         ps.setString(2, name);
      });
   }
}
